package org.beanmaker.v2.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * In conjunction with the {@link DbAccess} or {@link DbTransaction} class, use implementations of this interface
 * to insert the code for processing the results of a database query.
 */
public interface DbQueryProcess {

    /**
     * Implement this function to process the ResultSet that will be handed to your class
     * by {@link DbAccess} or {@link DbTransaction}.
     * @param rs the ResultSet from the execution of the query.
     * @throws SQLException if a database error occurs
     */
    void processResultSet(ResultSet rs) throws SQLException;

}
