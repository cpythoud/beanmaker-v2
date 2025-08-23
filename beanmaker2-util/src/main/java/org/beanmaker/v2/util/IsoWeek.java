package org.beanmaker.v2.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class IsoWeek {

    private final int year;
    private final int week;
    private final LocalDate monday; // anchor
    private final LocalDate sunday;

    public IsoWeek(int year, int week) {
        int maxWeeks = getWeekCount(year);
        if (week < 1 || week > maxWeeks) {
            throw new IllegalArgumentException(
                    "Invalid ISO week " + week + " for ISO year " + year + " (1.." + maxWeeks + ")"
            );
        }
        this.year = year;
        this.week = week;
        this.monday = getMonday(year, week);
        this.sunday = this.monday.with(WeekFields.ISO.dayOfWeek(), 7);
    }

    public static boolean isValid(int year, int week) {
        int maxWeeks = getWeekCount(year);
        return week >= 1 && week <= maxWeeks;
    }

    public static int getWeekCount(int year) {
        // * Dec 28 is always in the last ISO week of its week-based-year
        var dec28 = LocalDate.of(year, 12, 28);
        return dec28.get(WeekFields.ISO.weekOfWeekBasedYear());
    }

    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public static LocalDate getMonday(int year, int week) {
        // * Jan 4 is always in ISO week 1 of its week-based-year
        var base = LocalDate.of(year, 1, 4);
        return base.with(WeekFields.ISO.weekOfWeekBasedYear(), week)
                .with(WeekFields.ISO.dayOfWeek(), 1); // * 1 = Monday
    }

    public static LocalDate getSunday(int year, int week) {
        return getMonday(year, week)
                .with(WeekFields.ISO.dayOfWeek(), 7); // * 7 = Sunday
    }

    public static List<IsoWeek> getWeeks(int year) {
        var weeks = new ArrayList<IsoWeek>();
        int maxWeeks = getWeekCount(year);
        for (int week = 1; week <= maxWeeks; week++)
            weeks.add(new IsoWeek(year, week));

        return weeks;
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }

    public LocalDate getMonday() {
        return monday;
    }

    public LocalDate getSunday() {
        return sunday;
    }

    public LocalDate getDay(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        return monday.with(WeekFields.ISO.dayOfWeek(), dayOfWeek.getValue());
    }

    public List<LocalDate> getDays() {
        var days = new ArrayList<LocalDate>(7);
        for (int i = 0; i < 7; i++) {
            days.add(monday.plusDays(i));
        }
        return days;
    }

    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return !date.isBefore(monday) && !date.isAfter(sunday);
    }

    public String print(FormatStyle formatStyle, Locale locale) {
        Objects.requireNonNull(formatStyle, "formatStyle");
        Objects.requireNonNull(locale, "locale");
        return DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(locale).format(monday)
                + " - "
                + DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(locale).format(sunday);
    }

    @Override
    public String toString() {
        return "IsoWeek{year=" + year + ", week=" + week + ", monday=" + monday + ", sunday=" + sunday + '}';
    }

}
