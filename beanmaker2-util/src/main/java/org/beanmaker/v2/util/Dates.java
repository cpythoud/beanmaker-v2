package org.beanmaker.v2.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class contains static function to help with Dates manipulations in the context of a database application.
 * Only Gregorian calendar dates are supported.
 */
@SuppressWarnings("MagicConstant")
public class Dates {

    /**
     * Given a java.sql.Date, returns the day of month.
     * @param date, a java.sql.Date.
     * @return day of month.
     */
    public static int getDay(Date date) {
        return Integer.parseInt(date.toString().substring(8, 10));
    }

    /**
     * Given a java.sql.Date, returns the month.
     * @param date, a java.sql.Date.
     * @return month.
     */
    public static int getMonth(Date date) {
        return Integer.parseInt(date.toString().substring(5, 7));
    }

    /**
     * Given a java.sql.Date, returns the year.
     * @param date, a java.sql.Date.
     * @return year.
     */
    public static int getYear(Date date) {
        return Integer.parseInt(date.toString().substring(0, 4));
    }

    /**
     * Given a java.sql.Time, returns the hours.
     * @param time, a java.sql.Time.
     * @return hours.
     */
    public static int getHours(Time time) {
        return Integer.parseInt(time.toString().substring(0, 2));
    }

    /**
     * Given a java.sql.Time, returns the minutes.
     * @param time, a java.sql.Time.
     * @return minutes.
     */
    public static int getMinutes(Time time) {
        return Integer.parseInt(time.toString().substring(3, 5));
    }

    /**
     * Given a java.sql.Time, returns the seconds.
     * @param time, a java.sql.Time.
     * @return seconds.
     */
    public static int getSeconds(Time time) {
        return Integer.parseInt(time.toString().substring(6, 8));
    }

    /**
     * Given a java.sql.Time, returns how many seconds have elapsed in the day since midnight.
     * @param time, a java.sql.Time.
     * @return seconds since midnight.
     */
    public static int getTimeInSeconds(Time time) {
        return getSeconds(time) + getMinutes(time) * 60 + getHours(time) * 3600;
    }

    /**
     * Returns a java.sql.Date initialized with the current date.
     * The timestamp part of any java.sql.Date returned by getCurrentDate()
     * is 00:00:00.000.
     * @return a java.sql.Date initialized with the current date.
     */
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Date(calendar.getTime().getTime());
    }

    /**
     * Returns a java.sql.Time initialized with the current timestamp.
     * The date part of any java.sql.Time returned by getCurrentTime()
     * is January 1, 1970 and the microseconds are set to 0.
     * @return a java.sql.Time initialized with the current timestamp.
     */
    public static Time getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Time(calendar.getTime().getTime());
    }

    /**
     * Returns the current year on the computer clock
     * @return the current year
     */
    public static int getCurrentYear() {
        return getYear(getCurrentDate());
    }

    /**
     * Returns the current month (1-12) on the computer clock
     * @return the current month
     */
    public static int getCurrentMonth() {
        return getMonth(getCurrentDate());
    }

    /**
     * Returns the current day of the month (1-31) on the computer clock
     * @return the current day of the month
     */
    public static int getCurrentDayOfMonth() {
        return getDay(getCurrentDate());
    }

    /**
     * Returns a java.sql.Timestamp initialized with the current date and timestamp.
     * @return a java.sql.Timestamp initialized with the current date and timestamp.
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp((new java.util.Date()).getTime());
    }

    /**
     * Returns a number that can be used as a timestamp. Useful for recording creation dates or generating
     * timestamp dependant, unique, names. (For temporary files or directories for example.)
     * @return a long. Its first four digits represent the current year, the next two the current month, the next
     * two the current day of month, the next two the current hour, the next two the current minutes, the next
     * two the current seconds and the next three the current milliseconds.
     */
    public static long getMeaningfulTimeStamp() {
        return Long.parseLong(MTS_DATE_FORMAT.format(new java.util.Date()));
    }

    private static final DateFormat MTS_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * Check if a date is correct.
     * @param day of the month (1-31)
     * @param month of the year (1-12)
     * @param year (1583+)
     * @return true if the date is correct, false otherwise.
     * @see Dates#isDateOK(String, String, String)
     */
    public static boolean isDateOK(int day, int month, int year) {
        Calendar cal = new GregorianCalendar(year, month - 1, day);
        cal.setLenient(false);
        boolean result = true;
        try {
            cal.getTime();
        } catch (IllegalArgumentException ex) {
            result = false;
        }
        return result;
    }

    /**
     * Check if a date is correct.
     * @param day of the month (1-31)
     * @param month of the year (1-12)
     * @param year (1583+)
     * @return true if the date is correct, false otherwise.
     * @see Dates#isDateOK(int, int, int)
     */
    public static boolean isDateOK(String day, String month, String year) {
        boolean result = false;
        try {
            result = isDateOK(Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));
        } catch (NumberFormatException ex) {
            // result = false !
        }
        return result;
    }

    /**
     * Check if a timestamp is correct. This function checks if the hours, minutes and seconds of a timestamp are in
     * acceptable range, respectively 0-23, 0-59 and 0-59
     * @param hours of timestamp to be checked.
     * @param minutes of timestamp to be checked.
     * @param seconds of timestamp to be checked.
     * @return true if timestamp can be validated, false otherwise.
     * @see Dates#isTimeOK(String, String, String)
     */
    public static boolean isTimeOK(int hours, int minutes, int seconds) {
        return !(hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59);
    }

    /**
     * Check if a timestamp is correct. This function checks if the hours, minutes and seconds of a timestamp are in
     * acceptable range, respectively 0-23, 0-59 and 0-59.
     * @param hours of timestamp to be checked.
     * @param minutes of timestamp to be checked.
     * @param seconds of timestamp to be checked.
     * @return true if timestamp can be validated, false otherwise.
     * @see Dates#isTimeOK(int, int, int)
     */
    public static boolean isTimeOK(String hours, String minutes, String seconds) {
        boolean result = false;
        try {
            result = isTimeOK(Integer.parseInt(hours), Integer.parseInt(minutes), Integer.parseInt(seconds));
        } catch (NumberFormatException ex) {
            // result = false !
        }
        return result;
    }

    /**
     * Check if a timestamp String is correctly formatted.
     * The timestamp string must be comprised of hours, minutes and seconds. Precision beyond seconds is not supported.
     * The three parts of the timestamp string must be between acceptable range for hours, minutes and seconds,
     * respectively 0-23, 0-59 and 0-59.
     * @param time string to be checked.
     * @param separator between the timestamp string parts.
     * @return true if timestamp string can be validated, false otherwise.
     * @see Dates#isShortTimeOK(String, String)
     */
    public static boolean isTimeOK(String time, String separator) {
        String[] parts = time.split(separator);
        return parts.length == 3 && isTimeOK(parts[0], parts[1], parts[2]);
    }

    /**
     * Check if a timestamp String, without seconds, is correctly formatted.
     * The timestamp string must be comprised of hours and minutes only.
     * The two parts of the timestamp string must be between acceptable range for hours and minutes,
     * respectively 0-23 and 0-59.
     * @param time string to be checked.
     * @param separator between the hours and minutes.
     * @return true if timestamp string can be validated, false otherwise.
     * @see Dates#isTimeOK(String, String, String)
     */
    public static boolean isShortTimeOK(String time, String separator) {
        return isTimeOK(time + separator + "00", separator);
    }

    /**
     * Check if a date in the format YYMD is correct. That is a date in the format: 4-digits-year separator
     * 2-digits-month separator 2-digits-day-of-month.
     * @param date to be checked.
     * @param separator character(s) used to separate the digits.
     * @return true if the date is correct, false otherwise.
     * @see Dates#isDMYYDateOK(String, String)
     * @see Dates#isMDYYDateOK(String, String)
     */
    public static boolean isYYMDDateOK(String date, String separator) {
        String[] parts = date.split(separator);
        return parts.length == 3 && isDateOK(parts[2], parts[1], parts[0]);
    }

    /**
     * Check if a date in the format DMYY is correct. That is a date in the format: 2-digits-day-of-month separator
     * 2-digits-month separator 4-digits-year.
     * @param date to be checked.
     * @param separator character(s) used to separate the digits.
     * @return true if the date is correct, false otherwise.
     * @see Dates#isYYMDDateOK(String, String)
     * @see Dates#isDMYYDateOK(String, String)
     */
    public static boolean isDMYYDateOK(String date, String separator) {
        String[] parts = date.split(separator);
        return parts.length == 3 && isDateOK(parts[0], parts[1], parts[2]);
    }

    /**
     * Check if a date in the format DMYY is correct. That is a date in the format: 2-digits-month separator
     * 2-digits-day-of-month separator 4-digits-year.
     * @param date to be checked.
     * @param separator character(s) used to separate the digits.
     * @return true if the date is correct, false otherwise.
     * @see Dates#isYYMDDateOK(String, String)
     * @see Dates#isDMYYDateOK(String, String)
     */
    public static boolean isMDYYDateOK(String date, String separator) {
        String[] parts = date.split(separator);
        return parts.length == 3 && isDateOK(parts[1], parts[0], parts[2]);
    }

    /**
     * Check if a timestamp, of which the date part is in the format YYMD, is correct.
     * @param timestamp to be checked.
     * @param dateSeparator to be used to separate the date elements (year, month, day).
     * @param timeSeparator to be used to separate the timestamp elements (hours, minutes, seconds).
     * @return true if the timestamp is correct, false otherwise.
     * @see Dates#isYYMDTimestampOK(String, String, String, String)
     * @see Dates#isYYMDDateOK(String, String)
     * @see Dates#isTimeOK(String, String)
     */
    public static boolean isYYMDTimestampOK(String timestamp, String dateSeparator, String timeSeparator) {
        return isYYMDTimestampOK(timestamp, dateSeparator, timeSeparator, "\\.");
    }

    /**
     * Check if a timestamp, of which the date part is in the format YYMD, is correct.
     * @param timestamp to be checked.
     * @param dateSeparator to be used to separate the date elements (year, month, day).
     * @param timeSeparator to be used to separate the timestamp elements (hours, minutes, seconds).
     * @param millisecondsSeparator to be used to separate the milliseconds from the timestamp. If you don't need
     *                              to check for milliseconds, use
     *                              {@link Dates#isYYMDTimestampOK(String, String, String)}.
     * @return true if the timestamp is correct, false otherwise.
     * @see Dates#isYYMDTimestampOK(String, String, String)
     * @see Dates#isYYMDDateOK(String, String)
     * @see Dates#isTimeOK(String, String)
     */
    public static boolean isYYMDTimestampOK(String timestamp, String dateSeparator, String timeSeparator, String millisecondsSeparator) {
        if (dateSeparator.equals(timeSeparator) || dateSeparator.equals(millisecondsSeparator) || timeSeparator.equals(millisecondsSeparator))
            throw new IllegalArgumentException("Separators must be distincts");

        String[] parts = timestamp.split(millisecondsSeparator);
        if (parts.length > 2)
            return false;
        String milliseconds;
        if (parts.length == 1)
            milliseconds = "0";
        else
            milliseconds = parts[1];

        parts = parts[0].split("[\\s]+");
        if (parts.length != 2)
            return false;
        String date = parts[0];
        String time = parts[1];

        return isYYMDDateOK(date, dateSeparator) && isTimeOK(time, timeSeparator) && representsNumber(milliseconds);
    }

    private static boolean representsNumber(String s) {
        return s.matches("[0-9]+");
    }

    /**
     * Check if a timestamp, of which the date part is in the format DMYY, is correct.
     * @param timestamp to be checked.
     * @param dateSeparator to be used to separate the date elements (year, month, day).
     * @param timeSeparator to be used to separate the timestamp elements (hours, minutes, seconds).
     * @return true if the timestamp is correct, false otherwise.
     * @see Dates#isDMYYDateOK(String, String)
     * @see Dates#isTimeOK(String, String)
     */
    public static boolean isDMYYTimestampOK(String timestamp, String dateSeparator, String timeSeparator) {
        String[] parts = timestamp.split("[\\s]+");
        return parts.length == 2 && isDMYYDateOK(parts[0], dateSeparator) && isTimeOK(parts[1], timeSeparator);
    }

    /**
     * Check if a timestamp, of which the date part is in the format MDYY, is correct.
     * @param timestamp to be checked.
     * @param dateSeparator to be used to separate the date elements (year, month, day).
     * @param timeSeparator to be used to separate the timestamp elements (hours, minutes, seconds).
     * @return true if the timestamp is correct, false otherwise.
     * @see Dates#isMDYYDateOK(String, String)
     * @see Dates#isTimeOK(String, String)
     */
    public static boolean isMDYYTimestampOK(String timestamp, String dateSeparator, String timeSeparator) {
        String[] parts = timestamp.split("[\\s]+");
        return parts.length == 2 && isMDYYDateOK(parts[0], dateSeparator) && isTimeOK(parts[1], timeSeparator);
    }

    /**
     * Transform a String in a {@link java.sql.Date} object. The separator between the date elements
     * (year, month, day in this order) must be specified.
     * @param string to be converted.
     * @param separator used to separate the timestamp elements.
     * @return a Date object.
     */
    public static Date getDateFromYYMD(String string, String separator) {
        String[] parts = string.split(separator);
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid format: must be  yyyy" + separator + "[m]m" + separator + "[d]d, received " + string);
        if (!isDateOK(parts[2], parts[1], parts[0]))
            throw new IllegalArgumentException("Submitted date (" + string + ") is invalid.");

        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Transform a String in a {@link java.sql.Date} object. The separator between the date elements
     * (day, month, year in this order) must be specified.
     * @param string to be converted.
     * @param separator used to separate the timestamp elements.
     * @return a Date object.
     */
    public static Date getDateFromDMYY(String string, String separator) {
        String[] parts = string.split(separator);
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid format: must be  [d]d" + separator + "[m]m" + separator + "yyyy, received " + string);
        if (!isDateOK(parts[0], parts[1], parts[2]))
            throw new IllegalArgumentException("Submitted date (" + string + ") is invalid.");

        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Transform a String in a {@link java.sql.Date} object. The separator between the date elements
     * (month, day, year in this order) must be specified.
     * @param string to be converted.
     * @param separator used to separate the timestamp elements.
     * @return a Date object.
     */
    public static Date getDateFromMDYY(String string, String separator) {
        String[] parts = string.split(separator);
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid format: must be  yyyy" + separator + "[d]d" + separator + "[m]m, received " + string);
        if (!isDateOK(parts[1], parts[0], parts[2]))
            throw new IllegalArgumentException("Submitted date (" + string + ") is invalid.");

        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[2]), Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Transform a String in a {@link java.sql.Time} object. The separator between the timestamp elements
     * (hours, minutes, seconds) must be specified.
     * @param string to be converted.
     * @param separator used to separate the timestamp elements.
     * @return a Time object.
     */
    public static Time getTimeFromString(String string, String separator) {
        String[] parts = string.split(separator);
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid format: must be   [h]h" + separator + "[m]m" + separator + "[s]s, received " + string);
        if (!isTimeOK(parts[0], parts[1], parts[2]))
            throw new IllegalArgumentException("Submitted timestamp (" + string + ") is invalid.");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal.set(Calendar.SECOND, Integer.parseInt(parts[2]));

        return new Time(cal.getTimeInMillis());
    }

    /**
     * Transform a 'short' String, including only hours and minutes,
     * in a {@link java.sql.Time} object. The separator between hours and minutes must be specified.
     * @param string to be converted.
     * @param separator used to separate hours and minutes.
     * @return a Time object.
     */
    public static Time getTimeFromShortString(String string, String separator) {
        String[] parts = string.split(separator);
        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid format: must be   [h]h" + separator + "[m]m, received " + string);
        if (!isTimeOK(parts[0], parts[1], "00"))
            throw new IllegalArgumentException("Submitted timestamp (" + string + ") is invalid.");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal.set(Calendar.SECOND, 0);

        return new Time(cal.getTimeInMillis());
    }

    /**
     * Transform a String in a {@link java.sql.Timestamp} object. The separators between the date elements
     * (years, month, days) and timestamp elements (hours, minutes, seconds) must be specified.
     * @param string to be converted.
     * @param dateSeparator to separate date elements.
     * @param timeSeparator to separate timestamp elements.
     * @return a Timestamp object.
     */
    public static Timestamp getTimestampFromYYMD(String string, String dateSeparator, String timeSeparator) {
        return getTimestampFromYYMD(string, dateSeparator, timeSeparator, "\\.");
    }

    /**
     * Transform a String in a {@link java.sql.Timestamp} object. The separators between the date elements
     * (years, month, days) and timestamp elements (hours, minutes, seconds), and between timestamp and milliseconds,
     * must be specified.
     * @param string to be converted.
     * @param dateSeparator to separate date elements.
     * @param timeSeparator to separate timestamp elements.
     * @param millisecondsSeparator to separate timestamp and milliseconds.
     * @return a Timestamp object.
     */
    public static Timestamp getTimestampFromYYMD(String string, String dateSeparator, String timeSeparator, String millisecondsSeparator) {
        String[] milliParts = string.split(millisecondsSeparator);
        if (milliParts.length > 2)
            throw new IllegalArgumentException("Invalid format : must be yyyy" + dateSeparator + "[m]m" + dateSeparator + "[d]d [h]h" + timeSeparator + "[m]m" + timeSeparator + "[s]s[" + millisecondsSeparator + "mmm], got " + string);
        String milliseconds;
        if (milliParts.length == 1)
            milliseconds = "0";
        else
            milliseconds = milliParts[1];

        String[] parts = milliParts[0].split("[\\s]+");
        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid format : must be yyyy" + dateSeparator + "[m]m" + dateSeparator + "[d]d [h]h" + timeSeparator + "[m]m" + timeSeparator + "[s]s[" + millisecondsSeparator + "mmm], got " + string);

        String[] dateParts = parts[0].split(dateSeparator);
        if (dateParts.length != 3)
            throw new IllegalArgumentException("Invalid date format : must be yyyy" + dateSeparator + "[m]m" + dateSeparator + "[d]d, got " + parts[0]);
        if (!isDateOK(dateParts[2], dateParts[1], dateParts[0]))
            throw new IllegalArgumentException("La date fournie (" + parts[0] + ") est invalide !");

        String[] timeParts = parts[1].split(timeSeparator);
        if (timeParts.length != 3)
            throw new IllegalArgumentException("Invalid timestamp format : must be [h]h" + timeSeparator + "[m]m" + timeSeparator + "[s]s, got " + parts[1]);
        if (!isTimeOK(timeParts[0], timeParts[1], timeParts[2]))
            throw new IllegalArgumentException("Time passed (" + string + ") is invalid");

        Calendar cal = new GregorianCalendar(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        cal.set(Calendar.SECOND, Integer.parseInt(timeParts[2]));
        cal.set(Calendar.MILLISECOND, Integer.parseInt(milliseconds));

        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Compare two dates (java.sql.Date). If the first date is the same as the second date, returns 0.
     * If the first date is before the second date, returns a negative number (i.e., -1).
     * If the first date is after the second date, returns a positive number (i.e., 1).
     * This method should be used instead of the compareTo() method inherited from java.util.Date which does not
     * work as expected when the dates are the same (problem with the normalization of the timestamp parts in
     * java.util.Date).
     * @param date1
     * @param date2
     * @return
     */
    public static int compare(Date date1, Date date2) {
        return date1.toString().compareTo(date2.toString());
    }

    /**
     * Compare two times (java.sql.Time). Only the hours, minutes and seconds are compared. Milliseconds are ignored.
     * If the first timestamp is the same as the second timestamp, returns 0.
     * If the first timestamp is before the second timestamp, returns a negative number (i.e., -1).
     * If the first timestamp is after the second timestamp, returns a positive number (i.e., 1).
     * @param time1
     * @param time2
     * @return
     */
    public static int compare(Time time1, Time time2) {
        return getTimeInSeconds(time1) - getTimeInSeconds(time2);
    }

    /**
     * Checks if a date is between two other dates.
     * @param date to check
     * @param start of period
     * @param end of period
     * @return true if date is between start and end or if date == start or date == end, false otherwise
     */
    public static boolean isBetween(Date date, Date start, Date end) {
        if (compare(start, end) > 0)  // end before start
            return compare(date, end) >= 0 && compare(date, start) <= 0;

        return compare(date, start) >= 0 && compare(date, end) <= 0;
    }

    /**
     * Subtract days from a date.
     * @param date the date from which days are to be subtracted
     * @param days the number of days to subtract
     * @return the new date with subtracted days
     */
    public static Date minusDays(Date date, int days) {
        return MilliSeconds.minus(date, days * MilliSeconds.ONE_DAY);
    }

    /**
     * Add days to a date.
     * @param date the date to which days are to be added
     * @param days the number of days to add
     * @return the new date with added days
     */
    public static Date plusDays(Date date, int days) {
        return MilliSeconds.plus(date, days * MilliSeconds.ONE_DAY);
    }

    /**
     * Subtract weeks from a date.
     * @param date the date from which weeks are to be subtracted
     * @param weeks the number of weeks to subtract
     * @return the new date with subtracted weeks
     */
    public static Date minusWeeks(Date date, int weeks) {
        return MilliSeconds.minus(date, weeks * MilliSeconds.ONE_WEEK);
    }

    /**
     * Add weeks to a date.
     * @param date the date to which weeks are to be added
     * @param weeks the number of weeks to add
     * @return the new date with added weeks
     */
    public static Date plusWeeks(Date date, int weeks) {
        return MilliSeconds.plus(date, weeks * MilliSeconds.ONE_WEEK);
    }

    public static boolean isTodayAfter(Date date) {
        return compare(getCurrentDate(), date) > 0;
    }

    public static boolean isToday(Date date) {
        return compare(getCurrentDate(), date) == 0;
    }

    public static boolean isTodayBefore(Date date) {
        return compare(getCurrentDate(), date) < 0;
    }

    public static Date getFirstDayOfMonth(int month, int year) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("month must be between 1 & 12");

        return new Date(getMonthYearCalendar(month, year).getTimeInMillis());
    }

    public static Date getLastDayOfMonth(int month, int year) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("month must be between 1 & 12");

        Calendar cal = getMonthYearCalendar(month, year);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return new Date(cal.getTimeInMillis());
    }

    private static Calendar getMonthYearCalendar(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

    public static long getNumberOfDaysBetween(Date startDate, Date endDate) {
        return Math.abs(Math.round((endDate.getTime() - startDate.getTime()) / (double) MilliSeconds.ONE_DAY));
    }

    public static Timestamp getTimestamp(Date date, Time time) {
        String timeString = getYear(date) + "-" + getMonth(date) + "-" + getDay(date)
                + " " + getHours(time) + ":" + getMinutes(time) + ":" + getSeconds(time);
        return getTimestampFromYYMD(timeString, "-", ":");
    }

    public static Date changeYear(Date date, int year) {
        String monthDayPart = date.toString().substring(4);
        if (monthDayPart.equals("-02-29") && !isLeapYear(year))
            monthDayPart = "-02-28";
        return getDateFromYYMD(year + monthDayPart, "-");
    }

    public static boolean isLeapYear(int year) {
        if (year % 400 == 0)
            return true;
        return year % 4 == 0 && !(year % 100 == 0);
    }

    public static String formatIso8601dString(Date date) {
        return date.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatIso8601dString(Time time) {
        return time.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static String formatIso8601dString(Timestamp timestamp) {
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Creates a new instance of Date based on the provided year, month, and dayOfMonth.
     *
     * @param year        the year value, represented as an integer.
     * @param month       the month value, represented as an integer. The value should be between 1 and 12 (inclusive).
     * @param dayOfMonth  the day of the month value, represented as an integer. The value should be between 1 and 31 (inclusive).
     * @return a new instance of Date.
     */
    public static Date createDate(int year, int month, int dayOfMonth) {
        var date = LocalDate.of(year, month, dayOfMonth);
        return Date.valueOf(date);
    }

    /**
     * Creates a Time object with the specified hour, minutes, and seconds.
     *
     * @param hour    the hour value (0-23)
     * @param minutes the minute value (0-59)
     * @param seconds the second value (0-59)
     * @return the Time object representing the specified time
     */
    public static Time createTime(int hour, int minutes, int seconds) {
        var time = LocalTime.of(hour, minutes, seconds);
        return Time.valueOf(time);
    }

    /**
     * Creates a Timestamp object based on the provided date and time components.
     *
     * @param year        the year (e.g., 2022)
     * @param month       the month value (1-12)
     * @param dayOfMonth  the day of the month (1-31)
     * @param hour        the hour of the day (0-23)
     * @param minutes     the minute value (0-59)
     * @param seconds     the second value (0-59)
     * @return a Timestamp object representing the specified date and time
     */
    public static Timestamp createTimestamp(int year, int month, int dayOfMonth, int hour, int minutes, int seconds) {
        var dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minutes, seconds);
        return Timestamp.valueOf(dateTime);
    }

}
