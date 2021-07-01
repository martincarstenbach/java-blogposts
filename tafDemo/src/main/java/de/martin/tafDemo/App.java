package de.martin.tafDemo;


/**
 * A sample application to demonstrate the need for the OCI "fat" client when using Transparent
 * Application Failover
 *
 * Originally this code made its first appearance in the RAC book, but it is now heavily updated
 * for Oracle 19c.
 * 
 * The App class merely starts the application, code is in TAFDemo and TAFCallback
 * 
 * See https://martincarstenbach.wordpress.com/2020/08/18/jdbc-the-oracle-database-if-you-want-transparent-application-failover-you-need-the-oci-driver/
 * for more information, especially the configuration of the wallet.
 * 
 */

public class App 
{

    public static void main( String[] args )
    {
        System.out.println( "About to start a demonstration using Transparent Application Failover" );

        TAFDemo app = null;

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
