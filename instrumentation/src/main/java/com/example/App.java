package com.example;

import oracle.jdbc.OracleConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple application demonstrating how to use setClientInfo() and how it piggy-backs
 * on other calls.
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        App app = new App();
        app.run();
    }

    public void run()
    {
        try {
            OracleConnection conn = OracleUCPDataSource.getPooledConnection();
            System.out.println("Successfully acquired a pooled OracleConnection.");

            conn.setClientInfo("OCSID.CLIENTID", "Some dude");
            conn.setClientInfo("OCSID.MODULE", "Demo Module");
            conn.setClientInfo("OCSID.ACTION", "simulate some workload");

            System.out.println("""
                    Module/Action and Client Info have been set - v$session is empty though
                    select sid, serial#, username, program, module, action, client_identifier from v$session where username = 'DEMOUSER'
                    """
            );
            Thread.sleep(30000);

            PreparedStatement pstmt = conn.prepareStatement("select user");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("""
                    The metadata rode piggy-back on the previous query, check v$session again:
                    select sid, serial#, username, program, module, action, client_indentifier from v$session where username = 'DEMOUSER'
                    """
            );

            Thread.sleep(30000);

            while (rs.next()) {
                System.out.println("You are connected as " + rs.getString(1));
            }

            // return session to the pool
            conn.close();
            conn = null;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // nothing...
        }
    }

}
