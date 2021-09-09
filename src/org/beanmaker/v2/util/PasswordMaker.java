package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ...
 */
public class PasswordMaker {

    private final int minChars;
    private final int maxChars;

    private final String mainChars;
    private final List<String> altCharGroups;

    private final List<Integer> altCharMins;
    private final List<Integer> altCharMax;

    private final PasswordMakerRandomUIntGenerator randomUIntGenerator;

    //private final Random random = new Random();

    private PasswordMaker(
            final int minChars,
            final int maxChars,
            final String mainChars,
            final List<String> altCharGroups,
            final List<Integer> altCharMins,
            final List<Integer> altCharMax,
            final PasswordMakerRandomUIntGenerator randomUIntGenerator)
    {
        this.minChars = minChars;
        this.maxChars = maxChars;
        this.mainChars = mainChars;
        this.altCharGroups = altCharGroups;
        this.altCharMins = altCharMins;
        this.altCharMax = altCharMax;
        this.randomUIntGenerator = randomUIntGenerator;
    }

    public static Factory getFactory() {
        return new Factory();
    }

    public static class Factory {
        private int minChars = 12;
        private int maxChars = 12;

        private String mainChars = PasswordMakerCharacterSets.LOWER_CASES + PasswordMakerCharacterSets.UPPER_CASES;
        private List<String> altCharGroups;

        private List<Integer> altCharMins;
        private List<Integer> altCharMax;

        private PasswordMakerRandomUIntGenerator randomUIntGenerator;

        public Factory setCharCount(final int count) {
            return setMinMaxChars(count, count);
        }

        public Factory setMinMaxChars(final int minChars, final int maxChars) {
            if (minChars < 2)
                throw new IllegalArgumentException("minChars must be 4 or less; value received = " + minChars);
            if (maxChars < minChars)
                throw new IllegalArgumentException("maxChars cannont be smaller than minChars: " + maxChars + " < " + minChars);

            this.minChars = minChars;
            this.maxChars = maxChars;

            return this;
        }

        public Factory setMainChars(final String mainChars) {
            if (mainChars == null)
                throw new NullPointerException("mainChars cannot be null");
            if (mainChars.length() < 2)
                throw new IllegalArgumentException("mainChars must contained at least 4 characters (current count is " + mainChars.length() + ")");

            this.mainChars = mainChars;

            return this;
        }

        public Factory addAltCharGroup(final String chars, final int minChars, final int maxChars) {
            if (chars == null)
                throw new NullPointerException("chars cannot be null");
            if (chars.isEmpty())
                throw new IllegalArgumentException("chars must contained at least one character");
            if (minChars < 1)
                throw new IllegalArgumentException("minChars must be 1 or less; value received = " + minChars);
            if (maxChars < minChars)
                throw new IllegalArgumentException("maxChars cannont be smaller than minChars: " + maxChars + " < " + minChars);

            if (altCharGroups == null) {
                altCharGroups = new ArrayList<String>();
                altCharMins = new ArrayList<Integer>();
                altCharMax = new ArrayList<Integer>();
            }

            altCharGroups.add(chars);
            altCharMins.add(minChars);
            altCharMax.add(maxChars);

            return this;
        }

        public Factory setRandomUIntGenerator(final PasswordMakerRandomUIntGenerator randomUIntGenerator) {
            this.randomUIntGenerator = randomUIntGenerator;

            return this;
        }

        public PasswordMaker create() {
            if (altCharGroups == null)
                return new PasswordMaker(
                        minChars,
                        maxChars,
                        mainChars,
                        null,
                        null,
                        null,
                        randomUIntGenerator == null ? new Java6RandomUIntGenerator() : randomUIntGenerator);

            if (extraCharsMaxSumIsMoreThanMinChars())
                throw new IllegalStateException("Sum of possible optional characters is more than minimum characters in password.");

            return new PasswordMaker(
                    minChars,
                    maxChars,
                    mainChars,
                    Collections.unmodifiableList(altCharGroups),
                    Collections.unmodifiableList(altCharMins),
                    Collections.unmodifiableList(altCharMax),
                    randomUIntGenerator == null ? new Java6RandomUIntGenerator() : randomUIntGenerator);
        }

        private boolean extraCharsMaxSumIsMoreThanMinChars() {
            int sum = 0;
            for (int max: altCharMax)
                sum += max;
            return sum > minChars;
        }

        public Factory reset() {
            minChars = 12;
            maxChars = 12;

            mainChars = PasswordMakerCharacterSets.LOWER_CASES + PasswordMakerCharacterSets.UPPER_CASES;
            altCharGroups = null;

            altCharMins = null;
            altCharMax = null;

            return this;
        }
    }

    public String getPassword() {
        final Password password = new Password(getRandomIntBetween(minChars, maxChars));

        if (altCharGroups != null) {
            final int groups = altCharGroups.size();
            for (int index = 0; index < groups; ++index)
                populateChars(password, altCharGroups.get(index), getRandomIntBetween(altCharMins.get(index), altCharMax.get(index)));
        }

        for (int index: password.getUnsetIndexes())
            password.setCharAt(index, getRandomChar(mainChars));

        return password.toString();
    }

    private int getRandomIntBetween(final int start, final int end) {
        assert (start >= 0) : "start < 0; start = " + start;
        assert (end >= start) : "end < start; start = " + start + ", end = " + end;

        if (start == end)
            return start;

        if (start == 0)
            return randomUIntGenerator.getNextUInt(end);

        return start + randomUIntGenerator.getNextUInt(end - start + 1);
    }

    private void populateChars(final Password password, final String chars, final int count) {
        final List<Integer> availableIndexes = password.getUnsetIndexes();
        for (int i = 0; i < count; ++i) {
            final int index = randomUIntGenerator.getNextUInt(availableIndexes.size());
            password.setCharAt(availableIndexes.get(index), getRandomChar(chars));
            availableIndexes.remove(index);
        }
    }

    private String getRandomChar(final String chars) {
        final int index = randomUIntGenerator.getNextUInt(chars.length());
        return chars.substring(index, index + 1);
    }

    private static class Password {
        private final List<String> chars;

        Password(final int length) {
            chars = new ArrayList<String>();
            for (int i = 0; i < length; ++i)
                chars.add("");
        }

        void setCharAt(final int index, final String character) {
            chars.set(index, character);
        }

        List<Integer> getUnsetIndexes() {
            final List<Integer> indexes = new ArrayList<Integer>();

            int index = 0;
            for (String character: chars) {
                if (character.isEmpty())
                    indexes.add(index);
                ++index;
            }

            return indexes;
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();

            for (String character: chars)
                buf.append(character);

            return buf.toString();
        }
    }
}
