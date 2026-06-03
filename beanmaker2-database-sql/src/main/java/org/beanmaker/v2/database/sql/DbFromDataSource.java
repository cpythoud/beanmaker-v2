package org.beanmaker.v2.database.sql;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class implements the {@link Db} interface from a DataSource name.
 */
public class DbFromDataSource implements Db {

    private final DataSource dataSource;

    /**
     * Creates a new Db object from a datasource.
     * @param dataSourceName the name of the DataSource to be used
     * @throws RuntimeException if the name cannot be resolved to a DataSource, the NamingException can be retrieved
     * from RuntimeException.getCause()
     */
    public DbFromDataSource(String dataSourceName) {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(dataSourceName);
        } catch (NamingException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Creates a new Db object from a datasource.
     * @param dataSource the DataSource to be used
     */
    public DbFromDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a connection to the database.
     * @return a Connection from the DataSource initialized by the constructor
     * @throws SQLException if a database error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
