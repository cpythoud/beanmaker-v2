package org.beanmaker.v2.codegen;

import org.dbbeans.sql.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySQLDatabaseServer extends AbstractDatabaseServer {

    private static final int DEFAULT_PORT = 3306;
    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    private static final List<String> OFF_LIMIT_DBS = Arrays.asList("information_schema", "mysql");

    public MySQLDatabaseServer(String serverFQDN, int serverPort, String username, String password) {
        super(serverFQDN, serverPort, username, password, "mysql", DRIVER_NAME);
    }

    public MySQLDatabaseServer(String serverFQDN, String username, String password) {
        super(serverFQDN, DEFAULT_PORT, username, password, "mysql", DRIVER_NAME);
    }

    @Override
    public List<String> getAvailableDatabases() {
        List<String> dbList = new ArrayList<String>();

        Connection conn = null;
        try {
            conn = getConnection("mysql");
            PreparedStatement stat = conn.prepareStatement("SHOW DATABASES");
            try {
                ResultSet rs = stat.executeQuery();
                while (rs.next()) {
                    if (!OFF_LIMIT_DBS.contains(rs.getString(1)))
                        dbList.add(rs.getString(1));
                }
                stat.close();
            } finally {
                DBUtils.preparedStatementSilentClose(stat);
            }
            conn.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return dbList;
    }

    @Override
    public List<String> getTables(String dbName) {
        if (!getAvailableDatabases().contains(dbName))
            throw new IllegalArgumentException("La base de données " + dbName + " est introuvable sur ce serveur.");

        List<String> tableList = new ArrayList<String>();

        Connection conn = null;
        try {
            conn = getConnection(dbName);
            PreparedStatement stat = conn.prepareStatement("SHOW TABLES");
            try {
                ResultSet rs = stat.executeQuery();
                while (rs.next()) {
                    tableList.add(rs.getString(1));
                }
                stat.close();
            } finally {
                DBUtils.preparedStatementSilentClose(stat);
            }
            conn.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return tableList;
    }

    @Override
    public List<Column> getColumns(String dbName, String tableName) {
        if (!getAvailableDatabases().contains(dbName))
            throw new IllegalArgumentException("La base de données " + dbName + " est introuvable sur ce serveur.");

        if (!getTables(dbName).contains(tableName))
            throw new IllegalArgumentException("La base de données " + dbName + " ne contient pas de table " + tableName + ".");

        List<Column> cols = new ArrayList<Column>();

        Connection conn = null;
        try {
            conn = getConnection(dbName);
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM " + tableName);
            try {
                ResultSetMetaData md = stat.executeQuery().getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++)
                    cols.add(new Column(
                            md.getColumnTypeName(i), md.getColumnName(i), md.getColumnDisplaySize(i), md.getPrecision(i), md.getScale(i),
                            md.isAutoIncrement(i), md.isNullable(i) == ResultSetMetaData.columnNoNulls));
                stat.close();
            } finally {
                DBUtils.preparedStatementSilentClose(stat);
            }
            conn.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return cols;
    }

    @Override
    public List<OneToManyRelationship> getDetectedOneToManyRelationship(String dbName, String tableName) {
        int tableNameLength = tableName.length();

        List<String> likes = new ArrayList<>();
        likes.add(tableName);
        for (int i = 1; i < 4; i++) {
            if (tableNameLength > i)
                likes.add(tableName.substring(0, tableNameLength - i));
        }

        List<String> tables = getTables(dbName);

        List<OneToManyRelationship> relationships = new ArrayList<OneToManyRelationship>();

        Connection conn = null;
        try {
            conn = getConnection(dbName);
            for (String table: tables) {
                PreparedStatement stat = conn.prepareStatement("SELECT * FROM " + table);
                try {
                    ResultSetMetaData md = stat.executeQuery().getMetaData();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        String sqlName = md.getColumnName(i);
                        if (sqlName.startsWith("id_")) {
                            String fieldName = sqlName.substring(3);
                            for (String like: likes) {
                                if (fieldName.startsWith(like)) {
                                    String idSqlType = md.getColumnTypeName(i).split(" ")[0];
                                    String idJavaType = null;
                                    if (idSqlType.endsWith("INT")) {
                                        if (idSqlType.equals("BIGINT") || (idSqlType.equals("INT") && (md.getColumnTypeName(i).contains("UNSIGNED")))) {
                                            idJavaType = "long";
                                        } else {
                                            if (!(md.getColumnTypeName(i).equals("TINYINT UNSIGNED") && md.getPrecision(i) == 1))
                                                idJavaType = "int";
                                        }
                                    }
                                    if (idJavaType != null)
                                        relationships.add(new OneToManyRelationship(suggestBeanClass(table), suggestJavaName(table), table, sqlName));
                                    break;
                                }
                            }
                        }
                    }
                    stat.close();
                } finally {
                    DBUtils.preparedStatementSilentClose(stat);
                }
            }
            conn.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            DBUtils.connectionSilentClose(conn);
        }

        return relationships;
    }

}
