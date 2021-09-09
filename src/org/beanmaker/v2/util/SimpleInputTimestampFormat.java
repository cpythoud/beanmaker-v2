package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to validate timestamp input.
 * Its functions accept dates in various format and expect times as 24 hours daytime in hours, minutes and seconds.
 * Milliseconds are not supported. Please use the removeMillis static functions if you need to process timestamps strings with milliseconds.
 * This class is mostly used in web applications.
 * @see SimpleInputDateFormat
 * @see SimpleInputTimeFormat
 */
public class SimpleInputTimestampFormat {

    private final SimpleInputDateFormat.ElementOrder order;
    private final List<String> dateSeparators;
    private final List<String> timeSeparators;

    /**
     * Create a SimpleInputTimeStampFormat specifying the separator characters to be used between date and time element.
     * @param order of the date elements (YYMD, DMYY or MDYY).
     * @param dateSeparator to be used to separate date elements.
     * @param timeSeparator to be used to separate time elements.
     */
    public SimpleInputTimestampFormat(SimpleInputDateFormat.ElementOrder order, String dateSeparator, String timeSeparator) {
        this.order = order;
        List<String> dateSeps = new ArrayList<String>();
        List<String> timeSeps = new ArrayList<String>();
        dateSeps.add(dateSeparator);
        timeSeps.add(timeSeparator);
        dateSeparators = Collections.unmodifiableList(dateSeps);
        timeSeparators = Collections.unmodifiableList(timeSeps);
    }

    /**
     * Create a SimpleInputTimeStampFormat specifying the separator characters to be used between date and time element.
     * @param order of the date elements (YYMD, DMYY or MDYY).
     * @param dateSeparators that can be used to separate date elements.
     * @param timeSeparators that can be used to separate time elements.
     */
    public SimpleInputTimestampFormat(SimpleInputDateFormat.ElementOrder order, List<String> dateSeparators, List<String> timeSeparators) {
        this.order = order;
        List<String> dateSeps = new ArrayList<String>();
        List<String> timeSeps = new ArrayList<String>();
        dateSeps.addAll(dateSeparators);
        timeSeps.addAll(timeSeparators);
        this.dateSeparators = Collections.unmodifiableList(dateSeps);
        this.timeSeparators = Collections.unmodifiableList(timeSeps);
    }

    /**
     * Get the expected order of date elements (day, month, year) for this SimpleTimestampFormat.
     * @return the expected order of date elements.
     */
    public SimpleInputDateFormat.ElementOrder getOrder() {
        return order;
    }

    /**
     * Get a list of separators that can be used between date elements (day, month, year) for this SimpleTimestampFormat.
     * @return a list of separators that can be used between date elements.
     */
    public List<String> getDateSeparators() {
        return dateSeparators;
    }

    /**
     * Get a list of separators that can be used between time elements (hours, minutes, seconds) for this SimpleTimestampFormat.
     * @return a list of separators that can be used between time elements.
     */
    public List<String> getTimeSeparators() {
        return timeSeparators;
    }

    /**
     * Validate a time according to this SimpleTimestampFormat.
     * @param timestamp to be validated.
     * @return true if the time is correct according to the format, false otherwise.
     */
    public boolean validate(String timestamp) {
        for (String dateSeparator: dateSeparators) {
            for (String timeSeparator: timeSeparators) {
                switch (order) {
                    case YYMD:
                        if (Dates.isYYMDTimestampOK(timestamp, dateSeparator, timeSeparator))
                            return true;
                        break;
                    case DMYY:
                        if (Dates.isDMYYTimestampOK(timestamp, dateSeparator, timeSeparator))
                            return true;
                        break;
                    case MDYY:
                        if (Dates.isMDYYTimestampOK(timestamp, dateSeparator, timeSeparator))
                            return true;
                        break;
                    default:
                        throw new IllegalStateException("Cannot get here.");
                }
            }
        }

        return false;
    }

    /**
     * Removes the milliseconds at the end of a String representing a Timestamp. Uses the standard dot separator to
     * separate the seconds from the milliseconds.
     * @param timestampStr String representing a Timestamp.
     * @return a String representation of a Timestamp without milliseconds.
     */
    public static String removeMillis(String timestampStr) {
        return removeMillis(timestampStr, ".");
    }

    /**
     * Removes the milliseconds at the end of a String representing a Timestamp.
     * @param timestampStr String representing a Timestamp.
     * @param separator used between seconds and milliseconds.
     * @return a String representation of a Timestamp without milliseconds.
     */
    public static String removeMillis(String timestampStr, String separator) {
        int index = timestampStr.lastIndexOf(separator);
        if (index == -1)
            throw new IllegalArgumentException("Could not find separator. Timestamp String might not contain milliseconds. Received: " + timestampStr);

        if (!timestampStr.substring(index + 1).matches("[0-9]+"))
            throw new IllegalArgumentException("Characters other than digits after separator. Timestamp String might be incorrect. Received: " + timestampStr);

        return timestampStr.substring(0, index);
    }

}
