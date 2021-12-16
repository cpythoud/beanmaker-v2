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

public abstract class DbBeanFormatter {

    public String formatDate(Date date, DbBeanLocalization dbBeanLocalization) {
        return DateFormat.getDateInstance(DateFormat.LONG, dbBeanLocalization.getLocale()).format(date);
    }

    public String formatTime(Time time, DbBeanLocalization dbBeanLocalization) {
        if (time == null)
            return "";

        return DateFormat.getTimeInstance(DateFormat.LONG, dbBeanLocalization.getLocale()).format(time);
    }

    public String formatTimestamp(Timestamp timestamp, DbBeanLocalization dbBeanLocalization) {
        if (timestamp == null)
            return "";

        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, dbBeanLocalization.getLocale()).format(timestamp);
    }

    public String formatBoolean(Boolean value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return "";

        if (value)
            return dbBeanLocalization.getLabel("true_value");

        return dbBeanLocalization.getLabel("false_value");
    }

    public String formatInt(Integer value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return "";

        return value.toString();
    }

    public String formatLong(Long value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return "";

        return value.toString();
    }

    public String formatMoney(Money value, DbBeanLocalization dbBeanLocalization) {
        if (value == null)
            return "";

        return value.toString();
    }

    // *************

    public Date convertStringToDate(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getDateFromYYMD(str, "-");
    }

    public String convertDateToString(Date date) {
        if (date == null)
            return "";

        return date.toString();
    }

    public boolean validateDateFormat(String str) {
        SimpleInputDateFormat simpleInputDateFormat = new SimpleInputDateFormat(SimpleInputDateFormat.ElementOrder.YYMD, "-");
        return simpleInputDateFormat.validate(str);
    }

    public Time convertStringToTime(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getTimeFromString(str, ":");
    }

    public String convertTimeToString(Time time) {
        if (time == null)
            return "";

        return time.toString();
    }

    public boolean validateTimeFormat(String str) {
        SimpleInputTimeFormat simpleInputTimeFormat = new SimpleInputTimeFormat(":");
        return simpleInputTimeFormat.validate(str);
    }

    public Timestamp convertStringToTimestamp(String str) {
        if (Strings.isEmpty(str))
            return null;

        return Dates.getTimestampFromYYMD(str, "-", ":");
    }

    public String convertTimestampToString(Timestamp timestamp) {
        if (timestamp == null)
            return "";

        return timestamp.toString();
    }

    public boolean validateTimestampFormat(String str) {
        SimpleInputTimestampFormat simpleInputTimestampFormat = new SimpleInputTimestampFormat(SimpleInputDateFormat.ElementOrder.YYMD, "-", ":");
        return simpleInputTimestampFormat.validate(str);
    }

    // *************

    public MoneyFormat getDefaultMoneyFormat() {
        return MoneyFormat.getDefault();
    }

}
