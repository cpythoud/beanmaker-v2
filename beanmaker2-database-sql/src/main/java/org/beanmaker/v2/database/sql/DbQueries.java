package org.beanmaker.v2.database.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * In conjunction with the {@link DbAccess} or {@link DbTransaction} class, use implementations of this interface
 * to insert the code for processing a series of database queries.
 */
public interface DbQueries<T> {

    /**
     * Implement this function to setup the parameters of the PreparedStatement that will be handed to your class
     * by {@link DbAccess} or {@link DbTransaction}.
     * @param stat the preparedStatement to be set up by your code.
     * @return the aggregated result of the queries.
     * @throws SQLException if a database error occurs
     */
    T process(PreparedStatement stat) throws SQLException;

}
