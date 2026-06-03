package org.beanmaker.v2.database.sql;

import java.sql.SQLException;

/**
 * This class is the unchecked equivalent of SQLException.
 * <br>
 * The JDK forces us to catch SQLExceptions in many places. Most of the time, these exceptions come from a runtime
 * condition and not a logic error. To not lose the information while at the same time not forcing the clients of our
 * code to handle SQLExceptions, we usually rethrow the exception as a RuntimeException.
 * <br>
 * We suggest you use SqlRuntimeException instead.
 */
public class SqlRuntimeException extends RuntimeException {

    /**
     * Creates a SqlRuntimeException from a SQLException
     * @param ex the SQLException to be rethrown as a SqlRuntimeException
     */
    public SqlRuntimeException(SQLException ex) {
        super(ex.getMessage(), ex);
    }

    /**
     * Returns the SQLException that was passed as an argument to the SqlRuntimeException constructor.
     * @return SQLException passed as an argument to the constructor.
     */
    public SQLException getSQLException() {
        return (SQLException) getCause();
    }

}
