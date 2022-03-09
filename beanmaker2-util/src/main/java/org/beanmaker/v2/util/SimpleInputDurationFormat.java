package org.beanmaker.v2.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.regex.Pattern;

public class SimpleInputDurationFormat {

    private static final Pattern DIGITS =  Pattern.compile("\\d+");

    private final String daySymbol;
    private final String hourSymbol;
    private final String minuteSymbol;

    public SimpleInputDurationFormat(String daySymbol, String hourSymbol, String minuteSymbol) {
        this.daySymbol = daySymbol;
        this.hourSymbol = hourSymbol;
        this.minuteSymbol = minuteSymbol;
    }

    public String getDaySymbol() {
        return daySymbol;
    }

    public String getHourSymbol() {
        return hourSymbol;
    }

    public String getMinuteSymbol() {
        return minuteSymbol;
    }

    public boolean validate(String duration) {
        return validateAndParse(duration).ok();
    }

    private static record ValidationResults(boolean ok, List<String> results) { }
    private static record DigitsAndOffset(String digits, int offset) { }

    private ValidationResults validateAndParse(String durationParameter) {
        String duration = Strings.removeWhiteSpace(durationParameter);
        List<String> results = new ArrayList<>();

        int offset = 0;

        DigitsAndOffset dayDigitsAndOffset =
                getDigitsAndOffset(duration, offset, daySymbol, false);
        if (dayDigitsAndOffset.digits() == null)
            return returnFalse();
        results.add(dayDigitsAndOffset.digits());
        offset = dayDigitsAndOffset.offset();

        DigitsAndOffset hourDigitsAndOffset =
                getDigitsAndOffset(duration, offset, hourSymbol, false);
        if (hourDigitsAndOffset.digits() == null)
            return returnFalse();
        results.add(hourDigitsAndOffset.digits());
        offset = hourDigitsAndOffset.offset();

        DigitsAndOffset minuteDigitsAndOffset =
                getDigitsAndOffset(duration, offset, minuteSymbol, true);
        if (minuteDigitsAndOffset.digits() == null)
            return returnFalse();
        results.add(minuteDigitsAndOffset.digits());
        offset = minuteDigitsAndOffset.offset();

        if (offset != duration.length())
            return returnFalse();

        for (String result: results)
            if (!Strings.isEmpty(result))
                return new ValidationResults(true, results);

        return returnFalse();
    }

    private DigitsAndOffset getDigitsAndOffset(
            String duration,
            int offset,
            String symbol,
            boolean symbolOptional)
    {
        int pos = duration.indexOf(symbol);
        if (pos != -1) {
            if (pos == offset)
                return new DigitsAndOffset(null, offset);
            String digits = duration.substring(offset, pos);
            if (checkDigits(digits))
                return new DigitsAndOffset(digits, pos + symbol.length());

            return new DigitsAndOffset(null, offset);
        }

        if (symbolOptional) {
            String digits = duration.substring(offset);
            if (checkDigits(digits))
                return new DigitsAndOffset(digits, duration.length());
        }

        return new DigitsAndOffset("", offset);
    }

    private ValidationResults returnFalse() {
        return new ValidationResults(false, Collections.emptyList());
    }

    private boolean checkDigits(String digits) {
        return DIGITS.matcher(digits).matches();
    }

    public DurationData parse(String duration) {
        ValidationResults parsedData = validateAndParse(duration);
        if (!parsedData.ok())
            throw new IllegalArgumentException("Illegal duration format: " + duration);

        String dayStr = parsedData.results().get(0);
        String hourStr = parsedData.results().get(1);
        String minuteStr = parsedData.results().get(2);

        int days;
        if (Strings.isEmpty(dayStr))
            days = 0;
        else
            days = Integer.parseInt(dayStr);

        int hours;
        if (Strings.isEmpty(hourStr))
            hours = 0;
        else
            hours = Integer.parseInt(hourStr);

        int minutes;
        if (Strings.isEmpty(minuteStr))
            minutes = 0;
        else
            minutes = Integer.parseInt(minuteStr);

        return new DurationData(days, hours, minutes);
    }

}
