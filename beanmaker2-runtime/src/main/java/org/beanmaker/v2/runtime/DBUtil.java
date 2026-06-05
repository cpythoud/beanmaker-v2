package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbQuerySetup;
import org.beanmaker.v2.database.sql.DbTransaction;
import org.beanmaker.v2.database.sql.SqlRuntimeException;

import org.beanmaker.v2.util.DecimalValue;
import org.beanmaker.v2.util.Money;

import org.beanmaker.v2.util.Strings;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Function;

// TODO: rename class
public final class DBUtil {

    public static Boolean getBoolean(ResultSet rs, int index) {
        try {
            boolean b = rs.getBoolean(index);
            if (rs.wasNull())
                return null;
            return b;
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Integer getInt(ResultSet rs, int index) {
        try {
            int i = rs.getInt(index);
            if (rs.wasNull())
                return null;
            return i;
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Long getLong(ResultSet rs, int index) {
        try {
            long l = rs.getLong(index);
            if (rs.wasNull())
                return null;
            return l;
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static long getBeanID(ResultSet rs, int index) {
        try {
            return rs.getLong(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static int getBeanVersion(ResultSet rs, int index) {
        try {
            return rs.getInt(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static long getItemOrder(ResultSet rs, int index) {
        try {
            return rs.getLong(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static String getString(ResultSet rs, int index) {
        try {
            return rs.getString(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Date getDate(ResultSet rs, int index) {
        try {
            return rs.getDate(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Time getTime(ResultSet rs, int index) {
        try {
            return rs.getTime(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Timestamp getTimestamp(ResultSet rs, int index) {
        try {
            return rs.getTimestamp(index);
        } catch (SQLException sqlEx) {
            throw new SqlRuntimeException(sqlEx);
        }
    }

    public static Money getMoney(ResultSet rs, int index, DbBeanFormatter formatter) {
        Long amount = getLong(rs, index);
        if (amount == null)
            return null;
        return new Money(amount, formatter.getDefaultMoneyFormat());
    }

    public static DecimalValue getDecimalValue(ResultSet rs, int index, int decimals) {
        Long digits = getLong(rs, index);
        if (digits == null)
            return null;
        return DecimalValue.from(digits, decimals);
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

    public static void setDecimalValue(PreparedStatement stat, int index, DecimalValue value) throws SQLException {
        setDecimalValue(stat, index, value, Types.INTEGER);
    }

    public static void setDecimalValue(PreparedStatement stat, int index, DecimalValue value, int sqlType) throws SQLException {
        if (value == null)
            setLong(stat, index, null, sqlType);
        else
            setLong(stat, index, value.toLong());
    }

    public static void setID(PreparedStatement stat, int index, long id) throws SQLException {
        if (id == 0)
            stat.setNull(index, Types.INTEGER);
        else
            stat.setLong(index, id);
    }

    public static void setString(PreparedStatement stat, int index, String value) throws SQLException {
        if (Strings.isEmpty(value)) {
            stat.setNull(index, Types.VARCHAR);
        } else {
            stat.setString(index, value);
        }
    }

    public static void setDate(PreparedStatement stat, int index, Date value) throws SQLException {
        if (value == null) {
            stat.setNull(index, Types.DATE);
        } else {
            stat.setDate(index, value);
        }
    }

    public static void setTime(PreparedStatement stat, int index, Time value) throws SQLException {
        if (value == null) {
            stat.setNull(index, Types.TIME);
        } else {
            stat.setTime(index, value);
        }
    }

    public static void setTimestamp(PreparedStatement stat, int index, Timestamp value) throws SQLException {
        if (value == null) {
            stat.setNull(index, Types.TIMESTAMP);
        } else {
            stat.setTimestamp(index, value);
        }
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
            DbQuerySetup setup,
            Function<ResultSet, List<B>> listFunction,
            DbExecutor dbExecutor)
    {
        return getSelection(
                parameters.getDatabaseTableName(),
                parameters.getDatabaseFieldList(),
                whereClause,
                orderBy,
                setup,
                listFunction,
                dbExecutor
        );
    }

    public static <B> List<B> getSelection(
            String databaseTableName,
            String databaseFieldList,
            String whereClause,
            String orderBy,
            DbQuerySetup setup,
            Function<ResultSet, List<B>> listFunction,
            DbExecutor dbExecutor)
    {
        if (whereClause == null && setup != null)
            throw new IllegalArgumentException("Cannot accept setup code without a WHERE clause.");

        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(databaseFieldList).append(" FROM ").append(databaseTableName);
        if (whereClause != null)
            query.append(" WHERE ").append(whereClause);
        if (orderBy != null)
            query.append(" ORDER BY ").append(orderBy);

        if (whereClause == null || setup == null)
            return dbExecutor.processQuery(query.toString(), listFunction::apply);

        return dbExecutor.processQuery(query.toString(), setup, listFunction::apply);
    }

    public static long getSelectionCount(
            DbBeanParameters parameters,
            String whereClause,
            DbQuerySetup setup,
            DbExecutor dbExecutor)
    {
        return getSelectionCount(parameters.getDatabaseTableName(), whereClause, setup, dbExecutor);
    }

    public static long getSelectionCount(
            String databaseTableName,
            String whereClause,
            DbQuerySetup setup,
            DbExecutor dbExecutor)
    {
        String query = "SELECT COUNT(id) FROM " + databaseTableName + " WHERE " + whereClause;

        if (setup == null)
            return dbExecutor.processQuery(query, DBUtil::getCount);

        return dbExecutor.processQuery(query, setup, DBUtil::getCount);
    }

    public static long getFullCount(DbBeanParameters parameters, DbExecutor dbExecutor) {
        return dbExecutor.processQuery("SELECT COUNT(id) FROM " + parameters.getDatabaseTableName(), DBUtil::getCount);
    }

    public static <B> List<B> getVersionedSelection(
            DbBeanParameters parameters,
            String whereClause,
            String orderBy,
            DbQuerySetup setup,
            Function<ResultSet, List<B>> listFunction,
            DbExecutor dbExecutor)
    {
        return getSelection(
                parameters.getVersionedDatabaseViewName(),
                parameters.getVersionedDatabaseFieldList(),
                whereClause,
                orderBy,
                setup,
                listFunction,
                dbExecutor
        );
    }

    public static long getVersionedSelectionCount(
            DbBeanParameters parameters,
            String whereClause,
            DbQuerySetup setup,
            DbExecutor dbExecutor)
    {
        return getSelectionCount(parameters.getVersionedDatabaseViewName(), whereClause, setup, dbExecutor);
    }

    public static long getVersionedFullCount(DbBeanParameters parameters, DbExecutor dbExecutor) {
        return dbExecutor.processQuery("SELECT COUNT(id) FROM " + parameters.getDatabaseTableName(), DBUtil::getCount);
    }

    // ------------

    public static <B extends DbBeanInterface> List<B> getInventory(
            DbBeanParameters parameters,
            String fieldName,
            long id,
            Function<ResultSet, List<B>> collector,
            DbExecutor dbExecutor)
    {
        return dbExecutor.processQuery(
                "SELECT " + parameters.getDatabaseFieldList() + " FROM " + parameters.getDatabaseTableName()
                        + " WHERE " + fieldName + "=? ORDER BY " + parameters.getOrderByFields(),
                stat -> stat.setLong(1, id),
                collector::apply
        );
    }

    public static long getInventorySize(DbBeanParameters parameters, String fieldName, long id, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                "SELECT COUNT(id) FROM " + parameters.getDatabaseTableName() + " WHERE " + fieldName + "=?",
                stat -> stat.setLong(1, id),
                DBUtil::getCount
        );
    }

    // ------------

    public static boolean checkUnicity(
            DbBeanParameters parameters,
            String fieldName,
            Object value,
            long id,
            DbExecutor dbExecutor)
    {
        return !dbExecutor.processQuery(
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
            case "org.beanmaker.v2.util.DecimalValue":
                stat.setLong(1, ((DecimalValue) value).toLong());
                break;
            default:
                throw new IllegalArgumentException("Unsupported object type: " + typeName);
        }
    }

    public static boolean checkQualifiedUnicity(
            DbBeanParameters parameters,
            String fieldName,
            Object value,
            long id,
            String associatedBeanFieldName,
            long idAssociatedBean,
            DbExecutor dbExecutor)
    {
        return !dbExecutor.processQuery(
                getQualifiedUnicityQuery(parameters, fieldName, associatedBeanFieldName),
                stat -> {
                    setValueForUnicityCheck(stat, value);
                    stat.setLong(2, idAssociatedBean);
                    stat.setLong(3, id);
                },
                ResultSet::next
        );
    }

    private static String getQualifiedUnicityQuery(
            DbBeanParameters parameters,
            String fieldName,
            String associatedBeanFieldName)
    {
        return "SELECT id FROM " + parameters.getDatabaseTableName()
                + " WHERE " + fieldName + "=? AND " + associatedBeanFieldName + "=? AND id <> ?";
    }

    // ------------

    public static List<IdNamePair> getIdNamePairs(DbBeanParameters parameters, DbExecutor dbExecutor) {
        return getIdNamePairs(parameters, null, dbExecutor);
    }

    public static List<IdNamePair> getIdNamePairs(
            DbBeanParameters parameters,
            String whereClause,
            DbExecutor dbExecutor)
    {
        return getIdNamePairs(
                parameters,
                whereClause,
                parameters.getNamingFields(),
                parameters.getOrderingFields(),
                dbExecutor
        );
    }

    public static List<IdNamePair> getIdNamePairs(
            DbBeanParameters parameters,
            List<String> dataFields,
            List<String> orderingFields,
            DbExecutor dbExecutor)
    {
        return getIdNamePairs(parameters, null,dataFields, orderingFields, dbExecutor);
    }

    public static List<IdNamePair> getIdNamePairs(
            DbBeanParameters parameters,
            String whereClause,
            List<String> dataFields,
            List<String> orderingFields,
            DbExecutor dbExecutor)
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

        dbExecutor.processQuery(
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

    public static boolean isIdOK(DbBeanParameters parameters, long id, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                "SELECT id FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    // ------------

    public static String getHumanReadableTitle(
            DbBeanParameters parameters,
            long id,
            DbExecutor dbExecutor,
            DbBeanLabelBasicFunctions labelFunctions,
            DbBeanLanguage language)
    {
        if (!isIdOK(parameters, id, dbExecutor))
            throw new IllegalArgumentException("No such id (" + id + ") in database for table " + parameters.getDatabaseTableName());

        var name = new StringBuilder();
        for (String field: parameters.getNamingFields()) {
            if (isLabelField(field))
                name.append(getBeanNamingLabelValue(field, parameters, id, dbExecutor, labelFunctions, language));
            else
                name.append(getBeanNamingFieldValue(field, parameters, id, dbExecutor));
            name.append(" ");
        }
        name.delete(name.length() - 1, name.length());
        return name.toString();
    }

    private static boolean isLabelField(String field) {
        return field.startsWith("id_") && field.endsWith("_label");
    }

    private static String getBeanNamingFieldValue(
            String field,
            DbBeanParameters parameters,
            long id,
            DbExecutor dbExecutor)
    {
        return dbExecutor.processQuery(
                "SELECT " + field + " FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                rs -> {
                    rs.next();
                    return rs.getString(1);
                }
        );

    }

    private static String getBeanNamingLabelValue(
            String field,
            DbBeanParameters parameters,
            long id,
            DbExecutor dbExecutor,
            DbBeanLabelBasicFunctions labelFunctions,
            DbBeanLanguage language)
    {
        return dbExecutor.processQuery(
                "SELECT " + field + " FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                rs -> {
                    rs.next();
                    return labelFunctions.getLabel(rs.getLong(1)).getSafeValue(language);
                }
        );

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
    
}
