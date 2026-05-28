package org.beanmaker.v2.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Use a direct instantiation of a JDBC driver to implements the {@link DB} interface.
 * <br>
 * This class is not optimized in any way and does not implement any caching mechanism. It should therefore only
 * be used for testing or extremely simple applications requiring few database connexions.
 * <br>
 * Your application should use a container providing DataSources and you should use the DBFromDataSource class instead of this one.
 */
public class DBFromJDBCDriver implements DB {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Creates a DB object
     * @param driver the full class name of the driver (ex.: com.mysql.jdbc.Driver), must be in the CLASSPATH or otherwise accessible
     * @param url the JDBC url (ex.: jdbc:mysql://localhost/)
     * @param username to access the database
     * @param password to access the database
     */
    public DBFromJDBCDriver(String driver, String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException cnfex) {
            throw new RuntimeException(cnfex.getMessage(), cnfex);
        }
    }

    /**
     * Returns a connection to the database.
     * @return a Connection from the JDBC driver manager; it's the caller responsibility to close that connection
     * @throws SQLException if something goes wrong with the driver
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
