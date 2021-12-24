package org.beanmaker.v2.codegen;

import java.util.List;

public interface DatabaseServer {

    List<String> getAvailableDatabases();

    List<String> getTables(final String dbName);

    List<Column> getColumns(final String dbName, final String tableName);

    List<OneToManyRelationship> getDetectedOneToManyRelationship(final String dbName, final String tableName);

}
