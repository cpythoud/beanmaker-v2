package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertMoneyAmountToCentsTest {

    @Test
    void testConversion() {
        assertEquals(255, Strings.convertMoneyAmountToCents("2.55", 2, "."));
        assertEquals(255, Strings.convertMoneyAmountToCents("2,55", 2, ","));

        assertEquals(640, Strings.convertMoneyAmountToCents("6.4", 2, "."));

        assertEquals(100, Strings.convertMoneyAmountToCents("1", 2, "."));

        assertEquals(25000, Strings.convertMoneyAmountToCents("25", 3, "."));
        assertEquals( 8150, Strings.convertMoneyAmountToCents("8.15", 3, "."));
    }

}
