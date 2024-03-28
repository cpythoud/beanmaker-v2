package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

public class Iso8861Tests {

    @Test
    void testIso8861() {
        System.out.println(Dates.formatIso8601dString(Dates.getCurrentDate()));
        System.out.println(Dates.formatIso8601dString(Dates.getCurrentTime()));
        System.out.println(Dates.formatIso8601dString(Dates.getCurrentTimestamp()));
    }

}
