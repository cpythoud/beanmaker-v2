package org.beanmaker.v2.util;

public class DurationData {

    private final int days;
    private final int hours;
    private final int minutes;

    public DurationData(int days, int hours, int minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public DurationData(int minutes) {
        days = minutes / 1440;
        hours = (minutes % 1440) / 60;
        this.minutes = minutes % 60;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getTotalMinutes() {
        return 1440 * days + 60 * hours + minutes;
    }

    public String format(
            String daySymbol,
            String hourSymbol,
            String minuteSymbol,
            boolean skipMinuteSymbol)
    {
        return format(daySymbol, hourSymbol, minuteSymbol, skipMinuteSymbol, false);
    }

    public String format(
            String daySymbol,
            String hourSymbol,
            String minuteSymbol,
            boolean skipMinuteSymbol,
            boolean showZero)
    {
        StringBuilder buf = new StringBuilder();

        if (days > 0)
            buf.append(days).append(daySymbol);

        if (hours > 0) {
            if (!buf.isEmpty())
                buf.append(" ");
            buf.append(hours).append(hourSymbol);
        }

        if (minutes > 0) {
            if (hours > 0) {
                if (skipMinuteSymbol)
                    buf.append(minutes);
                else
                    buf.append(" ").append(minutes).append(minuteSymbol);
            } else
                buf.append(minutes).append(minuteSymbol);
        }

        if (showZero && buf.isEmpty())
            return "0" + minuteSymbol;

        return buf.toString();
    }

    public String format(SimpleInputDurationFormat simpleInputDurationFormat, boolean skipMinuteSymbol) {
        return format(simpleInputDurationFormat, skipMinuteSymbol, false);
    }

    public String format(
            SimpleInputDurationFormat simpleInputDurationFormat,
            boolean skipMinuteSymbol,
            boolean showZero)
    {
        return format(
                simpleInputDurationFormat.getDaySymbol(),
                simpleInputDurationFormat.getHourSymbol(),
                simpleInputDurationFormat.getMinuteSymbol(),
                skipMinuteSymbol,
                showZero);
    }
}
