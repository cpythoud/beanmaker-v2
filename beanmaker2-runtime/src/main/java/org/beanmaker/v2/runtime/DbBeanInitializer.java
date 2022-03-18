package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBAccess;

import java.sql.ResultSet;

import java.util.function.Consumer;

public final class DbBeanInitializer {

    private final long id;
    private final DbBeanParameters parameters;
    private final DBAccess dbAccess;
    private final Consumer<ResultSet> initializer;

    public static void initialize(long id, DbBeanParameters parameters, DBAccess dbAccess, Consumer<ResultSet> initializer) {
       new DbBeanInitializer(id, parameters, dbAccess, initializer).initialize();
    }

    private DbBeanInitializer(long id, DbBeanParameters parameters, DBAccess dbAccess, Consumer<ResultSet> initializer) {
        if (id < 1)
            throw new IllegalArgumentException("ID must be > 0");

        this.id = id;
        this.parameters = parameters;
        this.dbAccess = dbAccess;
        this.initializer = initializer;
    }

    private void initialize() {
        dbAccess.processQuery(
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
