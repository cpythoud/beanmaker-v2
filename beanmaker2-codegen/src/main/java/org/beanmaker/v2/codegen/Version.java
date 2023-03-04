package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

/**
 * This class gives information about the VERSION of this library.
 */
public class Version {

    private static final String VERSION = "1.0-SNAPSHOT";
    private static final String VERSION_BUILD = "30304";

    /**
     * @return the version number of this library
     */
    public static String get(boolean displayShort) {
        var version = new StringBuilder();
        version.append(VERSION);
        if (VERSION.endsWith("SNAPSHOT")) {
            if (!Strings.isEmpty(VERSION_BUILD)) {
                if (displayShort)
                    version.append("-");
                else
                    version.append("-build#");
                version.append(VERSION_BUILD);
            }
        }

        return version.toString();
    }

}
