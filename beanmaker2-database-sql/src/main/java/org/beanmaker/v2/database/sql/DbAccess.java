package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to encapsulate JDBC database access.
 */
public class DbAccess {

    private final Db db;

    /**
     * @param db a {@link Db} object to get connections to the database.
     */
    public DbAccess(Db db) {
        this.db = db;
    }

    /**
     * Use this method to update the database.
     * If you need to get the id of a newly created row, use the createRecord function.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the parameters for the update.
     * @return the number of database rows affected by the update.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @see DbAccess#createRecord(String, DbQuerySetup)
     */
    public int processUpdate(String query, DbQuerySetup querySetup) {
        int count;

        Connection conn = null;
        try {
            conn = db.getConnection();
            count = DbUtils.processUpdate(conn, query, querySetup);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }

        return count;
    }

    /**
     * Use this method to insert a single new row in the database and obtain its ID.
     * If you need to insert more than one row at a time, use the processUpdate function instead.
     * @param query SQL query used to insert the row.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the data used in the query.
     * @return the id of the newly created row as a long; if you need an int, you will have to cast it.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @throws IllegalArgumentException if the number of rows affected in the database is not strictly one.
     * @see DbAccess#processUpdate(String, DbQuerySetup)
     */
    public long createRecord(String query, DbQuerySetup querySetup) {
        long id;

        Connection conn = null;
        try {
            conn = db.getConnection();
            id = DbUtils.createRecord(conn, query, querySetup);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }

        return id;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process the query results.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @see DbAccess#processQuery(String, DbQueryProcess)
     */
    public void processQuery(String query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DbUtils.processQuery(conn, query, querySetup, queryProcess);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process the query results.
     * @see DbAccess#processQuery(String, DbQuerySetup, DbQueryProcess)
     */
    public void processQuery(String query, DbQueryProcess queryProcess) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DbUtils.processQuery(conn, query, queryProcess);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to setup the query parameters.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbAccess#processQuery(String, DbQueryRetrieveData)
     */
    public <T> T processQuery(String query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DbUtils.processQuery(conn, query, querySetup, queryRetrieveData);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param retrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to process the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DbAccess#processQuery(String, DbQuerySetup, DbQueryRetrieveData)
     */
    public <T> T processQuery(String query, DbQueryRetrieveData<T> retrieveData) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DbUtils.processQuery(conn, query, retrieveData);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to update the database by processing multiple updates.
     * @param query SQL query.
     * @param updates an object implementing the {@link DbUpdates} interface, used to execute the updates.
     */
    public void processUpdates(String query, DbUpdates updates) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DbUtils.processUpdates(conn, query, updates);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to process multiple queries on the database and retrieve their result.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueries} interface, used to process the queries result.
     * @param <T> type of queries result.
     * @return result of the queries.
     * @see DbAccess#processQueries(String, DbQueriesNoReturn)
     */
    public <T> T processQueries(String query, DbQueries<T> queries) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DbUtils.processQueries(conn, query, queries);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to process multiple queries on the database without returning results to the caller.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueriesNoReturn} interface, used to process the queries result.
     * @see  DbAccess#processQueries(String, DbQueries)
     */
    public void processQueries(String query, DbQueriesNoReturn queries) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DbUtils.processQueries(conn, query, queries);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DbUtils.connectionSilentClose(conn);
        }
    }

}
