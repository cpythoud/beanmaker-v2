package org.beanmaker.v2.util;

import java.sql.Date;

/**
 * This class contains a few constants to help work with durations in milliseconds and two functions to
 * add and remove milliseconds to a java.sql.Date.
 */
public class MilliSeconds {

    public static final long ONE_SECOND =        1000L;
    public static final long ONE_MINUTE =       60000L;
    public static final long ONE_HOUR   =     3600000L;
    public static final long ONE_DAY    =    86400000L;
    public static final long ONE_WEEK   =   604800000L;
    public static final long ONE_MONTH  =  2592000000L;  // 30 days
    public static final long ONE_YEAR   = 31536000000L;  // 365 days

    public static final long DAYS_28 = 2419200000L;
    public static final long DAYS_29 = DAYS_28 + ONE_DAY;
    public static final long DAYS_30 = ONE_MONTH;
    public static final long DAYS_31 = DAYS_30 + ONE_DAY;

    /**
     * Subtracts milliseconds from a date.
     * @param date the date from which to subtract milliseconds
     * @param milliseconds the number of milliseconds to subtract
     * @return a new date with the number of milliseconds substracted
     */
    public static Date minus(final Date date, final long milliseconds) {
        return new Date(date.getTime() - milliseconds);
    }

    /**
     * Add milliseconds to a date.
     * @param date the date to which milliseconds are to be added
     * @param milliseconds the number of milliseconds to add
     * @return a new date with the number of milliseconds added
     */
    public static Date plus(final Date date, final long milliseconds) {
        return new Date(date.getTime() + milliseconds);
    }
}
