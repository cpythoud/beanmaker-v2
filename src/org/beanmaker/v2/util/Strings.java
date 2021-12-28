package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Strings {

    /**
     * Check if a String is empty. A String is considered empty if it's null, its length is zero or it contains
     * only spaces and/or tabulations.
     * @param string to be checked.
     * @return true if string is empty as per the above definition, false otherwise.
     */
    public static boolean isEmpty(final String string) {
        return string == null || string.matches("\\s*");
    }

    /**
     * Capitalize a String.
     * If the first character of the String is lowercase, an identical String is returned with this first character converted to uppercase.
     * An identical String is returned otherwise.
     * @param string to be capitalized.
     * @return capitalized String.
     */
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
    }

    /**
     * Uncapitalize a String.
     * If the first character of the String is uppercase, an identical String is returned with this first character converted to lowercase.
     * An identical String is returned otherwise.
     * @param string to be uncapitalized.
     * @return uncapitalized String.
     */
    public static String uncapitalize(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1, string.length());
    }

    /**
     * Camelize a String by suppressing underscores and capitalizing the following letter.
     * Given a String of the form 'some_kind_of_string', this function returns a String of the form 'someKindOfString'.
     * This function is mostly used in programs that generate or manipulate source code.
     * @param string to be camelized.
     * @return camelizedString.
     * @throws java.lang.IllegalArgumentException if the String contains anything but alphanumeric ASCII characters, or
     * the String starts or ends with an underscore characters, or the String contains two consecutive underscore
     * characters.
     */
    public static String camelize(final String string) {
        if (!string.matches("^[a-zA-Z0-9_]+$"))
            throw new IllegalArgumentException("Illegal identifier character");
        if (string.startsWith("_"))
            throw new IllegalArgumentException("String cannot start with underscore character");
        if (string.contains("__"))
            throw new IllegalArgumentException("String cannot contain two consecutive underscore characters");
        if (string.endsWith("_"))
            throw new IllegalArgumentException("String cannot end with underscore character");
        String[] parts = string.split("_");
        StringBuilder buf = new StringBuilder();
        for (String part: parts)
            buf.append(capitalize(part.toLowerCase()));
        return buf.toString();
    }

    /**
     * Uncamelize a String by transforming any uppercase letter to its lowercase equivalent and preceding it by an
     * underscore.
     * Given a String of the form 'someKindOfString', this function returns a String of the form 'some_kind_of_string'.
     * This function is mostly used in programs that generate or manipulate source code.
     * @param string to be uncamelized.
     * @return uncamelized String.
     * @throws java.lang.IllegalArgumentException if the String contains anything but alphanumeric ASCII characters.
     */
    public static String uncamelize(final String string) {
        if (!string.matches("^[a-zA-Z0-9]+$"))
            throw new IllegalArgumentException("Illegal identifier character");
        List<String> parts = new ArrayList<String>();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (i > 0 && c >= 'A' && c <= 'Z') {
                parts.add(buf.toString());
                buf = new StringBuilder();
            }
            buf.append(c);
        }
        if (buf.length() > 0)
            parts.add(buf.toString());
        buf = new StringBuilder();
        for (int i = 0; i < parts.size() - 1; i++) {
            buf.append(parts.get(i));
            buf.append('_');
        }
        buf.append(parts.get(parts.size() - 1));
        return buf.toString().toLowerCase();
    }

    /**
     * Transforms a String in an int value. If the String is null or cannot be converted to an int, returns 0.
     * @param string to be converted.
     * @return the int value represented by the String or 0 if the value cannot be extracted.
     */
    public static int getIntVal(final String string) {
        int val = 0;
        try {
            val = Integer.valueOf(string);
        } catch (final NumberFormatException nfex) {
            // val = 0 !
        }
        return val;
    }

    /**
     * Transforms a String in a long value. If the String is null or cannot be converted to a long, returns 0.
     * @param string to be converted.
     * @return the long value represented by the String or 0 if the value cannot be extracted.
     */
    public static long getLongVal(final String string) {
        long val = 0;
        try {
            val = Long.valueOf(string);
        } catch (final NumberFormatException nfex) {
            // val = 0 !
        }
        return val;
    }

    /**
     * Transforms a String in a float value. If the String is null or cannot be converted to a float, returns 0.
     * @param string to be converted.
     * @return the float value represented by the String or 0 if the value cannot be extracted.
     */
    public static float getFloatVal(final String string) {
        float val = 0.0f;
        try {
            val = Float.valueOf(string);
        } catch (final NumberFormatException nfex) {
            // val = 0 !
        }
        return val;
    }

    /**
     * Transforms a String in a double value. If the String is null or cannot be converted to a double, returns 0.
     * @param string to be converted.
     * @return the double value represented by the String or 0 if the value cannot be extracted.
     */
    public static double getDoubleVal(final String string) {
        double val = 0.0d;
        try {
            val = Double.valueOf(string);
        } catch (final NumberFormatException nfex) {
            // val = 0 !
        }
        return val;
    }

    /**
     * Count how many time a certain substring appears inside an other string.
     * @param string to be parsed for occurrences of the substring.
     * @param substring which occurrences are to be counted.
     * @return the number of occurrences of <code>substring</code> in <code>string</code>.
     */
    public static int occurrenceCount(final String string, final String substring) {
        int count = 0;
        int index = 0;
        while (string.indexOf(substring, index) != -1) {
            count++;
            index = string.indexOf(substring, index) + 1;
            if (index > string.length())
                break;
        }
        return count;
    }

    /**
     * Returns all the letters in a given String as an array of Strings, each String representing a letter.
     * The concept of letter is used loosely here to refer to code points, but represented as Strings and not ints.
     * @param string which letters should be enumerated.
     * @return list of <code>string</code> letters.
     */
    public static List<String> toLetterList(final String string) {
        List<String> letterList = new ArrayList<String>();

        for (int i = 0; i < string.length(); i++)
            letterList.add(string.substring(i, i + 1));

        return letterList;
    }

    /**
     * Insert quotes around a String.
     * <br/>This function insert the specified quote characters around a String. It also escape any such character
     * found inside the String by having preceded by the character '\'.
     * @param string to quote.
     * @param openingChar to use for the opening quote character.
     * @param closingChar to use for the closing quote character.
     * @return the quoted String
     * @throws java.lang.NullPointerException if <code>string</code> is <code>null</code>.
     * @see Strings#quickQuote(String, String, String)
     * @see Strings#quickQuote(String)
     */
    public static String quote(String string, int openingChar, int closingChar) {
        if (string == null)
            throw new NullPointerException("String to quote cannot be null.");

        int c;
        int l = string.length();
        StringBuilder buf = new StringBuilder();

        buf.appendCodePoint(openingChar);
        for (int i = 0; i < l; i++) {
            c = string.codePointAt(i);
            if (c == openingChar || c == closingChar)
                buf.append("\\");
            buf.appendCodePoint(c);
        }
        buf.appendCodePoint(closingChar);

        return buf.toString();
    }

    /**
     * Insert quotes around a String.
     * <br/>This function insert the specified quote characters around a String. It assumes those characters are not
     * present inside the String and makes no attempt to escape them. If you expect quoting characters to be present
     * inside the String and need them escaped, you should use {@link Strings#quote(String, int, int)} instead.
     * @param string to quote.
     * @param openingQuote to use for the opening quote character(s).
     * @param closingQuote to use for the closing quote character(s).
     * @return the quoted String
     * @throws java.lang.NullPointerException if <code>string</code> is <code>null</code>.
     * @see Strings#quote(String, int, int)
     * @see Strings#quickQuote(String)
     */
    public static String quickQuote(String string, String openingQuote, String closingQuote) {
        return openingQuote + string + closingQuote;
    }

    /**
     * Insert quotes (") around a String.
     * <br/>This function inserts quotes (") around a String. It assumes there are no quotes (") present
     * inside the String and makes no attempt to escape them. If you expect quotes to be present inside the
     * String and need them escaped, you should use {@link Strings#quote(String, int, int)} instead.
     * @param string to quote.
     * @return the quoted String
     * @throws java.lang.NullPointerException if <code>string</code> is <code>null</code>.
     * @see Strings#quote(String, int, int)
     * @see Strings#quickQuote(String, String, String)
     */
    public static String quickQuote(String string) {
        return quickQuote(string, "\"", "\"");
    }

    /**
     * Takes a long or compatible value and returns a string representing that value that starts with as many zeros
     * as necessary to reach the count of characters specified by the digits parameter.
     * @param value to be displayed with leading zeros.
     * @param digits how many digits to represent the value (2-18)
     * @return a String representing value, with leading zeros.
     * @throws java.lang.IllegalArgumentException if value is negative, or if value cannot be represented
     * with the number of digits specified by <code>digits</code> or less.
     */
    public static String zeroFill(final long value, final int digits) {
        if (value < 0)
            throw new IllegalArgumentException("Illegal value " + value + " < 1");
        if (digits < 2 || digits > 18)
            throw new IllegalArgumentException("Illegal digits number: " + digits + ", must be between 2 and 19.");

        if (value == 0)
            return repeatString("0", digits);

        final long maxVal = pow10(digits);

        if (value > maxVal)
            throw new IllegalArgumentException("Illegal value " + value + " > " + maxVal);

        final StringBuilder buf = new StringBuilder();
        long curMax = maxVal;
        for (int i = digits; i >= 0; i--) {
            curMax = curMax / 10;
            if (value >= curMax)
                break;
            else
                buf.append("0");
        }
        buf.append(value);

        return buf.toString();
    }

    /**
     * Given a String returns the same String repeated n times
     * @param s the String to repeat
     * @param times how many times to repeat the String
     * @return s repeated times times
     */
    public static String repeatString(final String s, final int times) {
        if (times < 0)
            throw new IllegalArgumentException("Multiplier must be >= 0");

        final StringBuilder repeated = new StringBuilder();
        for (int i = 0; i < times; ++i)
            repeated.append(s);

        return repeated.toString();
    }

    // TODO: replace these 2 functions by a proper call to the appropriate java.math function
    private static long pow10(final int power) {
        return recursivePow10(10, power);
    }

    private static long recursivePow10(final long base, final int power) {
        if (power == 1)
            return base;

        return recursivePow10(base * 10, power - 1);
    }

    /**
     * Takes one string and removes all white space from it.
     * @param string
     * @return string without any whitespace
     */
    public static String removeWhiteSpace(final String string) {
        return string.replaceAll("\\s+", "").trim();
    }

    /**
     * Concatenate Strings together, inserting a specified separator between them.
     * @param separator to be inserted between Strings.
     * @param strings to be concatenated.
     * @return the result of the concatenation.
     */
    public static String concatWithSeparator(final String separator, final String... strings) {
        return concatWithSeparator(separator, Arrays.asList(strings));
    }

    /**
     * Takes a list of Strings and concatenate them together, inserting a specified separator between them.
     * @param separator to be inserted between Strings.
     * @param strings a list of Strings to be concatenated together.
     * @return the result of the concatenation.
     */
    public static String concatWithSeparator(final String separator, final List<String> strings) {
        return concatStringCollectionWithSeparator(separator, strings);
    }

    /**
     * Takes a Collection of Strings and concatenate them together, inserting a specified separator between them.
     * @param separator to be inserted between Strings.
     * @param strings a list of Strings to be concatenated together.
     * @return the result of the concatenation.
     */
    public static String concatWithSeparator(final String separator, final Collection<String> strings) {
        return concatStringCollectionWithSeparator(separator, strings);
    }

    private static String concatStringCollectionWithSeparator(final String separator, final Collection<String> strings) {
        if (strings.isEmpty())
            return "";

        final StringBuilder buf = new StringBuilder();

        for (String s: strings) {
            buf.append(s);
            buf.append(separator);
        }
        buf.delete(buf.length() - separator.length(), buf.length());

        return buf.toString();
    }

    /**
     * Return a List of Strings from a List of any type by calling toString().
     * On each element in the parameter List, toString() is called to create the corresponding element in the
     * returned List.
     * @param objects list of objects from which the List of Strings will be produced
     * @param <T> any java Object
     * @return a List of Strings
     */
    public static <T> List<String> asListOfStrings(final List<T> objects) {
        final List<String> strings = new ArrayList<String>();

        for (T object: objects)
            strings.add(object.toString());

        return strings;
    }

}
