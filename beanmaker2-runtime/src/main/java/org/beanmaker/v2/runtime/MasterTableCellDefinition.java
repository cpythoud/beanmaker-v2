package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Money;
import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.HtmlCodeFragment;
import org.jcodegen.html.Tag;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class MasterTableCellDefinition {

    private final String fieldName;
    private final String content;
    private String orderingValue = null;
    private String filteringValue = null;
    private String sumValue = null;
    private String extraCssClasses = null;

    public MasterTableCellDefinition(String fieldName, String content) {
        this.fieldName = fieldName;
        this.content = content;
    }

    public MasterTableCellDefinition(String fieldName, Tag htmlContent) {
        this(fieldName, htmlContent.toString());
    }

    public MasterTableCellDefinition(String fieldName, HtmlCodeFragment htmlContent) {
        this(fieldName, htmlContent.toString());
    }

    public static MasterTableCellDefinition createTextCellDefinition(String fieldName, String text) {
        return new MasterTableCellDefinition(fieldName, text);
    }

    public static MasterTableCellDefinition createDateCellDefinition(String fieldName, String text, Date date) {
        return new MasterTableCellDefinition(fieldName, text).orderingValue(date);
    }

    public static MasterTableCellDefinition createTimeCellDefinition(String fieldName, String text, Time time) {
        return new MasterTableCellDefinition(fieldName, text).orderingValue(time);
    }

    public static MasterTableCellDefinition createTimestampCellDefinition(String fieldName, String text, Timestamp timestamp) {
        return new MasterTableCellDefinition(fieldName, text).orderingValue(timestamp);
    }

    public static MasterTableCellDefinition createBooleanCellDefinition(
            String fieldName,
            String text,
            boolean value,
            String yesValue,
            String noValue)
    {
        return new MasterTableCellDefinition(fieldName, text)
                .orderingValue(value, yesValue, noValue)
                .filteringValue(value, yesValue, noValue);
    }

    public static MasterTableCellDefinition createIntegerCellDefinition(
            String fieldName,
            String text,
            long value,
            int zeroFilledMaxDigits)
    {
        return new MasterTableCellDefinition(fieldName, text).orderingValue(value, zeroFilledMaxDigits);
    }

    public static MasterTableCellDefinition createMoneyCellDefinition(
            String fieldName,
            String text,
            Money value,
            int zeroFilledMaxDigits)
    {
        return new MasterTableCellDefinition(fieldName, text).orderingValue(value, zeroFilledMaxDigits);
    }

    public static MasterTableCellDefinition createEmptyCellDefinition(String fieldName) {
        return new MasterTableCellDefinition(fieldName, "");
    }

    public String fieldName() {
        return fieldName;
    }

    public String content() {
        return content;
    }

    public String orderingValue() {
        return orderingValue;
    }

    public boolean orderingDefined() {
        return orderingValue != null;
    }

    public MasterTableCellDefinition orderingValue(String orderingValue) {
        this.orderingValue = orderingValue;
        return this;
    }

    public MasterTableCellDefinition orderingValue(Date orderingValue) {
        if (orderingValue == null)
            this.orderingValue = null;
        else
            this.orderingValue = orderingValue.toString();
        return this;
    }

    public MasterTableCellDefinition orderingValue(Time orderingValue) {
        if (orderingValue == null)
            this.orderingValue = null;
        else
            this.orderingValue = orderingValue.toString();
        return this;
    }

    public MasterTableCellDefinition orderingValue(Timestamp orderingValue) {
        if (orderingValue == null)
            this.orderingValue = null;
        else
            this.orderingValue = orderingValue.toString();
        return this;
    }

    public MasterTableCellDefinition orderingValue(boolean orderingValue, String yesValue, String noValue) {
        this.orderingValue = orderingValue ? yesValue : noValue;
        return this;
    }

    public MasterTableCellDefinition orderingValue(long orderingValue, int zeroFilledMaxDigits) {
        this.orderingValue = Strings.zeroFill(orderingValue, zeroFilledMaxDigits);
        return this;
    }

    public MasterTableCellDefinition orderingValue(Money orderingValue, int zeroFilledMaxDigits) {
        this.orderingValue = Strings.zeroFill(orderingValue.getVal(), zeroFilledMaxDigits);
        return this;
    }

    public String filteringValue() {
        return filteringValue;
    }

    public boolean filteringDefined() {
        return filteringValue != null;
    }

    public MasterTableCellDefinition filteringValue(String filteringValue) {
        this.filteringValue = filteringValue;
        return this;
    }

    public MasterTableCellDefinition filteringValue(boolean filteringValue, String yesValue, String noValue) {
        this.filteringValue = filteringValue ? yesValue : noValue;
        return this;
    }

    public String sumValue() {
        return sumValue;
    }

    public boolean sumDefined() {
        return sumValue != null;
    }

    public MasterTableCellDefinition sumValue(String sumValue) {
        this.sumValue = sumValue;
        return this;
    }

    public MasterTableCellDefinition sumValue(Money sumValue) {
        this.sumValue = Double.toString(sumValue.getDoubleVal());
        return this;
    }

    public String extraCssClasses() {
        return extraCssClasses;
    }

    public MasterTableCellDefinition extraCssClasses(String extraCssClasses) {
        this.extraCssClasses = extraCssClasses;
        return this;
    }

}
