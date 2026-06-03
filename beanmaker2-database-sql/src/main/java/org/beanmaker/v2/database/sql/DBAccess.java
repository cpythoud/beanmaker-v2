package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to encapsulate JDBC database access.
 */
public class DBAccess {

    private final DB db;

    /**
     * @param db a {@link DB} object to get connections to the database.
     */
    public DBAccess(DB db) {
        this.db = db;
    }

    /**
     * Use this method to update the database.
     * If you need to get the id of a newly created row, use the createRecord function.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DBQuerySetup} interface, used to setup the parameters for the update.
     * @return the number of database rows affected by the update.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @see DBAccess#createRecord(String, DBQuerySetup)
     */
    public int processUpdate(String query, DBQuerySetup querySetup) {
        int count;

        Connection conn = null;
        try {
            conn = db.getConnection();
            count = DBUtils.processUpdate(conn, query, querySetup);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return count;
    }

    /**
     * Use this method to insert a single new row in the database and obtain its ID.
     * If you need to insert more than one row at a time, use the processUpdate function instead.
     * @param query SQL query used to insert the row.
     * @param querySetup an object implementing the {@link DBQuerySetup} interface, used to setup the data used in the query.
     * @return the id of the newly created row as a long; if you need an int, you will have to cast it.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @throws IllegalArgumentException if the number of rows affected in the database is not strictly one.
     * @see DBAccess#processUpdate(String, DBQuerySetup)
     */
    public long createRecord(String query, DBQuerySetup querySetup) {
        long id;

        Connection conn = null;
        try {
            conn = db.getConnection();
            id = DBUtils.createRecord(conn, query, querySetup);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return id;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DBQuerySetup} interface, used to setup the query parameters.
     * @param queryProcess an object implementing the {@link DBQueryProcess} interface, used to process the query results.
     * @throws SqlRuntimeException if an SQLException is thrown during database access, it will be rethrown as a SqlRuntimeException.
     * @see DBAccess#processQuery(String, DBQueryProcess)
     */
    public void processQuery(String query, DBQuerySetup querySetup, DBQueryProcess queryProcess) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DBUtils.processQuery(conn, query, querySetup, queryProcess);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param queryProcess an object implementing the {@link DBQueryProcess} interface, used to process the query results.
     * @see DBAccess#processQuery(String, DBQuerySetup, DBQueryProcess)
     */
    public void processQuery(String query, DBQueryProcess queryProcess) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DBUtils.processQuery(conn, query, queryProcess);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DBQuerySetup} interface, used to setup the query parameters.
     * @param queryRetrieveData an object implementing the {@link DBQueryRetrieveData} interface, used to process the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DBAccess#processQuery(String, DBQueryRetrieveData)
     */
    public <T> T processQuery(String query, DBQuerySetup querySetup, DBQueryRetrieveData<T> queryRetrieveData) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DBUtils.processQuery(conn, query, querySetup, queryRetrieveData);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to query the database.
     * @param query SQL query.
     * @param retrieveData an object implementing the {@link DBQueryRetrieveData} interface, used to process the query results.
     * @param <T> type of query result.
     * @return result of the query.
     * @see DBAccess#processQuery(String, DBQuerySetup, DBQueryRetrieveData)
     */
    public <T> T processQuery(String query, DBQueryRetrieveData<T> retrieveData) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DBUtils.processQuery(conn, query, retrieveData);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to update the database by processing multiple updates.
     * @param query SQL query.
     * @param updates an object implementing the {@link DBUpdates} interface, used to execute the updates.
     */
    public void processUpdates(String query, DBUpdates updates) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DBUtils.processUpdates(conn, query, updates);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }
    }

    /**
     * Use this method to process multiple queries on the database and retrieve their result.
     * @param query SQL query.
     * @param queries an object implementing the {@link DBQueries} interface, used to process the queries result.
     * @param <T> type of queries result.
     * @return result of the queries.
     * @see DBAccess#processQueries(String, DBQueriesNoReturn)
     */
    public <T> T processQueries(String query, DBQueries<T> queries) {
        T data;

        Connection conn = null;
        try {
            conn = db.getConnection();
            data = DBUtils.processQueries(conn, query, queries);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return data;
    }

    /**
     * Use this method to process multiple queries on the database without returning results to the caller.
     * @param query SQL query.
     * @param queries an object implementing the {@link DBQueriesNoReturn} interface, used to process the queries result.
     * @see  DBAccess#processQueries(String, DBQueries)
     */
    public void processQueries(String query, DBQueriesNoReturn queries) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DBUtils.processQueries(conn, query, queries);
            conn.close();
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        } finally {
            DBUtils.connectionSilentClose(conn);
        }
    }

}
