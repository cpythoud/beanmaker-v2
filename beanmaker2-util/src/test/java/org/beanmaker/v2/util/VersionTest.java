package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

public class VersionTest {

    @Test
    void printVersion() {
        System.out.println("Version = " + Version.get());
    }

}
