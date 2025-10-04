package org.beanmaker.v2.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Objects;

public class DecimalValue implements Comparable<DecimalValue> {

    private final long integerPart;
    private final long fractionalPart;
    private final int decimals;
    private final boolean negative;

    private final long noDecimalMultiplier;
    private final int negativeMultiplier;

    public DecimalValue(long integerPart, long fractionalPart, int decimals, boolean negative, boolean lenient) {
        if (integerPart < 0)
            throw new IllegalArgumentException("Integer part must be non-negative");
        if (fractionalPart < 0)
            throw new IllegalArgumentException("Fractional part must be non-negative");
        if (decimals < 0)
            throw new IllegalArgumentException("Decimals must be non-negative");

        noDecimalMultiplier = calcNoDecimalMultiplier(decimals);
        negativeMultiplier = negative ? -1 : 1;

        long actualFractionalPart = fractionalPart;
        if (fractionalPart >= noDecimalMultiplier) {
            if (lenient) {
                actualFractionalPart = calcActualFractionPart(fractionalPart, decimals);
            } else {
                throw new NumberFormatException(
                        "You specified " + decimals + " decimals. Fraction part "  + fractionalPart + " is too large");
            }
        }

        this.integerPart = integerPart;
        this.fractionalPart = actualFractionalPart;
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
        return new DecimalValue(integerPart, fractionalPart, decimals, negative, false);
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
        return new DecimalValue(integerPart, fractionalPart, decimals, negative, false);
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
        return (integerPart * noDecimalMultiplier + getZeroPatchedFractionalPart()) * negativeMultiplier;
    }

    public long getZeroPatchedFractionalPart() {
        if (fractionalPart == 0)
            return 0;

        long patchedFractionalPart = fractionalPart;
        while (patchedFractionalPart * 10 < noDecimalMultiplier)
            patchedFractionalPart *= 10;
        return patchedFractionalPart;
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

    @Override
    public int compareTo(DecimalValue o) {
        return 0;
    }

}
