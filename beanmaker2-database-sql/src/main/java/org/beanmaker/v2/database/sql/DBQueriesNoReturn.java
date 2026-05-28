package org.beanmaker.v2.database.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * In conjunction with the {@link DBAccess} or {@link DBTransaction} class, use implementations of this interface to insert the code for processing
 * a series of database queries.
 */
public interface DBQueriesNoReturn {

    /**
     * Implement this function to setup the parameters of the PreparedStatement that will be handed to your class by {@link DBAccess} or {@link DBTransaction}.
     * @param stat the preparedStatement to be set up by your code.
     * @throws SQLException if a database error occurs
     */
    void process(PreparedStatement stat) throws SQLException;

}
