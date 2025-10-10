package org.beanmaker.v2.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DecimalValueParser {

    public static final DecimalValueParser DOT = DecimalValueParser.create(".");
    public static final DecimalValueParser COMMA = DecimalValueParser.create(",");

    private final int decimals;
    private final List<String> separators;
    private final List<String> decorators;
    private final boolean lenient;

    private DecimalValueParser(int decimals, List<String> separators, List<String> decorators, boolean lenient) {
        this.decimals = decimals;
        this.separators = separators;
        this.decorators = decorators;
        this.lenient = lenient;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DecimalValueParser create(String separator) {
        return builder().addSeparator(separator).build();
    }

    public static DecimalValueParser create(String separator, int decimals) {
        return builder().addSeparator(separator).decimals(decimals).build();
    }

    public static DecimalValueParser create(String separator, boolean lenient) {
        return builder().addSeparator(separator).lenient(lenient).build();
    }

    public static DecimalValueParser create(String separator, int decimals, boolean lenient) {
        return builder().addSeparator(separator).decimals(decimals).lenient(lenient).build();
    }

    public static DecimalValueParser from(DecimalValueParser parser, int decimals) {
        Objects.requireNonNull(parser, "source parser must not be null");
        if (parser.decimals == decimals)
            return parser;

        return createFromOtherParser(parser, decimals, parser.lenient);
    }

    public static DecimalValueParser from(DecimalValueParser parser, boolean lenient) {
        Objects.requireNonNull(parser, "source parser must not be null");
        if (parser.lenient == lenient)
            return parser;

        return createFromOtherParser(parser, parser.decimals, lenient);
    }

    public static DecimalValueParser from(DecimalValueParser parser, int decimals, boolean lenient) {
        Objects.requireNonNull(parser, "source parser must not be null");
        if (parser.decimals == decimals && parser.lenient == lenient)
            return parser;

        return createFromOtherParser(parser, decimals, lenient);
    }

    private static DecimalValueParser createFromOtherParser(DecimalValueParser parser, int decimals, boolean lenient) {
        if (decimals <= 0)
            throw new IllegalArgumentException("Decimals must be positive");

        return new DecimalValueParser(decimals, parser.separators, parser.decorators, lenient);
    }

    public DecimalValue parse(String value) {
        return parse(value, decimals, lenient);
    }

    public DecimalValue parse(String value, int decimals) {
        return parse(value, decimals, lenient);
    }

    public DecimalValue parse(String value, boolean lenient) {
        return parse(value, decimals, lenient);
    }

    public DecimalValue parse(String value, int decimals, boolean lenient) {
        Objects.requireNonNull(value);
        if (Strings.isEmpty(value))
            throw new IllegalArgumentException("String value must not be empty");

        String selectedSeparator = separators.iterator().next();
        int separatorCount = 0;
        for (String separator : separators) {
            int count = getSeparatorCount(value, separator);
            if (count > 0)
                selectedSeparator = separator;
            separatorCount += count;
        }
        if (separatorCount > 1) {
            throw new IllegalArgumentException(
                    "Too many separators (" + Strings.concatWithSeparator(", ", separators)
                            + ") in value: " + value);
        }

        return parse(value, decimals, selectedSeparator, lenient);
    }

    private DecimalValue parse(String value, int decimals, String separator, boolean lenient) {
        Objects.requireNonNull(value);
        if (Strings.isEmpty(value))
            throw new IllegalArgumentException("String value must not be empty");
        if (hasTooManySeparators(value,  separator))
            throw new IllegalArgumentException("Too many separators (" + separator + ") in value: " + value);

        boolean negative = false;
        String noSignValue;
        if (value.startsWith("-")) {
            negative = true;
            noSignValue = value.substring(1);
        } else if (value.startsWith("+")) {
            noSignValue = value.substring(1);
        } else {
            noSignValue = value;
        }

        String decoratorLessValue = noSignValue;
        for (String decorator : decorators) {
            if (decorator != null && !decorator.isEmpty())
                decoratorLessValue = decoratorLessValue.replace(decorator, "");
        }

        long integerPart = 0;
        String fractionalPart = null;
        if (decoratorLessValue.contains(separator)) {
            if (decoratorLessValue.startsWith(separator)) {
                fractionalPart = decoratorLessValue.substring(separator.length());
            } else if (decoratorLessValue.endsWith(separator)) {
                integerPart = Long.parseLong(
                        decoratorLessValue.substring(0, decoratorLessValue.length() - separator.length())
                );
            } else {
                int separatorIndex = decoratorLessValue.indexOf(separator);
                integerPart = Long.parseLong(decoratorLessValue.substring(0, separatorIndex));
                fractionalPart = decoratorLessValue.substring(separatorIndex + 1);
            }
        } else {
            integerPart = Long.parseLong(decoratorLessValue);
        }

        if (integerPart == 0)
            negative = false;  // * We do not want a negative zero

        return new DecimalValue(
                integerPart,
                calcActualFractionPart(fractionalPart, decimals, lenient),
                decimals,
                negative
        );
    }

    private static boolean hasTooManySeparators(String value, String separator) {
        return getSeparatorCount(value, separator) > 1;
    }

    private static int getSeparatorCount(String value, String separator) {
        int count = 0, from = 0;
        while ((from = value.indexOf(separator, from)) != -1) {
            count++;
            from += separator.length();
        }
        return count;
    }

    private long calcActualFractionPart(String fractionalPart, int decimals, boolean lenient) {
        if  (Strings.isEmpty(fractionalPart) || fractionalPart.matches("0+"))
            return 0;

        if (fractionalPart.length() == decimals)
            return Long.parseLong(fractionalPart);
        else if (!lenient && fractionalPart.length() > decimals)
            throw new NumberFormatException(
                "You specified " + decimals + " decimals. Fraction part "  + fractionalPart + " is too large");

        if (fractionalPart.length() < decimals)
            return zeroPadDigits(fractionalPart, decimals);

        return roundFraction(fractionalPart, decimals);
    }

    private long zeroPadDigits(String fractionPart, int decimals) {
        return Long.parseLong(fractionPart + "0".repeat(decimals - fractionPart.length()));
    }

    private long roundFraction(String fractionPart, int decimals) {
        long value = Long.parseLong(fractionPart.substring(0, decimals));
        boolean allFives = true;
        for (int i = decimals; i < fractionPart.length(); i++) {
            int digit = Integer.parseInt(fractionPart.substring(i, i + 1));
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

    public static class Builder {
        private int decimals = 2;
        private final Set<String> separators = new LinkedHashSet<>();
        private final Set<String> decorators = new LinkedHashSet<>();
        private boolean lenient = false;

        private Builder() { }

        public Builder decimals(int decimals) {
            if (decimals <= 0)
                throw new IllegalArgumentException("Decimals must be positive");

            this.decimals = decimals;
            return this;
        }

        public Builder addSeparator(String separator) {
            Objects.requireNonNull(separator, "separator must not be null");
            if (separator.isEmpty())
                throw new IllegalArgumentException("Separators must not be empty");

            separators.add(separator);
            return this;
        }

        public Builder addSeparators(Collection<String> separators) {
            for (String separator : separators)
                addSeparator(separator);

            return this;
        }

        public Builder addSeparators(String... separators) {
            return addSeparators(Arrays.asList(separators));
        }

        public Builder addDecorator(String decorator) {
            Objects.requireNonNull(decorator, "decorator must not be null");
            if (decorator.isEmpty())
                throw new IllegalArgumentException("Decorators must not be empty");

            decorators.add(decorator);
            return this;
        }

        public Builder addDecorators(Collection<String> decorators) {
            for (String decorator : decorators)
                addDecorator(decorator);

            return this;
        }

        public Builder addDecorators(String... decorators) {
            return addDecorators(Arrays.asList(decorators));
        }

        public Builder lenient(boolean lenient) {
            this.lenient = lenient;
            return this;
        }

        public DecimalValueParser build() {
            if (separators.isEmpty())
                throw new IllegalStateException("At least one separator must be provided");

            for (var separator : separators) {
                if (decorators.contains(separator)) {
                    throw new IllegalStateException(
                            "Separator also present in decorators: [" + separator + "], not allowed");
                }
            }

            return new DecimalValueParser(
                    decimals,
                    List.copyOf(separators),
                    List.copyOf(decorators),
                    lenient
            );
        }
    }

}
