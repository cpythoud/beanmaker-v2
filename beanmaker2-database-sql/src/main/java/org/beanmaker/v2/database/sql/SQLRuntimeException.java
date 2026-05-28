package org.beanmaker.v2.database.sql;

import java.sql.SQLException;

/**
 * This class is the unchecked equivalent of SQLException.
 * <br>
 * The JDK forces us to catch SQLExceptions in many places. Most of the time, these exceptions come from a runtime
 * condition and not a logic error. To not lose the information while at the same time not forcing the clients of our
 * code to handle SQLExceptions, we usually rethrow the exception as a RuntimeException.
 *
 * We suggest you use SQLRuntimeException instead.
 */
public class SQLRuntimeException extends RuntimeException {

    /**
     * Creates a SQLRuntimeException from a SQLException
     * @param ex the SQLException to be rethrown as a SQLRuntimeException
     */
    public SQLRuntimeException(SQLException ex) {
        super(ex.getMessage(), ex);
    }

    /**
     * Returns the SQLException that was passed as an argument to the SQLRuntimeException constructor.
     * @return SQLException passed as an argument to the constructor.
     */
    public SQLException getSQLException() {
        return (SQLException) getCause();
    }

}
