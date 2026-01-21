package org.beanmaker.v2.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Objects;

public final class DecimalValue implements Comparable<DecimalValue> {

    private final long integerPart;
    private final long fractionalPart;
    private final int decimals;
    private final boolean negative;

    private final long noDecimalMultiplier;
    private final int negativeMultiplier;

    public DecimalValue(long integerPart, long fractionalPart, int decimals, boolean negative) {
        if (integerPart < 0)
            throw new IllegalArgumentException("Integer part must be non-negative");
        if (fractionalPart < 0)
            throw new IllegalArgumentException("Fractional part must be non-negative");
        if (decimals < 0)
            throw new IllegalArgumentException("Decimals must be non-negative");

        noDecimalMultiplier = calcNoDecimalMultiplier(decimals);
        negativeMultiplier = negative ? -1 : 1;

        if (fractionalPart >= noDecimalMultiplier)
            throw new NumberFormatException(
                    "You specified " + decimals + " decimals. Fraction part "  + fractionalPart + " is too large");

        this.integerPart = integerPart;
        this.fractionalPart = fractionalPart;
        this.decimals = decimals;
        this.negative = negative;
    }

    private static long calcNoDecimalMultiplier(int decimals) {  // * Math.pow() requires doubles and I don't want to go there
        long mul = 1;
        for (int i = 0; i < decimals; i++)
            mul *= 10;
        return mul;
    }

    private long calcActualFractionPart(long fractionalPart, int decimals) {
        String digits = String.valueOf(fractionalPart);
        long value = Long.parseLong(digits.substring(0, decimals));
        boolean allFives = true;
        for (int i = decimals; i < digits.length(); i++) {
            int digit = Integer.parseInt(digits.substring(i, i + 1));
            if (digit != 5) {
                allFives = false;
                if (digit > 5)
                    ++value;
                break;
            }
        }
        if (allFives)
            ++value;
        return value;
    }

    public static DecimalValue from(BigDecimal bigDecimal, int decimals) {
        Objects.requireNonNull(bigDecimal);
        if (decimals < 0)
            throw new IllegalArgumentException("Decimals must be non-negative");

        boolean negative = bigDecimal.signum() < 0;
        var scaled = bigDecimal.abs().setScale(decimals, RoundingMode.HALF_UP);
        long integerPart = scaled.longValue();
        long fractionalPart = scaled.remainder(BigDecimal.ONE)
                .movePointRight(decimals)
                .abs()
                .longValueExact();
        return new DecimalValue(integerPart, fractionalPart, decimals, negative);
    }

    public static DecimalValue from(long value, int decimals) {
        return createDecimalValue(value, decimals);
    }

    public static DecimalValue from(int value, int decimals) {
        return createDecimalValue(value, decimals);
    }

    private static DecimalValue createDecimalValue(long value, int decimals) {
        boolean negative = value < 0;
        long absValue = Math.abs(value);
        long noDecimalMultiplier = calcNoDecimalMultiplier(decimals);
        long integerPart = absValue / noDecimalMultiplier;
        long fractionalPart = absValue % noDecimalMultiplier;
        return new DecimalValue(integerPart, fractionalPart, decimals, negative);
    }

    public long getIntegerPart() {
        return integerPart;
    }

    public long getFractionalPart() {
        return fractionalPart;
    }

    public int getDecimals() {
        return decimals;
    }

    public boolean isNegative() {
        return negative;
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(DecimalValueFormat.DOT.format(this));
    }

    public long toLong() {
        return (integerPart * noDecimalMultiplier + fractionalPart) * negativeMultiplier;
    }

    public int toInt() {
        long l = toLong();
        if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
            throw new NumberFormatException("DecimalValue cannot be converted to int");
        return (int) l;
    }

    @Override
    public String toString() {
        return "DecimalValue{" +
                "integerPart=" + integerPart +
                ", decimalPart=" + fractionalPart +
                ", decimals=" + decimals +
                ", negative=" + negative +
                '}';
    }

    /**
     * Compares this DecimalValue with another for order. Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.
     * <p>
     * <strong>Important:</strong> Both DecimalValue instances must have the same number of decimals.
     * Attempting to compare instances with different decimal precision will result in an IllegalArgumentException.
     * </p>
     *
     * @param decimalValue the DecimalValue to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     *         or greater than the specified object
     * @throws IllegalArgumentException if the decimal precision differs between the two instances
     */
    @Override
    public int compareTo(DecimalValue decimalValue) {
        if (this.decimals != decimalValue.decimals) {
            throw new IllegalArgumentException(
                    "Cannot compare DecimalValue instances with different decimal precision: " +
                    this.decimals + " vs " + decimalValue.decimals);
        }
        
        int signComparison = Boolean.compare(this.negative, decimalValue.negative);
        if (signComparison != 0) {
            return -signComparison; // * Negative values are less than positive
        }
        
        int intComparison = Long.compare(this.integerPart, decimalValue.integerPart);
        if (intComparison != 0) {
            return this.negative ? -intComparison : intComparison;
        }
        
        int fracComparison = Long.compare(this.fractionalPart, decimalValue.fractionalPart);
        return this.negative ? -fracComparison : fracComparison;
    }

    /**
     * Compares the numerical values of two DecimalValue instances, ignoring decimal precision.
     * This method converts both values to BigDecimal for comparison.
     *
     * @param other the DecimalValue to compare with
     * @return a negative integer, zero, or a positive integer as this object is numerically less than,
     *         equal to, or greater than the specified object
     */
    public int compareNumerically(DecimalValue other) {
        return this.toBigDecimal().compareTo(other.toBigDecimal());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) 
            return false;
        
        DecimalValue that = (DecimalValue) o;
        return integerPart == that.integerPart 
                && fractionalPart == that.fractionalPart 
                && decimals == that.decimals 
                && negative == that.negative;
    }

    @Override
    public int hashCode() {
        return Objects.hash(integerPart, fractionalPart, decimals, negative);
    }
    
}
