package org.beanmaker.v2.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * This class gives information about the current Beanmaker version.
 */
public class Version {

    public static final String PREFIX;
    public static final int MAJOR;
    public static final int MINOR;
    public static final int PATCH;
    public static final String POSTFIX;

    static {
        Properties version = new Properties();
        try (InputStream inputStream = Version.class.getClassLoader().getResourceAsStream("version.properties")) {
            version.load(inputStream);
            PREFIX  = version.getProperty("version.prefix");
            MAJOR   = Integer.parseInt(version.getProperty("version.major"));
            MINOR   = Integer.parseInt(version.getProperty("version.minor"));
            PATCH   = Integer.parseInt(version.getProperty("version.patch"));
            POSTFIX = version.getProperty("version.postfix");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the version number
     */
    public static String get() {
        var version = new StringBuilder();
        if (!Strings.isEmpty(PREFIX))
            version.append(PREFIX).append("-");
        version.append(MAJOR).append(".").append(MINOR);
        if (PATCH != 0)
            version.append(".").append(PATCH);
        if (!Strings.isEmpty(POSTFIX))
            version.append("-").append(POSTFIX);
        return version.toString();
    }

}
