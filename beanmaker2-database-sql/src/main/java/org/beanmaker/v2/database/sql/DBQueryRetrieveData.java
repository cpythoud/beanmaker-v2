package org.beanmaker.v2.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * In conjunction with the {@link DBAccess} or {@link DBTransaction} class, use implementations of this interface
 * to insert the code for processing the results of a database query.
 */
public interface DBQueryRetrieveData<T> {

    /**
     * Implement this function to process the ResultSet that will be handed to your class
     * by {@link DBAccess} or {@link DBTransaction}.
     * @param rs the ResultSet from the execution of the query.
     * @throws java.sql.SQLException if a database error occurs
     */
    T processResultSet(ResultSet rs) throws SQLException;

}
