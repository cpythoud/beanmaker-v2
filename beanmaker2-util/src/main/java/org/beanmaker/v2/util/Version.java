package org.beanmaker.v2.util;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * This class gives information about the current Beanmaker version.
 */
public class Version {

    public static final int MAJOR;
    public static final int MINOR;
    public static final int PATCH;

    static {
        Properties version = new Properties();
        try (InputStream inputStream = Version.class.getClassLoader().getResourceAsStream("version.properties")) {
            version.load(inputStream);
            MAJOR = Integer.parseInt(version.getProperty("version.major"));
            MINOR = Integer.parseInt(version.getProperty("version.minor"));
            PATCH = Integer.parseInt(version.getProperty("version.patch"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the version number
     */
    public static String get() {
        if (PATCH == 0)
            return MAJOR + "." + MINOR;

        return MAJOR + "." + MINOR + "." + PATCH;
    }

}
