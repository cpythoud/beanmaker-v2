package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Dates;
import org.beanmaker.v2.util.Money;
import org.beanmaker.v2.util.MoneyFormat;
import org.beanmaker.v2.util.SimpleInputDateFormat;
import org.beanmaker.v2.util.SimpleInputTimeFormat;
import org.beanmaker.v2.util.SimpleInputTimestampFormat;
import org.beanmaker.v2.util.Strings;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.text.DateFormat;

public interface DbBeanFormatter {

    default String formatString(String text) {
        if (text == null)
            return noData();

        return text;
    }

    default String formatString(String text, DbBeanLocalization dbBeanLocalization) {
        return formatString(text);
    }

    default String formatDate(Date date, DbBeanLocalization dbBeanLocalization) {
        if (date == null)
            return noData();

        return DateFormat.getDateInstance(DateFormat.LONG, dbBeanLocalization.getLocale()).format(date);
    }

    default String formatTime(Time time, DbBeanLocalization dbBeanLocalization) {
        if (time == null)
            return noData();

        return DateFormat.getTimeInstance(DateFormat.LONG, dbBeanLocalization.getLocale()).format(time);
    }

    default String formatTimestamp(Timestamp timestamp, DbBeanLocalization dbBeanLocalization) {
        if (timestamp == null)
            return noData();

        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, dbBeanLocalization.getLocale()).format(timestamp);
    }

    default String formatBoolean(Boolean value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return noData();

        if (value)
            return dbBeanLocalization.getLabel("true_value");

        return dbBeanLocalization.getLabel("false_value");
    }

    default String formatInteger(Integer value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return noData();

        return value.toString();
    }

    default String formatInt(Integer value, DbBeanLocalization dbBeanLocalization) {
        return formatInteger(value, dbBeanLocalization);
    }

    default String formatLong(Long value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return noData();

        return value.toString();
    }

    default String formatMoney(Money value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return noData();

        return value.toString();
    }

    default String formatBean(DbBeanInterface bean, DbBeanLocalization dbBeanLocalization) {
        if (bean == null)
            return noData();

        return bean.getNameForIdNamePairsAndTitles(dbBeanLocalization.getLanguage());
    }

    default String formatLabel(DbBeanLabel label, DbBeanLocalization dbBeanLocalization) {
        return formatLabel(label, dbBeanLocalization.getLanguage());
    }

    default String formatLabel(DbBeanLabel label, DbBeanLanguage language) {
        if (label == null || !label.hasDataFor(language))
            return noData();

        return label.get(language);
    }

    default String formatFile(DbBeanFile file, DbBeanLocalization dbBeanLocalization) {
        if (file == null)
            return noData();

        return file.getFilename();
    }

    default String formatFileLink(DbBeanFile file, DbBeanLocalization dbBeanLocalization) {
        if (file == null)
            return noData();

        return DbBeanFile.getLink(file).toString();
    }

    default String noData() {
        return "";
    }

    // *************

    default Date convertStringToDate(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getDateFromYYMD(str, "-");
    }

    default String convertDateToString(Date date) {
        if (date == null)
            return noData();

        return date.toString();
    }

    default boolean validateDateFormat(String str) {
        SimpleInputDateFormat simpleInputDateFormat = new SimpleInputDateFormat(SimpleInputDateFormat.ElementOrder.YYMD, "-");
        return simpleInputDateFormat.validate(str);
    }

    default Time convertStringToTime(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getTimeFromString(str, ":");
    }

    default String convertTimeToString(Time time) {
        if (time == null)
            return noData();

        return time.toString();
    }

    default boolean validateTimeFormat(String str) {
        SimpleInputTimeFormat simpleInputTimeFormat = new SimpleInputTimeFormat(":");
        return simpleInputTimeFormat.validate(str);
    }

    default Timestamp convertStringToTimestamp(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getTimestampFromYYMD(str, "-", ":");
    }

    default String convertTimestampToString(Timestamp timestamp) {
        if (timestamp == null)
            return noData();

        return timestamp.toString();
    }

    default boolean validateTimestampFormat(String str) {
        SimpleInputTimestampFormat simpleInputTimestampFormat = new SimpleInputTimestampFormat(SimpleInputDateFormat.ElementOrder.YYMD, "-", ":");
        return simpleInputTimestampFormat.validate(str);
    }

    // *************

    default MoneyFormat getDefaultMoneyFormat() {
        return MoneyFormat.getDefault();
    }

}
