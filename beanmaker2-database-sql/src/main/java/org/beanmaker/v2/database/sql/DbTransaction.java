package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to encapsulate JDBC transactions.
 */
public class DbTransaction implements DbExecutor {

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
     * @see DbTransaction#createRecord(String, DbQuerySetup)
     */
    @Override
    public int processUpdate(String query, DbQuerySetup querySetup) {
        int count;

        try {
            count = DbUtils.processUpdate(conn, query, querySetup);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return count;
    }

    @Deprecated
    public int addUpdate(String query, DbQuerySetup querySetup) {
        return processUpdate(query, querySetup);
    }

    /**
     * Use this method to update the database.
     * If you need to get the id of a newly created row, use the createRecord function.
     * @param query SQL query.
     * @return the number of database rows affected by the update.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown
     * as a SqlRuntimeException.
     * @see DbTransaction#createRecord(String, DbQuerySetup)
     */
    @Override
    public int processUpdate(String query) {
        int count;

        try {
            count = DbUtils.processUpdate(conn, query);
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
     * @see DbTransaction#processUpdate(String, DbQuerySetup)
     */
    @Override
    public long createRecord(String query, DbQuerySetup querySetup) {
        long id;

        try {
            id = DbUtils.createRecord(conn, query, querySetup);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return id;
    }

    @Deprecated
    public long addRecordCreation(String query, DbQuerySetup querySetup) {
        return createRecord(query, querySetup);
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process
     *                     the query results.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown
     * as a SqlRuntimeException.
     * @see DbTransaction#processQuery(String, DbQueryProcess)
     */
    @Override
    public void processQuery(String query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        try {
            DbUtils.processQuery(conn, query, querySetup, queryProcess);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    @Deprecated
    public void addQuery(String query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        processQuery(query, querySetup, queryProcess);
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process the query
     *                     results.
     * @see DbTransaction#processQuery(String, DbQuerySetup, DbQueryProcess)
     */
    @Override
    public void processQuery(String query, DbQueryProcess queryProcess) {
        try {
            DbUtils.processQuery(conn, query, queryProcess);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    @Deprecated
    public void addQuery(String query, DbQueryProcess queryProcess) {
        processQuery(query, queryProcess);
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process
     *                          the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbTransaction#processQuery(String, DbQueryRetrieveData)
     */
    @Override
    public <T> T processQuery(String query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        T data;

        try {
            data = DbUtils.processQuery(conn, query, querySetup, queryRetrieveData);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    @Deprecated
    public <T> T addQuery(String query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        return processQuery(query, querySetup, queryRetrieveData);
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process
     *                          the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbTransaction#processQuery(String, DbQuerySetup, DbQueryRetrieveData)
     */
    @Override
    public <T> T processQuery(String query, DbQueryRetrieveData<T> queryRetrieveData) {
        T data;

        try {
            data = DbUtils.processQuery(conn, query, queryRetrieveData);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    @Deprecated
    public <T> T addQuery(String query, DbQueryRetrieveData<T> queryRetrieveData) {
        return processQuery(query, queryRetrieveData);
    }

    /**
     * Use this method to update the database by processing multiple updates.
     * @param query SQL query.
     * @param updates an object implementing the {@link DbUpdates} interface, used to execute the updates.
     */
    @Override
    public void processUpdates(String query, DbUpdates updates) {
        try {
            DbUtils.processUpdates(conn, query, updates);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    @Deprecated
    public void addUpdates(String query, DbUpdates updates) {
        processUpdates(query, updates);
    }

    /**
     * Use this method to process multiple queries on the database and retrieve their result.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueries} interface, used to process the queries result.
     * @param <T> type of queries result.
     * @return result of the queries.
     * @see DbTransaction#processQueries(String, DbQueriesNoReturn)
     */
    @Override
    public <T> T processQueries(String query, DbQueries<T> queries) {
        T data;

        try {
            data = DbUtils.processQueries(conn, query, queries);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

        return data;
    }

    @Deprecated
    public <T> T addQueries(String query, DbQueries<T> queries) {
        return processQueries(query, queries);
    }

    /**
     * Use this method to process multiple queries on the database without returning results to the caller.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueriesNoReturn} interface, used to process the queries
     *                result.
     * @see  DbTransaction#processQueries(String, DbQueries)
     */
    @Override
    public void processQueries(String query, DbQueriesNoReturn queries) {
        try {
            DbUtils.processQueries(conn, query, queries);
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    @Deprecated
    public void addQueries(String query, DbQueriesNoReturn queries) {
        processQueries(query, queries);
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
