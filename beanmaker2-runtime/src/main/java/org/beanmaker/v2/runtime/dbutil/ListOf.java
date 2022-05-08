package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.util.Money;
import org.beanmaker.v2.util.MoneyFormat;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public final class ListOf {

    private ListOf() { }

    public static List<Boolean> booleans(ResultSet rs) throws SQLException {
        List<Boolean> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getBoolean(1));

        return list;
    }

    public static List<Integer> integers(ResultSet rs) throws SQLException {
        List<Integer> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getInt(1));

        return list;
    }

    public static List<Long> longs(ResultSet rs) throws SQLException {
        List<Long> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getLong(1));

        return list;
    }

    public static List<String> strings(ResultSet rs) throws SQLException {
        List<String> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getString(1));

        return list;
    }

    public static List<Date> dates(ResultSet rs) throws SQLException {
        List<Date> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getDate(1));

        return list;
    }

    public static List<Time> times(ResultSet rs) throws SQLException {
        List<Time> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getTime(1));

        return list;
    }

    public static List<Timestamp> timestamps(ResultSet rs) throws SQLException {
        List<Timestamp> list = new ArrayList<>();

        while (rs.next())
            list.add(rs.getTimestamp(1));

        return list;
    }

    public static List<Money> monies(ResultSet rs) throws SQLException {
        return monies(rs, MoneyFormat.getDefault());
    }

    public static List<Money> monies(ResultSet rs, MoneyFormat moneyFormat) throws SQLException {
        List<Money> list = new ArrayList<>();

        while (rs.next())
            list.add(new Money(rs.getLong(1), moneyFormat));

        return list;
    }
    
}
