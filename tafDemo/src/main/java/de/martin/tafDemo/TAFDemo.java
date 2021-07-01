package de.martin.tafDemo;

import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;

/**
 * A sample application to demonstrate the need for the OCI "fat" client when using Transparent
 * Application Failover
 *
 * Originally this code made its first appearance in the RAC book, but it is now heavily updated
 * for Oracle 19c.
 * 
 * See https://martincarstenbach.wordpress.com/2020/08/18/jdbc-the-oracle-database-if-you-want-transparent-application-failover-you-need-the-oci-driver/
 * for more information, especially the configuration of the wallet and the service definition.
 * 
 */

public class TAFDemo 
{
    final static String thinDriverConnectionString = "jdbc:oracle:thin:/@swingbench1_taf?TNS_ADMIN=/home/martin/tns";
    final static String ociDriverConnectionString = "jdbc:oracle:oci:/@swingbench1_taf";
    private String jdbcDriver = null;
    final static int maxIterations = 100;
    private OracleDataSource ods;
    
    /**
     * Initialise the data source
     * 
     * @param driver The driver type to choose from: oci or thin
     */
    public TAFDemo(String driver) {

        try {
            OracleDataSource myODS = new OracleDataSource();
            
            if (driver == "thin") {
                System.out.println("Trying to connect to the database using the THIN driver");
            
                myODS.setURL(thinDriverConnectionString);
            } else {
                System.out.println("Trying to connect to the database using the OCI driver");
                myODS.setURL(ociDriverConnectionString);
            }

            this.jdbcDriver = driver;
            this.ods = myODS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print metadata about the JDBC connection that's been established. This is not the most
     * elegant way of doing this.
     */
    public void printJDBCmetadata() {
        try {
            OracleConnection connection = (OracleConnection) ods.getConnection();
            
            /* get the JDBC driver name and version  */
            DatabaseMetaData dbmd = connection.getMetaData();       
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());
            System.out.println("Database Username is: " + connection.getUserName());
            System.out.println();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is where the work is done ... 
     * 
     * The code in this function connects to the database using an external password store (aka wallet)
     * created as per the blog post's instructions.
     * 
     * The main loop consists of constantly querying v$session for TAF related information. Killing an instance
     * should cause the code execution featuring the thin driver to abort.
     * 
     * If you use the OCI driver, you should see your session re-connecting to the other RAC instance.
     * 
     */
    public void doSomeWork() {

        System.out.println("Let's do some work.\n");

        /* get a connection to the database */
        OracleConnection connection = null;
        try {
            connection = (OracleConnection) ods.getConnection();
        } catch (SQLException e1) {

            System.out.println("Cannot get a connection to the database, aborting");
            return;
        } 

        /* it is important to always tell the Oracle database what we are up to */
        try {
            connection.setClientInfo("OCSID.CLIENTID","MARTIN");
            connection.setClientInfo("OCSID.MODULE", "TAF Demo");
            connection.setClientInfo("OCSID.ACTION", this.jdbcDriver);
        } catch (SQLClientInfoException e2) {

            System.out.println("Error: cannot set module/action, ignoring ...");
        }


        /* this is where the actual work starts */
        String query = "select inst_id, sid, failover_type, failover_method, failed_over, module, action " +
        "from gv$session where sid = sys_context('userenv','sid') and username = 'MARTIN'";

        /* 
            prepare the SQL statement and run it. If you created the service with failover type SESSION you need
            to catch ora-25408 yourself. This is not necessary if you chose failover type SELECT
        */
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);

            /* query the database continuously for maxIterations */
            ResultSet rs = null;
            int prevInstance = -1;

            for (int i = 0; i < maxIterations; i++) {

                rs = pstmt.executeQuery();
            
                while (rs.next()) {
                    System.out.println("Connected to instance# " + rs.getString("inst_id")
                        + " sid " + rs.getString("sid") 
                        + " failover type: " + rs.getString("failover_type") 
                        + " failover method: " + rs.getString("failover_method") 
                        + " failed over: " + rs.getString("failed_over") 
                        + " module: " + rs.getString("module")
                        + " action: " + rs.getString("action")
                        );

                    /* this code block is executed yet not reflected in the query output. */ 
                    if (prevInstance != rs.getInt("inst_id")) {
                        System.out.println("trying to set client info, module and action again");
                        connection.setClientInfo("OCSID.CLIENTID","MARTIN");
                        connection.setClientInfo("OCSID.MODULE", "TAF Demo");
                        connection.setClientInfo("OCSID.ACTION", this.jdbcDriver);
                    }
                    prevInstance = rs.getInt("inst_id");
                }

                /* sleep for 5 seconds */
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
            }

            /* we are done - clean up and leave */
            connection.close();
        } catch (SQLException e4) {
            
            System.out.println("Something unexpected happened: " + e4.toString());
            e4.printStackTrace();
        }
    }

    public static void main( String[] args ) throws SQLException
    {
        System.out.println( "About to start a demonstration using Transparent Application Failover" );

        TAFDemo app = null;

        for (String arg: args) {
            System.out.println(arg);
        }

        if ( args.length == 0 ) {
            app = new TAFDemo("oci");
        } else {
            if ( args[0].equals("thin")) {
                app = new TAFDemo("thin");
            } else if ( args[0].equals("oci")) {
                app = new TAFDemo("oci");
            }
        }

        app.printJDBCmetadata();
        app.doSomeWork();
    }
}
