package org.beanmaker.v2.util;

import java.util.ArrayList;
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
        long fractionalPart = 0;
        if (decoratorLessValue.contains(separator)) {
            if (decoratorLessValue.startsWith(separator)) {
                fractionalPart = Long.parseLong(decoratorLessValue.substring(separator.length()));
            } else if (decoratorLessValue.endsWith(separator)) {
                integerPart = Long.parseLong(
                        decoratorLessValue.substring(0, decoratorLessValue.length() - separator.length())
                );
            } else {
                int separatorIndex = decoratorLessValue.indexOf(separator);
                integerPart = Long.parseLong(decoratorLessValue.substring(0, separatorIndex));
                fractionalPart = Long.parseLong(decoratorLessValue.substring(separatorIndex + 1));
            }
        } else {
            integerPart = Long.parseLong(decoratorLessValue);
        }

        if (integerPart == 0)
            negative = false;  // * We do not want a negative zero

        return new DecimalValue(integerPart, fractionalPart, decimals, negative, lenient);
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
                    new ArrayList<>(separators),
                    new ArrayList<>(decorators),
                    lenient
            );
        }
    }

}
