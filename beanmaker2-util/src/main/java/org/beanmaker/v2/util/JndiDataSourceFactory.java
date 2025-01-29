package org.beanmaker.v2.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

/**
 * The JndiDataSourceFactory is responsible for creating a DataSource object
 * by looking it up with a provided JNDI name.
 * <br>
 * This class encapsulates the logic for resolving a JNDI name in the context and
 * obtaining a DataSource instance that can be used for database operations.
 */
public class JndiDataSourceFactory {

    private String jndiName;


    /**
     * Sets the JNDI name for the data source lookup.
     *
     * @param jndiName the JNDI name to be used for looking up the data source.
     */
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    /**
     * Retrieves a DataSource instance by performing a JNDI lookup using the provided JNDI name.
     *
     * @return the DataSource object retrieved from the JNDI context
     * @throws RuntimeException if a NamingException occurs during the JNDI lookup
     */
    public DataSource getDataSource() {
        try {
            Context ctx = new InitialContext();
            return (DataSource) ctx.lookup(jndiName);
        } catch (NamingException e) {
            throw new RuntimeException("Failed to look up JNDI DataSource: " + jndiName, e);
        }
    }

}
