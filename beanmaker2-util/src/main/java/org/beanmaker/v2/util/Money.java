package org.beanmaker.v2.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
public final class Money implements Comparable<Money> {

    public static final Money ZERO = new Money(0L);

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

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
        this(value, format, 2);
    }

    /**
     * Creates a Money object with the appropriate value and associate the default printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The value will be stored as a long,
     *              representing the smallest unit of the currency (i.e., cents). The value will be rounded up
     *              or down if necessary (.5 goes up).
     */
    public Money(double value) {
        this(value, MoneyFormat.getDefault(), 2);
    }

    /**
     * Creates a Money object with the appropriate value and associate a printing format to it.
     * @param value in the main unit of the currency (i.e., dollars). The value will be stored as a long,
     *              representing the smallest unit of the currency (i.e., cents). The value will be rounded up
     *              or down if necessary (.5 goes up).
     * @param format used to print the value.
     * @param scale how many decimals the currency uses. For currencies not using the default 2 decimals. Of course
     *              the default MoneyFormatter is not appropriate for this type of currency and you should provide
     *              your own.
     * @throws java.lang.IllegalArgumentException if <code>value</code> is negative.
     */
    public Money(double value, MoneyFormat format, int scale) {
        this(convertToLong(value, scale), format);
    }

    private static long convertToLong(double value, int scale) {
        return BigDecimal.valueOf(value).multiply(BigDecimal.TEN.pow(scale)).setScale(0, RoundingMode.HALF_UP).longValue();
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
     */
    public double getDoubleVal() {
        return BigDecimal.valueOf(val)
                .divide(ONE_HUNDRED, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
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
     * @return the internal representation of the value of this MoneyFormat.
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

        BigDecimal percent = BigDecimal.valueOf(percentage).divide(ONE_HUNDRED, RoundingMode.HALF_UP);
        long result = BigDecimal.valueOf(val).multiply(percent).setScale(2, RoundingMode.HALF_UP).longValue();

        return new Money(result, format);
    }

    /**
     * Test equality of two Monwy objects. This function compares the amount AND the MoneyFormat in use. So two
     * Money object representing the same amount might not be equal if their MoneyFormat differ.
     * To assess amount equality, either pre-assess that MoneyFormat objects are identical or, preferably, use
     * compareTo()
     * @param o object to be compared to.
     * @return true if the two objects are considered equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Money))
            return false;

        Money money = (Money) o;
        return val == money.val && format.equals(money.format);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(val);
        result = 31 * result + format.hashCode();
        return result;
    }

    /**
     * Compares two Money objects based on the amount they hold. Unlike equals(), this comparison doesn't take into
     * account the associated MoneyFormat.
     * @param money amount to compare
     * @return a negative number if this object contains a smaller amount than the money parameter; a positive
     * number if this object contains a greater amount than the money parameter; 0 if this object and the
     * money parameter contain the same amount.
     */
    @Override
    public int compareTo(Money money) {
        return Long.compare(val, money.val);
    }

    /**
     * Create a Money object with the same amount as this object and a different format.
     * @param format to be used
     * @return a Money object with the same amount as this object associated to the new MoneyFormat
     */
    public Money withFormat(MoneyFormat format) {
        if (this.format.equals(format))
            return this;

        return new Money(val, format);
    }
}
