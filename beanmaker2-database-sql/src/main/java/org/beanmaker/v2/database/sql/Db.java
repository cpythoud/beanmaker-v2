package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to represent a database.
 * <br>
 * Implementations should be immutable, therefore an application should never need to create more than
 * one such instance to represent the same database, unless there are differences in access rights.
 */
public interface Db {

    /**
     * @return a Connection from the database. The user is responsible for closing the connection.
     */
    Connection getConnection() throws SQLException;

}
