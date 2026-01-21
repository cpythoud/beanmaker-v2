package org.beanmaker.v2.util;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class DecimalValueFormat {

    public static final DecimalValueFormat DOT = new DecimalValueFormat(".");
    public static final DecimalValueFormat COMMA = new DecimalValueFormat(",");

    private final String separator;
    private final String decorator;

    public DecimalValueFormat(String separator) {
        this(separator, null);
    }

    public DecimalValueFormat(String separator, String decorator) {
        Objects.requireNonNull(separator, "separator cannot be null");
        if (separator.isEmpty())
            throw new IllegalArgumentException("separator cannot be empty");

        this.separator = separator;
        if (decorator == null || decorator.isEmpty())
            this.decorator = null;
        else
            this.decorator = decorator;
    }

    public String getSeparator() {
        return separator;
    }

    public Optional<String> getDecorator() {
        return Optional.ofNullable(decorator);
    }

    public String format(DecimalValue value) {
        if (value.getDecimals() == 0)
            return getSign(value) + formatIntegerPart(value);

        return getSign(value) + formatIntegerPart(value) + separator + formatFractionalPart(value);
    }

    private String getSign(DecimalValue value) {
        return value.isNegative() ? "-" : "";
    }

    private String formatIntegerPart(DecimalValue value) {
        String digits = Long.toString(value.getIntegerPart());

        if (decorator == null)
            return digits;

        StringBuilder buf = new StringBuilder();
        int count = 0;
        for (int i = digits.length(); i > 0; i--) {
            buf.append(digits.charAt(i - 1));
            count++;
            if (count == 3) {
                buf.append(decorator);
                count = 0;
            }
        }
        if (count == 0)
            buf.delete(buf.length() - 1, buf.length());

        return buf.reverse().toString();
    }

    private String formatFractionalPart(DecimalValue value) {
        String digits = Long.toString(value.getFractionalPart());

        if (digits.length() == value.getDecimals())
            return digits;

        return "0".repeat(Math.max(0, value.getDecimals() - digits.length())) + digits;
    }

    public String scrapeTrailingZeros(DecimalValue value) {
        String formatted = format(value);
        var parts = formatted.split(Pattern.quote(separator));
        if (parts.length == 1)
            return formatted;
        if (parts.length != 2)
            throw new AssertionError("Invalid/impossible format: " + formatted);

        String fractionalPart = parts[1];
        while (fractionalPart.endsWith("0"))
            fractionalPart = fractionalPart.substring(0, fractionalPart.length() - 1);

        if (fractionalPart.isEmpty())
            return parts[0];

        return parts[0] + separator + fractionalPart;
    }

}
