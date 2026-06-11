package org.beanmaker.v2.codegen;

import org.beanmaker.v2.database.sql.DbType;

import java.util.List;

public interface DatabaseServer {

    DbType getDbType();

    List<String> getAvailableDatabases();

    List<String> getTables(String dbName);

    List<Column> getColumns(String dbName, String tableName);

    List<Column> getColumns(String dbname, String tableName, ReservedDatabaseFieldManager fields);

    List<OneToManyRelationship> getDetectedOneToManyRelationship(String dbName, String tableName);

}
