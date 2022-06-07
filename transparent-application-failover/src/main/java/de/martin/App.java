package de.martin;

import de.martin.taf.TAFDemo;

public class App {
    public static void main( String[] args ) {
    System.out.println("About to start a demonstration of Transparent Application Failover");

        String driverType = null;

        // no command line arguments: default to "OCI"
        if (args.length == 0) {
            driverType = "thin";
        } else {
            if (args[0].equals("thin")) {
                driverType = "thin";
            } else if (args[0].equals("oci")) {
                driverType = "oci";
            } else {
                System.err.println("Usage: App thin|oci");
                return;
            }
        }

        try {
            TAFDemo demo = new TAFDemo(driverType);
            demo.printJDBCmetadata();
            demo.doSomeWork();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}