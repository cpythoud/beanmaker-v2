package org.beanmaker.v2.util;

import org.javatuples.Pair;

import java.util.ArrayList;
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
        return validateAndParse(duration).getValue0();
    }

    private Pair<Boolean, List<String>> validateAndParse(String durationParameter) {
        String duration = Strings.removeWhiteSpace(durationParameter);
        List<String> results = new ArrayList<>();

        int offset = 0;

        Pair<String, Integer> dayDigitsAndOffset =
                getDigitsAndOffset(duration, offset, daySymbol, false);
        if (dayDigitsAndOffset.getValue1() == null)
            return returnFalse();
        results.add(dayDigitsAndOffset.getValue0());
        offset = dayDigitsAndOffset.getValue1();

        Pair<String, Integer> hourDigitsAndOffset =
                getDigitsAndOffset(duration, offset, hourSymbol, false);
        if (hourDigitsAndOffset.getValue1() == null)
            return returnFalse();
        results.add(hourDigitsAndOffset.getValue0());
        offset = hourDigitsAndOffset.getValue1();

        Pair<String, Integer> minuteDigitsAndOffset =
                getDigitsAndOffset(duration, offset, minuteSymbol, true);
        if (minuteDigitsAndOffset.getValue1() == null)
            return returnFalse();
        results.add(minuteDigitsAndOffset.getValue0());
        offset = minuteDigitsAndOffset.getValue1();

        if (offset != duration.length())
            return returnFalse();

        for (String result: results)
            if (!Strings.isEmpty(result))
                return new Pair<>(true, results);

        return returnFalse();
    }

    private Pair<String, Integer> getDigitsAndOffset(
            String duration,
            int offset,
            String symbol,
            boolean symbolOptional)
    {
        int pos = duration.indexOf(symbol);
        if (pos != -1) {
            if (pos == offset)
                return new Pair<>(null, offset);
            String digits = duration.substring(offset, pos);
            if (checkDigits(digits))
                return new Pair<>(digits, pos + symbol.length());

            return new Pair<>(null, offset);
        }

        if (symbolOptional) {
            String digits = duration.substring(offset);
            if (checkDigits(digits))
                return new Pair<>(digits, duration.length());
        }

        return new Pair<>("", offset);
    }

    private Pair<Boolean, List<String>> returnFalse() {
        return new Pair<>(false, new ArrayList<>());
    }

    private boolean checkDigits(String digits) {
        return DIGITS.matcher(digits).matches();
    }

    public DurationData parse(String duration) {
        Pair<Boolean, List<String>> parsedData = validateAndParse(duration);
        if (!parsedData.getValue0())
            throw new IllegalArgumentException("Illegal duration format: " + duration);

        String dayStr = parsedData.getValue1().get(0);
        String hourStr = parsedData.getValue1().get(1);
        String minuteStr = parsedData.getValue1().get(2);

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
