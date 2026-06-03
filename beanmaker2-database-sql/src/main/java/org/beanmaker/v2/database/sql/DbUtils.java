package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains static utility methods to help manage various aspects of JDBC programming.
 * <br>
 * You usually don't need to call these method directly. Instead you would use an instance of the
 * {@link DbAccess} or the {@link DbTransaction} class (which call these function internally).
 */
public final class DbUtils {

    /**
     * Close a PreparedStatement discarding any thrown SQLException.
     * @param stat the PreparedStatement to be closed.
     */
    public static void preparedStatementSilentClose(PreparedStatement stat) {
        if (stat != null) {
            try { stat.close(); }
            catch (SQLException ignore) { }
        }
    }

    /**
     * Close a Connection discarding any thrown SQLException.
     * @param conn the Connection to be closed.
     */
    public static void connectionSilentClose(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException ignore) { }
        }
    }

    /**
     * Process a database update.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to set up the parameters
     *                   for the update.
     * @return how many table rows were affected by the update.
     * @throws SQLException if a database error occurs
     */
    public static int processUpdate(Connection conn, String query, DbQuerySetup querySetup) throws SQLException {
        int count;

        PreparedStatement stat = conn.prepareStatement(query);
        try {
            querySetup.setupPreparedStatement(stat);
            count = stat.executeUpdate();
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }

        return count;
    }

    /**
     * Creates a new record in the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to set up the parameters
     *                   for the record creation.
     * @return the id of the created record.
     * @throws SQLException if a database error occurs
     * @throws java.lang.IllegalArgumentException if not exactly one row is created.
     */
    public static long createRecord(Connection conn, String query, DbQuerySetup querySetup) throws SQLException {
        long id;

        PreparedStatement stat = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        try {
            querySetup.setupPreparedStatement(stat);
            int count = stat.executeUpdate();
            if (count != 1)
                throw new IllegalArgumentException(
                        "Record creation query did not affect a single row. Rows affected: " + count + ".");
            ResultSet rs = stat.getGeneratedKeys();
            rs.next();
            id = rs.getLong(1);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }

        return id;
    }

    /**
     * Process a query on the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to set up parameters
     *                   for the query.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process results
     *                     from the query.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQuery(java.sql.Connection, String, DbQueryProcess)
     */
    public static void processQuery(Connection conn, String query, DbQuerySetup querySetup, DbQueryProcess queryProcess) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(query);
        try {
            querySetup.setupPreparedStatement(stat);
            ResultSet rs = stat.executeQuery();
            queryProcess.processResultSet(rs);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }
    }

    /**
     * Process a query on the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param queryProcess an object implementing the {@link DbQueryProcess} interface, used to process results
     *                     from the query.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQuery(java.sql.Connection, String, DbQuerySetup, DbQueryProcess)
     */
    public static void processQuery(Connection conn, String query, DbQueryProcess queryProcess) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(query);
        try {
            ResultSet rs = stat.executeQuery();
            queryProcess.processResultSet(rs);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }
    }

    /**
     * Process a query on the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param querySetup an object implementing the {@link DbQuerySetup} interface, used to set up parameters for the query.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to get
     *                          the query result.
     * @param <T> type of query result.
     * @return result of the query.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQuery(java.sql.Connection, String, DbQueryRetrieveData)
     */
    public static <T> T processQuery(
            Connection conn, String query,
            DbQuerySetup querySetup,
            DbQueryRetrieveData<T> queryRetrieveData
    ) throws SQLException
    {
        T data;

        PreparedStatement stat = conn.prepareStatement(query);
        try {
            querySetup.setupPreparedStatement(stat);
            ResultSet rs = stat.executeQuery();
            data = queryRetrieveData.processResultSet(rs);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }

        return data;
    }

    /**
     * Process a query on the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param queryRetrieveData an object implementing the {@link DbQueryRetrieveData} interface, used to get
     *                          the query result.
     * @param <T> type of query result.
     * @return result of the query.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQuery(java.sql.Connection, String, DbQuerySetup, DbQueryRetrieveData)
     */
    public static <T> T processQuery(Connection conn, String query, DbQueryRetrieveData<T> queryRetrieveData)
            throws SQLException
    {
        T data;

        PreparedStatement stat = conn.prepareStatement(query);
        try {
            ResultSet rs = stat.executeQuery();
            data = queryRetrieveData.processResultSet(rs);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }

        return data;
    }

    /**
     * Apply some updates to the database.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param updates an object implementing the {@link DbUpdates} interface, containing the code for the updates.
     * @throws SQLException if a database error occurs
     */
    public static void processUpdates(Connection conn, String query, DbUpdates updates) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(query);
        try {
            updates.execute(stat);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }
    }

    /**
     * Process some queries on the database and retrieve the result.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueries} interface, used to get the queries result.
     * @param <T> type of queries result.
     * @return result of the queries.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQueries(java.sql.Connection, String, DbQueriesNoReturn)
     */
    public static <T> T processQueries(Connection conn, String query, DbQueries<T> queries) throws SQLException {
        T data;

        PreparedStatement stat = conn.prepareStatement(query);
        try {
            data = queries.process(stat);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }

        return data;
    }

    /**
     * Process some queries on the database and retrieve the result.
     * @param conn database connection to use.
     * @param query SQL query.
     * @param queries an object implementing the {@link DbQueriesNoReturn} interface, used to get the queries result.
     * @throws SQLException if a database error occurs
     * @see DbUtils#processQueries(java.sql.Connection, String, DbQueries)
     */
    public static void processQueries(Connection conn, String query, DbQueriesNoReturn queries) throws SQLException {
        PreparedStatement stat = conn.prepareStatement(query);
        try {
            queries.process(stat);
            stat.close();
        } finally {
            preparedStatementSilentClose(stat);
        }
    }

}
