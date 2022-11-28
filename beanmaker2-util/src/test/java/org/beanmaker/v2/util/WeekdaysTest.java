package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeekdaysTest {

    @Test
    void testWeekdayCalculation() {
        var wds = new Weekdays(2022);
        assertEquals("2022-08-26", wds.getWeekday(DayOfWeek.FRIDAY, 34).toString());
        assertEquals("2022-11-04", wds.getWeekday(DayOfWeek.FRIDAY, 44).toString());

        // TODO: test all limit cases
    }

}
