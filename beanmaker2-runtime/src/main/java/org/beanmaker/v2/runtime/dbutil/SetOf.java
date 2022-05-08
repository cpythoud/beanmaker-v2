package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.util.Money;
import org.beanmaker.v2.util.MoneyFormat;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.HashSet;
import java.util.Set;

public final class SetOf {

    private SetOf() { }

    public static Set<Boolean> booleans(ResultSet rs) throws SQLException {
        Set<Boolean> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getBoolean(1));

        return set;
    }

    public static Set<Integer> integers(ResultSet rs) throws SQLException {
        Set<Integer> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getInt(1));

        return set;
    }

    public static Set<Long> longs(ResultSet rs) throws SQLException {
        Set<Long> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getLong(1));

        return set;
    }

    public static Set<String> strings(ResultSet rs) throws SQLException {
        Set<String> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getString(1));

        return set;
    }

    public static Set<Date> dates(ResultSet rs) throws SQLException {
        Set<Date> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getDate(1));

        return set;
    }

    public static Set<Time> times(ResultSet rs) throws SQLException {
        Set<Time> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getTime(1));

        return set;
    }

    public static Set<Timestamp> timestamps(ResultSet rs) throws SQLException {
        Set<Timestamp> set = new HashSet<>();

        while (rs.next())
            set.add(rs.getTimestamp(1));

        return set;
    }

    public static Set<Money> monies(ResultSet rs) throws SQLException {
        return monies(rs, MoneyFormat.getDefault());
    }

    public static Set<Money> monies(ResultSet rs, MoneyFormat moneyFormat) throws SQLException {
        Set<Money> list = new HashSet<>();

        while (rs.next())
            list.add(new Money(rs.getLong(1), moneyFormat));

        return list;
    }
    
}
