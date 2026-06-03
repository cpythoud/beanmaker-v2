package org.beanmaker.v2.database.sql;

public enum DbType {
    GENERIC_SQL(SqlIdentifierPolicy.STRICT),
    MYSQL(SqlIdentifierPolicy.MYSQL),
    POSTGRESQL(SqlIdentifierPolicy.STRICT),
    SQLITE(SqlIdentifierPolicy.STRICT);

    private final SqlIdentifierPolicy sqlIdentifierPolicy;

    DbType(SqlIdentifierPolicy sqlIdentifierPolicy) {
        this.sqlIdentifierPolicy = sqlIdentifierPolicy;
    }

    public SqlIdentifierPolicy getSqlIdentifierPolicy() {
        return sqlIdentifierPolicy;
    }

}
