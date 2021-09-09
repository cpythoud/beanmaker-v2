package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to validate dates input. It allows for different orderings of dates elements (day,
 * month, year) and varying separators between these elements.
 */
public class SimpleInputDateFormat {

    /**
     * Here are defined constants to specify the order of elements (day, month, year) in a date format.
     */
    public static enum ElementOrder {
        /**
         * Represents the following date format: 4-digits-year separator 2-digits-month separator 2-digits-day-of-month.
         */
        YYMD,
        /**
         * Represents the following date format: 2-digits-day-of-month separator 2-digits-month separator 4-digits-year.
         */
        DMYY,
        /**
         * Represents the following date format: 2-digits-month separator 2-digits-day-of-month separator 4-digits-year.
         */
        MDYY
    }

    /**
     * Create a SimpleInputDateFormat, specifying the order of the date elements (day, month, year) and the
     * separator used between these elements.
     * @param order of the date elements.
     * @param separator between the elements.
     * @see SimpleInputDateFormat#SimpleInputDateFormat(SimpleInputDateFormat.ElementOrder, java.util.List)
     */
    public SimpleInputDateFormat(final ElementOrder order, final String separator) {
        this.order = order;
        List<String> seps = new ArrayList<String>();
        seps.add(separator);
        this.separators = Collections.unmodifiableList(seps);
    }

    /**
     * Create a SimpleInputDateFormat, specifying the order of the date elements (day, month, year) and a list of
     * possible separators used between these elements.
     * @param order of the date elements.
     * @param separators that can be used between the elements.
     * @see SimpleInputDateFormat#SimpleInputDateFormat(SimpleInputDateFormat.ElementOrder, String)
     */
    public SimpleInputDateFormat(final ElementOrder order, final List<String> separators) {
        this.order = order;
        List<String> seps = new ArrayList<String>();
        seps.addAll(separators);
        this.separators = Collections.unmodifiableList(seps);
    }

    /**
     * Get the expected order of date elements (day, month, year) for this SimpleDateFormat.
     * @return the expected order of date elements.
     */
    public ElementOrder getOrder() {
        return order;
    }

    /**
     * Get a list of separators that can be used between date elements (day, month, year) for this SimpleDateFormat.
     * @return a list of separators that can be used between date elements.
     */
    public List<String> getSeparators() {
        return Collections.unmodifiableList(separators);
    }

    /**
     * Validate a date according to this SimpleDateFormat.
     * @param date to be validated.
     * @return true if the date is correct according to the format, false otherwise.
     */
    public boolean validate(final String date) {
        for (String separator: separators) {
            switch (order) {
                case YYMD:
                    if (Dates.isYYMDDateOK(date, separator))
                        return true;
                    break;
                case DMYY:
                    if (Dates.isDMYYDateOK(date, separator))
                        return true;
                    break;
                case MDYY:
                    if (Dates.isMDYYDateOK(date, separator))
                        return true;
                    break;
                default:
                    throw new IllegalStateException("Cannot get here.");
            }
        }

        return false;
    }

    private final ElementOrder order;
    private final List<String> separators;
}
