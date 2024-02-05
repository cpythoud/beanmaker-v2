package org.beanmaker.v2.util;

import java.io.PrintStream;

/**
 * This class provides debugging utilities.
 */
public class Debug {


    /**
     * Prints the trust/key store path.
     *
     * This method prints the trust/key store path to the specified PrintStream.
     * The trust/key store path can be retrieved from the property "javax.net.ssl.trustStore".
     * If the property is not set, the default trust/key store path is printed.
     *
     * @param out The PrintStream to which the trust/key store path will be printed
     */
    public static void printTrustKeyStorePath(PrintStream out) {
        out.println("Trust/key store path from property javax.net.ssl.trustStore (null if default) = "
                + System.getProperty("javax.net.ssl.trustStore"));
        out.println("Default trust/key store path = " + System.getProperty("java.home") + "/lib/security/cacerts");
    }

}
