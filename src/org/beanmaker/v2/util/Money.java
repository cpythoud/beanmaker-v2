package org.beanmaker.v2.util;

/**
 * This class it a trivial implementation of a money representation to be used with values retrieved from a database.
 * It is mostly used, with the companion class {@link MoneyFormat}, in web applications related to sales or
 * online shop. It is not appropriate for financial applications.<br/>
 * In this version, negative values are not supported.<br/>
 * Values are stored as cents, or as the smallest denomination possible for the currency concerned (cents for
 * dollars or euros, centimes for Swiss Francs, etc.).<br/>
 * Internally, all information is stored as long values, so there can be no rounding errors due to floating
 * point arithmetic. This is why it is necessary to store the value as the smallest possible unit of representation
 * for the currency that interests us (i.e., cents and not dollars).<br/>
 * Objects of the Money class are immutable and therefore thread safe.
 */
public class Money {
    private final long val;

    private final MoneyFormat format;


    /**
     * Creates a Money object with the appropriate value and associate a printing format to it.
     * @param value in cents.
     * @param format used to print the value.
     * @throws java.lang.IllegalArgumentException if <code>value</code> is negative.
     */
    public Money(long value, MoneyFormat format) {
        if (value < 0)
            throw new IllegalArgumentException("Negative values not accepted (for now).");

        this.val = value;
        this.format = format;
    }

    /**
     * Creates a Money object with the appropriate value and associate the default printing format to it.
     * @param value in cents.
     */
    public Money(long value) {
        this(value, MoneyFormat.getDefault());
    }

    /**
     * Creates a Money object with the appropriate value and associate a printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The value will be stored as a long,
     *              representing the smallest unit of the currency (i.e., cents). The value will be rounded up
     *              or down if necessary (.5 goes up).
     * @param format used to print the value.
     * @throws java.lang.IllegalArgumentException if <code>value</code> is negative.
     */
    public Money(double value, MoneyFormat format) {
        this((long) (value + 0.5), format);
    }

    /**
     * Creates a Money object with the appropriate value and associate the default printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The value will be stored as a long,
     *              representing the smallest unit of the currency (i.e., cents). The value will be rounded up
     *              or down if necessary (.5 goes up).
     */
    public Money(double value) {
        this(value, MoneyFormat.getDefault());
    }

    /**
     * Creates a Money object with the appropriate value and associate a printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The String will be converted to the
     *              internal representation as a long according to the MoneyFormat provided.
     * @param format used to print the value.
     * @throws java.lang.IllegalArgumentException if <code>value</code> cannot be parsed using <code>format</code>.
     */
    public Money(String value, MoneyFormat format) {
        this(format.getVal(value), format);
    }

    /**
     * Creates a Money object with the appropriate value and associate the default printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The String will be converted to the
     *              internal representation as a long according to the default MoneyFormat.
     * @throws java.lang.IllegalArgumentException if <code>value</code> cannot be parsed using the default MoneyFormat.
     */
    public Money(String value) {
        this(MoneyFormat.getDefault().getVal(value), MoneyFormat.getDefault());
    }

    /**
     * Returns the internal representation of the value of this MoneyFormat as a long.
     * @return he internal representation of the value of this MoneyFormat.
     */
    public long getVal() {
        return val;
    }

    /**
     * Returns the internal representation of the value of this MoneyFormat as an int.
     * @return he internal representation of the value of this MoneyFormat.
     * @throws java.lang.IllegalArgumentException if the value is too big to fit in an int.
     */
    public int getIntVal() {
        if (val > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Monetary value too big to fit in an int: " + val);

        return (int) val;
    }

    /**
     * Returns the internal representation of the value of this MoneyFormat as a double.
     * This function should only be used to communicate the value to external classes and libraries
     * that expect a double value. Using this value to do any sort of calculation in your code
     * defeats the purpose of the Money class.
     * @return he internal representation of the value of this MoneyFormat.
     * @throws java.lang.IllegalArgumentException if the value is too big to fit in an int.
     */
    public double getDoubleVal() {
        return Double.valueOf((val / 100) + "." + (val % 100));
    }

    /**
     * Returns the associated MoneyFormat.
     * @return the associated MoneyFormat.
     */
    public MoneyFormat getFormat() {
        return format;
    }

    /**
     * Returns the internal representation of the value of this MoneyFormat as a String.
     * @return he internal representation of the value of this MoneyFormat.
     */
    public String toString() {
        return format.print(val);
    }

    /**
     * Returns a certain percentage of the value of this Money object as a new Money object.
     * @param percentage of value to be returned.
     * @return a Money object representing <code>percentage</code> of the current Money object.
     * @throws java.lang.IllegalArgumentException if <code>percentage</code> is not comprised between 0 and 100.
     */
    public Money getPercent(int percentage) {
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Percentage must be comprised between 0 and 100. Incorrect value: " + percentage);

        double result = val * ((float) percentage / 100);
        return new Money(Math.round(result), format);
    }
}
