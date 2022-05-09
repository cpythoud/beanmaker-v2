package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

/**
 * This class gives information about the VERSION of this library.
 */
public class Version {

    private static final String VERSION = "1.0-SNAPSHOT";
    private static final String VERSION_BUILD = "20509";

    /**
     * @return the version number of this library
     */
    public static String get() {
        return VERSION + (Strings.isEmpty(VERSION_BUILD) ? "" : "-build#" + VERSION_BUILD);
    }

}
