package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a companion to the {@link Money} class. It is used to parameter String input and output of
 * money values.
 * <br/>
 * A MoneyFormat specifies how many decimal places are to be used and which characters are accepted as
 * decimals separator and thousands decorator.
 * <br/>
 * MoneyFormat are created through a {@link MoneyFormat.Builder} object.
 */
public final class MoneyFormat {

    private final int decimals;

    private final List<String> decimalSeparators;

    private final List<String> decorators;

    private final long noDecimalMultiplier;

    private static final MoneyFormat DEFAULT_MONEY_FORMAT = (new Builder()).create();


    /**
     * Gets the default MoneyFormat.
     * The default format specifies two decimals, a decimal separator that can be either a dot or a comma, and
     * a thousands decorator that can be either a space or an apostrophe.
     * @return the default MoneyFormat.
     */
    public static MoneyFormat getDefault() {
        return DEFAULT_MONEY_FORMAT;
    }

    /**
     * Checks if a String can be transformed in a value according to the current MoneyFormat.
     * @param val the String to be checked.
     * @return true if the String <code>val</code> can be transformed into a money value, false otherwise.
     */
    public boolean isValOK(String val) {
        return !(hasSeparator() && !separatorOK(val)) && digitsOK(val);
    }

    private boolean hasSeparator() {
        return decimalSeparators.size() > 0;
    }

    private boolean separatorOK(String val) {
        return separatorCountOK(val) && separatorPlaceOK(val);
    }

    private boolean separatorCountOK(String val) {
        int count = 0;
        for (String separator: decimalSeparators)
            count += Strings.occurrenceCount(val, separator);
        return count <= 1;
    }

    private boolean separatorPlaceOK(String val) {
        for (String separator: decimalSeparators) {
            int index = val.indexOf(separator);
            if (index == -1)
                continue;
            if (index < val.length() - decimals - 1 || index == val.length() - 1)
                return false;
        }

        return true;
    }

    private boolean digitsOK(String val) {
        for (String digit: Strings.toLetterList(val)) {
            if (digit.matches("[0-9]"))
                continue;
            if (decimalSeparators.contains(digit) || decorators.contains(digit))
                continue;
            return false;
        }

        return true;
    }

    /**
     * Get a long value from a String representation of a money amount.
     * @param string representation of money amount.
     * @return money amount.
     * @throws java.lang.IllegalArgumentException if the String cannot be parsed according to the current MoneyFormat.
     */
    public long getVal(String string) {
        if (!isValOK(string))
            throw new IllegalArgumentException("Argument contains invalid characters: " + string);

        return calcValue(removeDecorators(string));
    }

    private String removeDecorators(String s) {
        if (decorators.isEmpty())
            return s;

        List<String> digits = Strings.toLetterList(s);
        StringBuilder buf = new StringBuilder();
        for (String digit: digits)
            if (!decorators.contains(digit))
                buf.append(digit);

        return buf.toString();
    }

    private long calcValue(String s) {
        if (decimals == 0)
            return getUnitValue(s);

        long unitValue = getUnitValue(s);
        long decimalValue = getDecimalValue(s);

        return unitValue * noDecimalMultiplier + decimalValue;
    }

    private long getUnitValue(String s) {
        if (decimals == 0)
            return Long.parseLong(s);

        return Long.parseLong(s.substring(0, getDecimalSymbolIndex(s)));
    }

    private int getDecimalSymbolIndex(String s) {
        int index = s.length();

        for (String separator: decimalSeparators) {
            int idx = s.indexOf(separator);
            if (idx != -1) {
                index = idx;
                break;
            }
        }

        return index;
    }

    private long getDecimalValue(String s) {
        int index = getDecimalSymbolIndex(s);
        if (index == s.length())
            return 0;

        String digits = s.substring(index + 1);
        if (digits.length() == decimals)
            return Long.parseLong(digits);

        long val = Long.parseLong(digits);
        for (int i = digits.length(); i < decimals; i++)
            val *= 10;
        return val;
    }

    /**
     * Get a String representation of a money amount.
     * @param amount to be processed.
     * @return string representation.
     * @throws java.lang.IllegalArgumentException if <code>amount</code> is negative.
     */
    public String print(long amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative.");

        if (decimals == 0)
            return getDigitsWithSeparator(amount);

        long unitVal = amount / noDecimalMultiplier;
        long decimalVal = amount - unitVal * noDecimalMultiplier;

        return getDigitsWithSeparator(unitVal) + decimalSeparators.get(0) + formatDecimals(decimalVal);
    }

    private String getDigitsWithSeparator(long val) {
        String digits = Long.toString(val);

        if (decorators.isEmpty())
            return digits;

        StringBuilder buf = new StringBuilder();
        int count = 0;
        for (int i = digits.length(); i > 0; i--) {
            buf.append(digits.charAt(i - 1));
            count++;
            if (count == 3) {
                buf.append(decorators.get(0));
                count = 0;
            }
        }
        if (count == 0)
            buf.delete(buf.length() - 1, buf.length());

        return buf.reverse().toString();
    }

    private String formatDecimals(long decimalDigits) {
        String ddStr = Long.toString(decimalDigits);

        if (ddStr.length() == decimals)
            return ddStr;

        return "0".repeat(Math.max(0, decimals - ddStr.length())) + ddStr;
    }


    private MoneyFormat(int decimals, List<String> decimalSeparators, List<String> decorators) {
        this.decimals = decimals;
        this.decimalSeparators = new ArrayList<>(decimalSeparators);
        this.decorators = new ArrayList<>(decorators);

        noDecimalMultiplier = calcNoDecimalMultiplier();
    }

    private long calcNoDecimalMultiplier() {
        long mul = 1;
        for (int i =0; i < decimals; i++)
            mul *= 10;
        return mul;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof MoneyFormat))
            return false;

        MoneyFormat format = (MoneyFormat) o;
        if (decimals != format.decimals || noDecimalMultiplier != format.noDecimalMultiplier)
            return false;
        if (decimalSeparators.size() != format.decimalSeparators.size() || decorators.size() != format.decorators.size())
            return false;
        for (int i = 0; i < decimalSeparators.size(); ++i)
            if (!decimalSeparators.get(i).equals(format.decimalSeparators.get(i)))
                return false;
        for (int i = 0; i < decorators.size(); ++i)
            if (!decorators.get(i).equals(format.decorators.get(i)))
                return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(decimals);
        result = 31 * result + Long.hashCode(noDecimalMultiplier);
        for (String decimalSeparator: decimalSeparators)
            result = 31 * result + decimalSeparator.hashCode();
        for (String decorator: decorators)
            result = 31 * result + decorator.hashCode();
        return result;
    }

    /**
     * This inner class is used to build instances of MoneyFormats.<br/>
     * An instance of this class must be created, then setters methods are used to specify the MoneyFormat.
     * Each setter function returns the Builder object, so function calls can be chained. Once all parameters
     * have been set, the {@link Builder#create()} function must be called to obtain the MoneyFormat.
     */
    public static class Builder {
        private int decimals;

        private final List<String> decimalSeparators;

        private final List<String> decorators;

        /**
         * Use this constructor to instantiate a Builder Object.
         */
        public Builder() {
            decimals = 2;

            decimalSeparators = new ArrayList<>();
            decimalSeparators.add(".");
            decimalSeparators.add(",");

            decorators = new ArrayList<>();
            decorators.add(" ");
            decorators.add("'");
        }

        /**
         * Specify the decimal separator character. Only this character will be accepted as the decimal separator.
         * @param decimals the decimal separator character.
         * @return this Builder object.
         */
        public Builder setDecimals(int decimals) {
            if (decimals < 0 || decimals > 5)
                throw new IllegalArgumentException("Number of decimal positions must be between 0 and 5.");

            this.decimals = decimals;

            return this;
        }

        /**
         * Removes all accepted decimal separators from the MoneyFormat being created.
         * @return this Builder object.
         */
        public Builder clearDecimalSeparators() {
            decimalSeparators.clear();
            return this;
        }

        /**
         * Add a decimal separator character to the list of accepted separators.
         * @param separator the decimal separator character.
         * @return this Builder object.
         */
        public Builder addDecimalSeparator(String separator) {
            if (!elementOK(separator))
                throw new IllegalArgumentException("Illegal decimal separator: " + separator);

            decimalSeparators.add(separator);

            return this;
        }

        private boolean elementOK(String element) {
            return element.length() == 1 && !element.matches("[0-9]");
        }

        /**
         * Removes all accepted thousands decorators from the MoneyFormat being created.
         * @return this Builder object.
         */
        public Builder clearDecorators() {
            decorators.clear();
            return this;
        }

        /**
         * Add a thousands decorator character to the list of accepted decortors.
         * @param decorator the thousands decorator character.
         * @return this Builder object.
         */
        public Builder addDecorator(String decorator) {
            if (!elementOK(decorator))
                throw new IllegalArgumentException("Illegal decorator: " + decorator);

            decorators.add(decorator);

            return this;
        }

        /**
         * Creates the MoneyFormat object.
         * @return a MoneyFormat object.
         * @throws java.lang.IllegalArgumentException if there is any inconsistency in the specification of the
         * MoneyFormat (missing decimal separator, decorator identical to separator, etc.)
         */
        public MoneyFormat create() {
            for (String sep: decimalSeparators)
                if (decorators.contains(sep))
                    throw new IllegalArgumentException("Decimal Separator " + sep + " is also present in decorators.");

            if (decimalSeparators.isEmpty() && decimals > 0)
                throw new IllegalArgumentException("No decimal separator specified and format takes " + decimals + " decimals.");

            if (decimals == 0 && !decimalSeparators.isEmpty())
                throw new IllegalArgumentException("Cannot specify a decimal separator when there is no decimal.");

            return new MoneyFormat(decimals, decimalSeparators, decorators);
        }
    }
}
