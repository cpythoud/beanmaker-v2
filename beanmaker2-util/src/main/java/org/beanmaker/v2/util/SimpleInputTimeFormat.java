package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to validate time input.
 * Its functions expect a 24 hours daytime in hours, minutes and seconds. Milliseconds are not supported.
 * This class is mostly used in web applications.
 */
public class SimpleInputTimeFormat {

    private final List<String> separators;

    /**
     * Create a SimpleInputTimeFormat specifying the separator character to be used between hours, minutes and
     * seconds.
     * @param separator to be used between time elements.
     * @see SimpleInputTimeFormat#SimpleInputTimeFormat(java.util.List)
     */
    public SimpleInputTimeFormat(String separator) {
        List<String> seps = new ArrayList<String>();
        seps.add(separator);
        this.separators = Collections.unmodifiableList(seps);
    }

    /**
     * Create a SimpleInputTimeFormat specifying the possible separator characters that can be be used between hours,
     * minutes and seconds.
     * @param separators list of possible separators.
     * @see SimpleInputTimeFormat#SimpleInputTimeFormat(String)
     */
    public SimpleInputTimeFormat(List<String> separators) {
        List<String> seps = new ArrayList<String>();
        seps.addAll(separators);
        this.separators = Collections.unmodifiableList(seps);
    }

    /**
     * Get a list of separators that can be used between time elements (hours, minutes, seconds) for this SimpleTimeFormat.
     * @return a list of separators that can be used between time elements.
     */
    public List<String> getSeparators() {
        return separators;
    }

    /**
     * Validate a time according to this SimpleTimeFormat.
     * @param time to be validated.
     * @return true if the time is correct according to the format, false otherwise.
     */
    public boolean validate(String time) {
        for (String separator: separators) {
            if (Dates.isTimeOK(time, separator))
                return true;
        }

        return false;
    }

    /**
     * Validate a time in short form (without indication of seconds) according to this SimpleTimeFormat.
     * @param time to be validated.
     * @return true if the time is correct according to the format, false otherwise.
     */
    public boolean validateShort(String time) {
        for (String separator: separators) {
            if (Dates.isShortTimeOK(time, separator))
                return true;
        }

        return false;
    }

}
