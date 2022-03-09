package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

public abstract class AbstractDatabaseServer implements DatabaseServer {

    private final String serverFQDN;
    private final int serverPort;
    private final String username;
    private final String password;
    private final String engineName;
    private final String driverName;

    public AbstractDatabaseServer(String serverFQDN, int serverPort, String username, String password, String engineName, String driverName) {
        this.serverFQDN = serverFQDN;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.engineName = engineName;
        this.driverName = driverName;
    }

    public abstract List<String> getAvailableDatabases();

    public abstract List<String> getTables(String dbName);

    public abstract List<Column> getColumns(String dbName, String tableName);

    public abstract List<OneToManyRelationship> getDetectedOneToManyRelationship(String dbName, String tableName);


    protected Connection getConnection(String databaseName) {
        Connection conn;

        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(getURL(databaseName));
        } catch (ClassNotFoundException cnfex) {
            throw new RuntimeException(cnfex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return conn;
    }

    protected String suggestBeanClass(String tableName) {
        String base = Strings.capitalize(Strings.camelize(tableName));

        if (base.endsWith("sses") || base.endsWith("shes") || base.endsWith("oes") || base.endsWith("uses"))
            return base.substring(0, base.length() - 2);

        if (base.endsWith("ies"))
            return base.substring(0, base.length() - 3) + "y";

        if (base.endsWith("s"))
            return base.substring(0, base.length() - 1);

        return base;
    }

    protected String suggestJavaName(String tableName) {
        return Strings.uncapitalize(Strings.camelize(tableName));
    }

    private String getURL(String databaseName) {
        return "jdbc:" + engineName + "://" + serverFQDN + ":" + serverPort + "/" + databaseName + "?user=" + username + "&password=" + password;
    }

}
