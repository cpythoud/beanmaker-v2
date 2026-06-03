package org.beanmaker.v2.database.sql;

public interface SqlIdentifierPolicy {

    SqlIdentifierPolicy STRICT = new StrictSqlIdentifierPolicy();
    SqlIdentifierPolicy MYSQL = new MySqlIdentifierPolicy();

    String table(String table);

    String column(String column);

}
