package de.martin.taf;

import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.net.URL;

/**
 * A sample application to demonstrate the need for the OCI "fat" client when
 * using Transparent Application Failover
 *
 * Originally this code made its first appearance in the RAC book, but it is now
 * heavily updated for Oracle 19c RAC.
 * 
 * Refer to https://martincarstenbach.wordpress.com/2020/08/18/jdbc-the-oracle-database-if-you-want-transparent-application-failover-you-need-the-oci-driver/
 * for more information, especially the configuration of the wallet and the RAC service definition.
 * 
 */

public class TAFDemo {

    private OracleDataSource ods;

    private String jdbcDriver = null;
    private int maxIterations = 100;

    /**
     * Initialise properties and set connection strings
     * 
     * @throws Exception if something goes wrong, we need to abort here
     */
    public TAFDemo(String driver) throws Exception {

        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("app.properties");
        properties.load(url.openStream());

        this.ods = new OracleDataSource();

        switch (driver) {
            case "thin": 
            System.out.println("Trying to connect to the database using the THIN driver");
            System.out.println("Connection string: " + properties.getProperty("conf.thinDriverConnectionString"));
            ods.setURL(properties.getProperty("conf.thinDriverConnectionString"));
            break;

            case "oci":
            System.out.println("Trying to connect to the database using the OCI driver");
            System.out.println("Connection string: " + properties.getProperty("conf.ociDriverConnectionString"));
            ods.setURL(properties.getProperty("conf.ociDriverConnectionString"));
            break;
            
            default:
                throw new IllegalArgumentException("driver must be either 'thin' or 'oci'");
        }
    }

    /**
     * Print metadata about the JDBC connection that's been established. This is probably not
     * the most elegant way of doing this.
     */
    public void printJDBCmetadata() {
        try {
            OracleConnection connection = (OracleConnection) ods.getConnection();

            /* get the JDBC driver name and version */
            DatabaseMetaData dbmd = connection.getMetaData();
            System.out.println("Driver Name:          " + dbmd.getDriverName());
            System.out.println("Driver Version:       " + dbmd.getDriverVersion());
            System.out.println("Database Username is: " + connection.getUserName());
            System.out.println();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setting client info and module/action
     * @param conn the database connection these settings apply to
     */
    private void setConnectionMetadata(OracleConnection conn) {
        try {
            conn.setClientInfo("OCSID.CLIENTID", conn.getUserName());
            conn.setClientInfo("OCSID.MODULE", "TAF Demo");
            conn.setClientInfo("OCSID.ACTION", this.jdbcDriver);
        } catch (Exception e2) {

            System.out.println("Error: cannot set module/action, ignoring ...");
        } 
    }

    /**
     * This is where the work is done ...
     * 
     * The code in this function connects to the database using an external password
     * store (aka wallet) created as per the blog post's instructions.
     * 
     * The main loop consists of constantly querying v$session for TAF related
     * information. Killing an instance should cause the code execution featuring the 
     * thin driver to abort.
     * 
     * If you use the OCI driver, you should see your session re-connecting to the
     * other RAC instance.
     */

    public void doSomeWork() {

        System.out.println("Let's do some work.\n");

        /* get a connection to the database, should this fail, exit the function */
        OracleConnection connection = null;
        try {
            connection = (OracleConnection) ods.getConnection();
        } catch (SQLException e1) {

            System.out.println("Cannot get a connection to the database, aborting");
            return;
        }

        /* it is important to always tell the Oracle database what we are up to */
        setConnectionMetadata(connection);

        /* this is where the actual work starts */
        String query = 
            "select inst_id, sid, failover_type, failover_method, failed_over, module, action " +
            "from gv$session where sid = sys_context('userenv','sid') and username = ?";

        /*
         * prepare the SQL statement and run it. If you created the service with failover type SESSION
         * you need to catch ora-25408 yourself. This is not necessary if you chose failover type
         * SELECT
         */
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, connection.getUserName());

            /* query the database continuously for maxIterations */
            ResultSet rs = null;
            int prevInstance = -1;

            for (int i = 0; i < maxIterations; i++) {

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    int currentInstance = rs.getInt("inst_id");

                    System.out.println("Connected to instance# " + currentInstance
                            + " sid " + rs.getString("sid")
                            + " failover type: " + rs.getString("failover_type")
                            + " failover method: " + rs.getString("failover_method")
                            + " failed over: " + rs.getString("failed_over")
                            + " module: " + rs.getString("module")
                            + " action: " + rs.getString("action"));

                    /* this code block is executed yet not reflected in the query output. */
                    if (prevInstance != currentInstance && prevInstance != -1) {
                        System.out.println(
                                "A failover must have occurrent. Trying to set client info, module and action again");
                        setConnectionMetadata(connection);
                    }
                    prevInstance = rs.getInt("inst_id");
                }

                /* sleep for 5 seconds */
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) { }
            }

            /* we are done - clean up and leave */
            pstmt.close();
            rs.close();
            connection.close();
        } catch (SQLException e4) {

            System.out.println("Something unexpected happened: " + e4.toString());
            e4.printStackTrace();
        }
    }
}