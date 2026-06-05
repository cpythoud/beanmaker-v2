package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbExecutor;

import java.sql.ResultSet;

import java.util.function.Consumer;

public final class DbBeanInitializer {

    private final long id;
    private final DbBeanParameters parameters;
    private final DbExecutor dbExecutor;
    private final Consumer<ResultSet> initializer;

    public static void initialize(long id, DbBeanParameters parameters, DbExecutor dbExecutor, Consumer<ResultSet> initializer) {
       new DbBeanInitializer(id, parameters, dbExecutor, initializer).initialize();
    }

    private DbBeanInitializer(long id, DbBeanParameters parameters, DbExecutor dbExecutor, Consumer<ResultSet> initializer) {
        if (id < 1)
            throw new IllegalArgumentException("ID must be > 0");

        this.id = id;
        this.parameters = parameters;
        this.dbExecutor = dbExecutor;
        this.initializer = initializer;
    }

    private void initialize() {
        dbExecutor.processQuery(
                "SELECT " + parameters.getDatabaseFieldList() + " FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id),
                rs -> {
                    if (rs.next())
                        initializer.accept(rs);
                    else
                        throw new IllegalArgumentException("No bean with ID #" + id);
                }
        );
    }

}
