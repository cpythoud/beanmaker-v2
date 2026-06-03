package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to encapsulate JDBC transactions.
 */
public class DbTransaction {

    final Db db;
    final Connection conn;

    /**
     * @param db a {@link Db} object to obtain connections to the database.
     */
    public DbTransaction(Db db) {
        this.db = db;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Use this method to update the database.
     * If you need to get the id of a newly created row, use the createRecord function.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the parameters
     *                   for the update.
     * @return the number of database rows affected by the update.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown
     * as a SqlRuntimeException.
     * @see DbTransaction#addRecordCreation(String, DbQuerySetup)
     */
    public int addUpdate(String query, DbQuerySetup querySetup) {
        int count;

        try {
            count = DbUtils.processUpdate(conn, query, querySetup);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return count;
    }

    /**
     * Use this method to insert a single new row in the database and obtain its ID.
     * If you need to insert more than one row at a time, use the processUpdate function instead.
     * @param query SQL query used to insert the row.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the data used
     *                   in the query.
     * @return the id of the newly created row as a long; if you need an int, you will have to cast it.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown
     * as a SqlRuntimeException.
     * @throws IllegalArgumentException if the number of rows affected in the database is not strictly one.
     * @see DbTransaction#addUpdate(String, DbQuerySetup)
     */
    public long addRecordCreation(String query, DbQuerySetup querySetup) {
        long id;

        try {
            id = DbUtils.createRecord(conn, query, querySetup);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return id;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process
     *                     the query results.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown
     * as a SqlRuntimeException.
     * @see DbTransaction#addQuery(String, DbQueryProcess)
     */
    public void addQuery(String query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        try {
            DbUtils.processQuery(conn, query, querySetup, queryProcess);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process the query
     *                     results.
     * @see DbTransaction#addQuery(String, DbQuerySetup, DbQueryProcess)
     */
    public void addQuery(String query, DbQueryProcess queryProcess) {
        try {
            DbUtils.processQuery(conn, query, queryProcess);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process
     *                          the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbTransaction#addQuery(String, DbQueryRetrieveData)
     */
    public <T> T addQuery(String query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        T data;

        try {
            data = DbUtils.processQuery(conn, query, querySetup, queryRetrieveData);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process
     *                          the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbTransaction#addQuery(String, DbQuerySetup, DbQueryRetrieveData)
     */
    public <T> T addQuery(String query, DbQueryRetrieveData<T> queryRetrieveData) {
        T data;

        try {
            data = DbUtils.processQuery(conn, query, queryRetrieveData);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    /**
     * Use this method to update the database by processing multiple updates.
     * @param query SQL query.
     * @param updates an object implementing the {@link DbUpdates} interface, used to execute the updates.
     */
    public void addUpdates(String query, DbUpdates updates) {
        try {
            DbUtils.processUpdates(conn, query, updates);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Use this method to process multiple queries on the database and retrieve their result.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueries} interface, used to process the queries result.
     * @param <T> type of queries result.
     * @return result of the queries.
     * @see DbTransaction#addQueries(String, DbQueriesNoReturn)
     */
    public <T> T addQueries(String query, DbQueries<T> queries) {
        T data;

        try {
            data = DbUtils.processQueries(conn, query, queries);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    /**
     * Use this method to process multiple queries on the database without returning results to the caller.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueriesNoReturn} interface, used to process the queries
     *                result.
     * @see  DbTransaction#addQueries(String, DbQueries)
     */
    public void addQueries(String query, DbQueriesNoReturn queries) {
        try {
            DbUtils.processQueries(conn, query, queries);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Use this function to commit the changes to the database, once your are done setting up the transaction
     * with this class other functions.
     */
    public void commit() {
        try {
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Roll back all changes previously set up via this class other functions.
     */
    public void rollback() {
        try {
            conn.rollback();
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

}
