package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Money;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;
import org.dbbeans.sql.SQLRuntimeException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.function.Function;

public final class DBUtil {

    public static Optional<ResultSet> getInitResultSet(long id, DbBeanParameters parameters, DBAccess dbAccess) {
        return Optional.ofNullable(
                dbAccess.processQuery(
                        "SELECT " + parameters.getDatabaseFieldList() + " FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                        stat -> stat.setLong(1, id),
                        rs -> {
                            if (rs.next())
                                return rs;

                            return null;
                        }
                )
        );
    }

    public static Boolean getBoolean(ResultSet rs, int index) {
        try {
            boolean b = rs.getBoolean(index);
            if (rs.wasNull())
                return null;
            return b;
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Integer getInt(ResultSet rs, int index) {
        try {
            int i = rs.getInt(index);
            if (rs.wasNull())
                return null;
            return i;
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Long getLong(ResultSet rs, int index) {
        try {
            long l = rs.getLong(index);
            if (rs.wasNull())
                return null;
            return l;
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static long getBeanID(ResultSet rs, int index) {
        try {
            return rs.getLong(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static long getItemOrder(ResultSet rs, int index) {
        try {
            return rs.getLong(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static String getString(ResultSet rs, int index) {
        try {
            return rs.getString(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Date getDate(ResultSet rs, int index) {
        try {
            return rs.getDate(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Time getTime(ResultSet rs, int index) {
        try {
            return rs.getTime(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Timestamp getTimestamp(ResultSet rs, int index) {
        try {
            return rs.getTimestamp(index);
        } catch (SQLException sqlEx) {
            throw new SQLRuntimeException(sqlEx);
        }
    }

    public static Money getMoney(ResultSet rs, int index, DbBeanFormatter formatter) {
        Long amount = getLong(rs, index);
        if (amount == null)
            return null;
        return new Money(amount, formatter.getDefaultMoneyFormat());
    }

    // ------------

    public static void setBoolean(PreparedStatement stat, int index, Boolean value) throws SQLException {
        if (value == null)
            stat.setNull(index, Types.BOOLEAN);
        else
            stat.setBoolean(index, value);
    }

    public static void setInt(PreparedStatement stat, int index, Integer value) throws SQLException {
        setInt(stat, index, value, Types.INTEGER);
    }

    public static void setInt(PreparedStatement stat, int index, Integer value, int sqlType) throws SQLException {
        if (value == null)
            stat.setNull(index, sqlType);
        else
            stat.setInt(index, value);
    }

    public static void setLong(PreparedStatement stat, int index, Long value) throws SQLException {
        setLong(stat, index, value, Types.INTEGER);
    }

    public static void setLong(PreparedStatement stat, int index, Long value, int sqlType) throws SQLException {
        if (value == null)
            stat.setNull(index, sqlType);
        else
            stat.setLong(index, value);
    }

    public static void setMoney(PreparedStatement stat, int index, Money value) throws SQLException {
        setMoney(stat, index, value, Types.INTEGER);
    }

    public static void setMoney(PreparedStatement stat, int index, Money value, int sqlType) throws SQLException {
        if (value == null)
            setLong(stat, index, null, sqlType);
        else
            setLong(stat, index, value.getVal());
    }

    public static void setID(PreparedStatement stat, int index, long id) throws SQLException {
        if (id == 0)
            stat.setNull(index, Types.INTEGER);
        else
            stat.setLong(index, id);
    }

    // ------------

    public static long getCount(ResultSet rs) throws SQLException {
        rs.next();
        return rs.getLong(1);
    }

    // ------------

    public static <B> List<B> getSelection(
            DbBeanParameters parameters,
            String whereClause,
            String orderBy,
            DBQuerySetup setup,
            Function<ResultSet, List<B>> listFunction,
            DBAccess dbAccess)
    {
        if (whereClause == null && setup != null)
            throw new IllegalArgumentException("Cannot accept setup code without a WHERE clause.");

        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(parameters.getDatabaseFieldList()).append(" FROM ").append(parameters.getDatabaseTableName());
        if (whereClause != null)
            query.append(" WHERE ").append(whereClause);
        if (orderBy != null)
            query.append(" ORDER BY ").append(orderBy);

        if (whereClause == null || setup == null)
            return dbAccess.processQuery(query.toString(), listFunction::apply);

        return dbAccess.processQuery(query.toString(), setup, listFunction::apply);
    }

    public static long getSelectionCount(DbBeanParameters parameters, String whereClause, DBQuerySetup setup, DBAccess dbAccess) {
        String query = "SELECT COUNT(id) FROM " + parameters.getDatabaseTableName() + " WHERE " + whereClause;

        if (setup == null)
            return dbAccess.processQuery(query, DBUtil::getCount);

        return dbAccess.processQuery(query, setup, DBUtil::getCount);
    }

    public static long getFullCount(DbBeanParameters parameters, DBAccess dbAccess) {
        return dbAccess.processQuery("SELECT COUNT(id) FROM " + parameters.getDatabaseTableName(), DBUtil::getCount);
    }

    // ------------

    public static <B extends DbBeanInterface> List<B> getInventory(
            DbBeanParameters parameters,
            String fieldName,
            long id,
            Function<ResultSet, List<B>> collector,
            DBAccess dbAccess)
    {
        return dbAccess.processQuery(
                "SELECT " + parameters.getDatabaseFieldList() + " FROM " + parameters.getDatabaseTableName()
                        + " WHERE " + fieldName + "=? ORDER BY " + parameters.getOrderByFields(),
                stat -> stat.setLong(1, id),
                collector::apply
        );
    }

    public static long getInventorySize(DbBeanParameters parameters, String fieldName, long id, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT COUNT(id) FROM " + parameters.getDatabaseTableName() + " WHERE " + fieldName + "=?",
                stat -> stat.setLong(1, id),
                DBUtil::getCount
        );
    }

    // ------------

    public static boolean checkUnicity(DbBeanParameters parameters, String fieldName, Object value, long id, DBAccess dbAccess) {
        return !dbAccess.processQuery(
                getUnicityQuery(parameters, fieldName),
                stat -> {
                    setValueForUnicityCheck(stat, value);
                    stat.setLong(2, id);
                },
                ResultSet::next
        );
    }

    public static boolean checkUnicity(DbBeanParameters parameters, String fieldName, Object value, long id, DBTransaction transaction) {
        return !transaction.addQuery(
                getUnicityQuery(parameters, fieldName),
                stat -> {
                    setValueForUnicityCheck(stat, value);
                    stat.setLong(2, id);
                },
                ResultSet::next
        );
    }

    private static String getUnicityQuery(DbBeanParameters parameters, String fieldName) {
        return "SELECT id FROM " + parameters.getDatabaseTableName() + " WHERE " + fieldName + "=? AND id <> ?";
    }

    private static void setValueForUnicityCheck(PreparedStatement stat, Object value) throws SQLException {
        String typeName = value.getClass().getName();
        switch (typeName) {
            case "java.lang.Boolean":
                stat.setBoolean(1, (Boolean) value);
                break;
            case "java.lang.Integer":
                stat.setInt(1, (Integer) value);
                break;
            case "java.lang.Long":
                stat.setLong(1, (Long) value);
                break;
            case "java.lang.String":
                stat.setString(1, (String) value);
                break;
            case "java.sql.Date":
                stat.setDate(1, (Date) value);
                break;
            case "java.sql.Time":
                stat.setTime(1, (Time) value);
                break;
            case "java.sql.Timestamp":
                stat.setTimestamp(1, (Timestamp) value);
                break;
            case "org.beanmaker.v2.util.Money":
                stat.setLong(1, ((Money) value).getVal());
                break;
            default:
                throw new IllegalArgumentException("Unsupported object type: " + typeName);
        }
    }

    // ------------

    public static List<IdNamePair> getIdNamePairs(DbBeanParameters parameters, DBAccess dbAccess) {
        return getIdNamePairs(parameters, null, dbAccess);
    }

    public static List<IdNamePair> getIdNamePairs(DbBeanParameters parameters, String whereClause, DBAccess dbAccess) {
        return getIdNamePairs(parameters, whereClause, parameters.getNamingFields(), parameters.getOrderingFields(), dbAccess);
    }

    public static List<IdNamePair> getIdNamePairs(
            DbBeanParameters parameters,
            List<String> dataFields,
            List<String> orderingFields,
            DBAccess dbAccess)
    {
        return getIdNamePairs(parameters, null,dataFields, orderingFields, dbAccess);
    }

    public static List<IdNamePair> getIdNamePairs(
            DbBeanParameters parameters,
            String whereClause,
            List<String> dataFields,
            List<String> orderingFields,
            DBAccess dbAccess)
    {
        var pairs = new ArrayList<IdNamePair>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT id, ");
        for (String field: dataFields) {
            query.append(field);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(" FROM ");
        query.append(parameters.getDatabaseTableName());
        if (whereClause != null) {
            query.append(" WHERE ");
            query.append(whereClause);
        }
        query.append(" ORDER BY ");
        for (String field: orderingFields) {
            query.append(field);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());

        dbAccess.processQuery(
                query.toString(),
                rs -> {
                    while (rs.next()) {
                        StringBuilder name = new StringBuilder();
                        for (int i = 0; i < dataFields.size(); i++) {
                            name.append(rs.getString(2 + i));
                            if (i < dataFields.size() - 1)
                                name.append(" ");
                        }
                        pairs.add(new IdNamePair(rs.getLong(1), name.toString()));
                    }
                });

        return pairs;
    }

    // ------------

    public static boolean isIdOK(DbBeanParameters parameters, long id, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT id FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean isIdOK(DbBeanParameters parameters, long id, DBTransaction transaction) {
        return transaction.addQuery(
                "SELECT id FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    // ------------

    public static String getHumanReadableTitle(DbBeanParameters parameters, long id, DBAccess dbAccess) {
        if (!isIdOK(parameters, id, dbAccess))
            throw new IllegalArgumentException("No such id (" + id + ") in database for table " + parameters.getDatabaseTableName());

        int fieldCount = parameters.getNamingFields().size();
        return dbAccess.processQuery(
                getHumanReadableTitleSQLQuery(parameters),
                stat -> stat.setLong(1, id),
                rs -> {
                    StringBuilder name = new StringBuilder();
                    rs.next();
                    for (int i = 0; i < fieldCount; i++) {
                        name.append(rs.getString(1 + i));
                        if (i < fieldCount - 1)
                            name.append(" ");
                    }
                    return name.toString();
                }
        );
    }

    private static String getHumanReadableTitleSQLQuery(DbBeanParameters parameters) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        for (String field: parameters.getNamingFields()) {
            query.append(field);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(" FROM ");
        query.append(parameters.getDatabaseTableName());
        query.append(" WHERE id=?");

        return query.toString();
    }

    // ------------

    public static Date copy(Date date) {
        if (date == null)
            return null;

        return new Date(date.getTime());
    }

    public static Time copy(Time time) {
        if (time == null)
            return null;

        return new Time(time.getTime());
    }

    public static Timestamp copy(Timestamp timestamp) {
        if (timestamp == null)
            return null;

        return new Timestamp(timestamp.getTime());
    }

    // ------------

    /*public static long getMaxItemOrder(DBAccess dbAccess, String query) {
        return dbAccess.processQuery(
                query,
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    public static long getMaxItemOrder(DBAccess dbAccess, String query, long... parameters) {
        return dbAccess.processQuery(
                query,
                 stat -> setupParameters(stat, 1, toList(parameters)),
                 rs -> {
                     rs.next();
                     return rs.getLong(1);
                 });
    }

    private static void setupParameters(PreparedStatement stat, int startIndex, List<Long> parameters) throws SQLException {
        int index = startIndex - 1;
        for (long parameter: parameters)
            stat.setLong(++index, parameter);
    }

    private static List<Long> toList(long... parameters) {
        List<Long> list = new ArrayList<Long>();
        for (long parameter: parameters)
            list.add(parameter);
        return list;
    }

    public static long getMaxItemOrder(DBTransaction transaction, String query) {
        return transaction.addQuery(
                query,
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    public static long getMaxItemOrder(DBTransaction transaction, String query, long... parameters) {
        return transaction.addQuery(
                query,
                stat -> setupParameters(stat, 1, toList(parameters)),
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    private static long getItemOrderSwapValue(long itemOrder, boolean moveUp) {
        if (moveUp)
            return itemOrder - 1;

        return itemOrder + 1;
    }

    public static void itemOrderMoveUp(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder) {
        itemOrderMove(db, idFromItemOrderQuery, table, id, itemOrder, null, true);
    }

    private static void itemOrderMove(
            DB db,
            String idFromItemOrderQuery,
            String table,
            long id,
            long itemOrder,
            List<Long> parameters,
            boolean moveUp)
    {
        DBTransaction transaction = new DBTransaction(db);

        final long swapPositionWithBeanId = transaction.addQuery(
                idFromItemOrderQuery,
                stat -> {
                    stat.setLong(1, getItemOrderSwapValue(itemOrder, moveUp));
                    if (parameters != null)
                        setupParameters(stat, 2, parameters);
                },
                rs -> {
                    if (rs.next())
                        return rs.getLong(1);

                    throw new IllegalArgumentException("No such item order # " + composeIdForException(id, parameters) + ". Cannot effect change.  Please check database integrity.");
                });

        if (moveUp) {
            incItemOrder(transaction, swapPositionWithBeanId, table);
            decItemOrder(transaction, id, table);
        } else {
            decItemOrder(transaction, swapPositionWithBeanId, table);
            incItemOrder(transaction, id, table);
        }

        transaction.commit();
    }

    private static String composeIdForException(long id, List<Long> parameters) {
        if (parameters == null)
            return "[" + id + "]";

        StringBuilder buf = new StringBuilder();
        buf.append("[").append(id).append(", ");
        for (long parameter: parameters)
            buf.append(parameter).append(", ");
        buf.delete(buf.length() - 2, buf.length());
        buf.append("]");

        return buf.toString();
    }

    private static void incItemOrder(DBTransaction transaction, long id, String table) {
        setItemOrder(transaction, id, table, getItemOrder(transaction, id, table) + 1);
    }

    private static void decItemOrder(DBTransaction transaction, long id, String table) {
        setItemOrder(transaction, id, table, getItemOrder(transaction, id, table) - 1);
    }

    private static long getItemOrder(DBTransaction transaction, long id, String table) {
        return transaction.addQuery(
                "SELECT item_order FROM " + table + " WHERE id=?",
                stat ->  stat.setLong(1, id),
                rs -> {
                    if (rs.next())
                        return rs.getLong(1);

                    throw new IllegalArgumentException("No such ID #" + id);
                });
    }

    private static void setItemOrder(DBTransaction transaction, long id, String table, long itemOrder) {
        transaction.addUpdate(
                "UPDATE " + table + " SET item_order=? WHERE id=?",
                stat -> {
                    stat.setLong(1, itemOrder);
                    stat.setLong(2, id);
                });
    }

    public static void itemOrderMoveUp(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder, long... parameters) {
        itemOrderMove(db, idFromItemOrderQuery, table, id, itemOrder, toList(parameters), true);
    }

    public static void itemOrderMoveDown(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder) {
        itemOrderMove(db, idFromItemOrderQuery, table, id, itemOrder, null, false);
    }

    public static void itemOrderMoveDown(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder, long... parameters) {
        itemOrderMove(db, idFromItemOrderQuery, table, id, itemOrder, toList(parameters), false);
    }

    public static void updateItemOrdersAbove(String query, DBTransaction transaction, long threshold) {
        updateItemOrdersAbove(query, transaction, threshold, null);
    }

    public static void updateItemOrdersAbove(String query, DBTransaction transaction, long threshold, long... parameters) {
        transaction.addUpdate(
                query,
                stat -> {
                    stat.setLong(1, threshold);
                    if (parameters != null) {
                        int index = 1;
                        for (long parameter: parameters)
                            stat.setLong(++index, parameter);
                    }
                });
    }

    public static void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound) {
        updateItemOrdersInBetween(query, transaction, lowerBound, upperBound, null);
    }

    public static void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound, long... parameters) {
        transaction.addUpdate(
                query,
                stat -> {
                    stat.setLong(1, lowerBound);
                    stat.setLong(2, upperBound);
                    if (parameters != null) {
                        int index = 2;
                        for (long parameter: parameters)
                            stat.setLong(++index, parameter);
                    }
                });
    }*/
}
