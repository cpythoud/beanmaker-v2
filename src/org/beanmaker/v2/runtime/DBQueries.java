package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQueryProcess;
import org.dbbeans.sql.DBQueryRetrieveData;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBQuerySetupRetrieveData;
import org.dbbeans.sql.DBTransaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
// TODO: remplacer/supprimer cette monstrosité
public class DBQueries {

    public static int getIntCount(DB db, String table) {
        return getIntCount(db, table, null);
    }

    public static long getLongCount(DB db, String table) {
        return getLongCount(db, table, null);
    }

    public static int getIntCount(DB db, String table, String whereClause) {
        return new DBAccess(db).processQuery(getCountQuery(table, whereClause), new DBQueryRetrieveData<Integer>() {
            @Override
            public Integer processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getInt(1);
            }
        });
    }

    public static long getLongCount(DB db, String table, String whereClause) {
        return new DBAccess(db).processQuery(getCountQuery(table, whereClause), new DBQueryRetrieveData<Long>() {
            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getLong(1);
            }
        });
    }

    private static String getCountQuery(String table, String whereClause) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT COUNT(id) FROM ").append(table);
        if (whereClause != null)
            query.append(" WHERE ").append(whereClause);

        return query.toString();
    }

    private static class ValidIdCheck implements DBQuerySetupRetrieveData<Boolean> {

        private final long id;

        ValidIdCheck(long id) {
            this.id = id;
        }

        @Override
        public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
            stat.setLong(1, id);
        }

        @Override
        public Boolean processResultSet(ResultSet rs) throws SQLException {
            return rs.next();
        }
    }

    public static boolean isIdOK(DB db, String table, long id) {
        return new DBAccess(db).processQuery(
                "SELECT id FROM " + table + " WHERE id=?",
                new ValidIdCheck(id));
    }

    public static boolean isIdOK(DBTransaction transaction, String table, long id) {
        return transaction.addQuery(
                "SELECT id FROM " + table + " WHERE id=?",
                new ValidIdCheck(id));
    }

    public static String getHumanReadableTitle(DB db, String table, long id, List<String> fields) {
        class Query extends HumanReadableNameProcess {
            Query(int fields) {
                super(fields);
            }

            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, id);
            }
        }

        if (!isIdOK(db, table, id))
            throw new IllegalArgumentException("No such id (" + id + ") in database for table " + table + ".");
        if (fields.size() == 0)
            throw new IllegalArgumentException("Field list is empty. It must contain at least one valid field name.");

        Query query = new Query(fields.size());
        return new DBAccess(db).processQuery(getHumanReadableTitleSQLQuery(table, fields), query);
    }

    private static abstract class HumanReadableNameProcess implements DBQuerySetupRetrieveData<String> {
        private final int fieldCount;

        HumanReadableNameProcess(int fieldCount) {
            this.fieldCount = fieldCount;
        }

        @Override
        public String processResultSet(ResultSet rs) throws SQLException {
            StringBuilder name = new StringBuilder();
            rs.next();
            for (int i = 0; i < fieldCount; i++) {
                name.append(rs.getString(1 + i));
                if (i < fieldCount - 1)
                    name.append(" ");
            }
            return name.toString();
        }
    }

    private static String getHumanReadableTitleSQLQuery(String table, List<String> fields) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        for (String field: fields) {
            query.append(field);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(" FROM ");
        query.append(table);
        query.append(" WHERE id=?");

        return query.toString();
    }


    public static List<IdNamePair> getIdNamePairs(DB db, String table, String whereClause, List<String> dataFields, List<String> orderingFields) {
        List<IdNamePair> pairs = new ArrayList<IdNamePair>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT id, ");
        for (String field: dataFields) {
            query.append(field);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(" FROM ");
        query.append(table);
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

        new DBAccess(db).processQuery(query.toString(), new DBQueryProcess() {
            @Override
            public void processResultSet(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    StringBuilder name = new StringBuilder();
                    for (int i = 0; i < dataFields.size(); i++) {
                        name.append(rs.getString(2 + i));
                        if (i < dataFields.size() - 1)
                            name.append(" ");
                    }
                    pairs.add(new IdNamePair(rs.getLong(1), name.toString()));
                }
            }
        });

        return pairs;
    }

    public static long getMaxItemOrder(DB db, String query) {
        return new DBAccess(db).processQuery(query, new DBQueryRetrieveData<Long>() {
            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getLong(1);
            }
        });
    }

    public static long getMaxItemOrder(DB db, String query, long... parameters) {
        return new DBAccess(db).processQuery(query, new DBQuerySetupRetrieveData<Long>() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                setupParameters(stat, 1, toList(parameters));
            }

            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getLong(1);
            }
        });
    }

    public static long getMaxItemOrder(DBTransaction transaction, String query) {
        return transaction.addQuery(query, new DBQueryRetrieveData<Long>() {
            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getLong(1);
            }
        });
    }

    public static long getMaxItemOrder(DBTransaction transaction, String query, long... parameters) {
        return transaction.addQuery(query, new DBQuerySetupRetrieveData<Long>() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                setupParameters(stat, 1, toList(parameters));
            }

            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getLong(1);
            }
        });
    }

    private static void setupParameters(PreparedStatement stat, int startIndex, List<Long> parameters) throws SQLException {
        int index = startIndex - 1;
        for (long parameter: parameters)
            stat.setLong(++index, parameter);
    }

    private static long getItemOrderSwapValue(long itemOrder, boolean moveUp) {
        if (moveUp)
            return itemOrder - 1;

        return itemOrder + 1;
    }

    public static void itemOrderMoveUp(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder) {
        itemOrderMove(db, idFromItemOrderQuery, table, id, itemOrder, null, true);
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
        transaction.addUpdate(query, new DBQuerySetup() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, threshold);
                if (parameters != null) {
                    int index = 1;
                    for (long parameter: parameters)
                        stat.setLong(++index, parameter);
                }
            }
        });
    }

    public static void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound) {
        updateItemOrdersInBetween(query, transaction, lowerBound, upperBound, null);
    }

    public static void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound, long... parameters) {
        transaction.addUpdate(query, new DBQuerySetup() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, lowerBound);
                stat.setLong(2, upperBound);
                if (parameters != null) {
                    int index = 2;
                    for (long parameter: parameters)
                        stat.setLong(++index, parameter);
                }
            }
        });
    }

    private static List<Long> toList(long... parameters) {
        List<Long> list = new ArrayList<Long>();
        for (long parameter: parameters)
            list.add(parameter);
        return list;
    }

    private static void itemOrderMove(DB db, String idFromItemOrderQuery, String table, long id, long itemOrder, List<Long> parameters, boolean moveUp) {
        final DBTransaction transaction = new DBTransaction(db);

        final long swapPositionWithBeanId = transaction.addQuery(idFromItemOrderQuery, new DBQuerySetupRetrieveData<Long>() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, getItemOrderSwapValue(itemOrder, moveUp));
                if (parameters != null)
                    setupParameters(stat, 2, parameters);
            }

            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                if (rs.next())
                    return rs.getLong(1);

                throw new IllegalArgumentException("No such item order # " + composeIdForException(id, parameters) + ". Cannot effect change.  Please check database integrity.");
            }
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
        return transaction.addQuery("SELECT item_order FROM " + table + " WHERE id=?", new DBQuerySetupRetrieveData<Long>() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, id);
            }

            @Override
            public Long processResultSet(ResultSet rs) throws SQLException {
                if (rs.next())
                    return rs.getLong(1);

                throw new IllegalArgumentException("No such ID #" + id);
            }
        });
    }

    private static void setItemOrder(DBTransaction transaction, long id, String table, long itemOrder) {
        transaction.addUpdate("UPDATE " + table + " SET item_order=? WHERE id=?", new DBQuerySetup() {
            @Override
            public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                stat.setLong(1, itemOrder);
                stat.setLong(2, id);
            }
        });
    }
}
