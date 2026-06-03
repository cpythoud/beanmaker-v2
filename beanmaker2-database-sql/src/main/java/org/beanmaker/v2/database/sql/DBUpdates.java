package org.beanmaker.v2.database.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * In conjunction with the {@link DBAccess} or {@link DBTransaction} class, you use implementations of this interface
 * to insert the code for setting up database updates.
 */
public interface DBUpdates {

    /**
     * Implement this function to setup the parameters of the PreparedStatement that will be handed to your class
     * by {@link DBAccess} or {@link DBTransaction}.
     * @param stat the preparedStatement to be set up by your code.
     * @throws SQLException if a database error occurs
     */
    void execute(PreparedStatement stat) throws SQLException;

}
