package de.martin.demo01;

import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

/**
 * The equivalent of a "hello world" type application for connecting to the 
 * Oracle database 
 */
public class App 
{

    final static String ezConnectPlus = "jdbc:oracle:thin:/@orcl?TNS_ADMIN=/home/martin/tns";

    public static void main( String[] args ) throws SQLException
    {
        System.out.println( "Getting ready to connect to the database" );

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(ezConnectPlus);

        try (OracleConnection connection = (OracleConnection) ods.getConnection()) {
            
            // Get the JDBC driver name and version 
            DatabaseMetaData dbmd = connection.getMetaData();       
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());
            
            // Print some connection properties
            System.out.println("Default Row Prefetch Value is: " + 
               connection.getDefaultRowPrefetch());
            System.out.println("Database Username is: " + connection.getUserName());
            System.out.println();

            connection.close();
          }   
    }
}
