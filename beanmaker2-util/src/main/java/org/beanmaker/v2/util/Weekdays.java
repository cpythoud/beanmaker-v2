package org.beanmaker.v2.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Weekdays {

    // * ISO Week date
    // * see https://en.wikipedia.org/wiki/ISO_week_date

    private final int year;
    private final DayOfWeek januaryFourWeekday;
    private final int weekCount;

    public Weekdays(int year) {
        this.year = year;
        januaryFourWeekday = LocalDate.of(year, 1, 4).getDayOfWeek();
        weekCount = calcWeekCount();
    }

    private int calcWeekCount() {
        // * If current year ends on Thursday or previous year ended on Wednesday
        if (getWeekDayIndexOfLastDayOfYear(year) == 4 || getWeekDayIndexOfLastDayOfYear(year -1) == 3)
            return 53;

        return 52;
    }

    public int getWeekCount() {
        return weekCount;
    }

    private int getWeekDayIndexOfLastDayOfYear(double year) {
        return ((int) (year + (year / 4d) - (year / 100d) + (year / 400d))) % 7;
    }

    public LocalDate getWeekday(DayOfWeek weekday, int week) {
        if (week < 1 || week > weekCount)
            throw new IllegalArgumentException("For year " + year + " week must be comprised between 1 and " + weekCount);

        return LocalDate.ofYearDay(year, week * 7 + weekday.getValue() - (januaryFourWeekday.getValue() + 3));
    }

}
