package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Dates;
import org.beanmaker.v2.util.DecimalValue;
import org.beanmaker.v2.util.Money;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Objects;

public class FieldEquality {

    public static boolean areEqual(long id1, long id2) {
        return id1 == id2;
    }

    public static boolean areEqual(Boolean field1, Boolean field2) {
        return Objects.equals(field1, field2);
    }

    public static boolean areEqual(Integer field1, Integer field2) {
        return Objects.equals(field1, field2);
    }

    public static boolean areEqual(Long field1, Long field2) {
        return Objects.equals(field1, field2);
    }

    public static boolean areEqual(String field1, String field2) {
        return Objects.equals(field1, field2);
    }

    public static boolean areEqual(Date field1, Date field2) {
        if (field1 == null || field2 == null)
            return field1 == null && field2 == null;

        return Dates.compare(field1, field2) == 0;
    }

    public static boolean areEqual(Time field1, Time field2) {
        if (field1 == null || field2 == null)
            return field1 == null && field2 == null;

        return Dates.compare(field1, field2) == 0;
    }

    public static boolean areEqual(Timestamp field1, Timestamp field2) {
        return Objects.equals(field1, field2);
    }

    public static boolean areEqual(Money field1, Money field2) {
        if (field1 == null || field2 == null)
            return field1 == null && field2 == null;

        return field1.compareTo(field2) == 0;
    }

    public static boolean areEqual(DecimalValue field1, DecimalValue field2) {
        if (field1 == null || field2 == null)
            return field1 == null && field2 == null;

        return field1.compareTo(field2) == 0;
    }

}
