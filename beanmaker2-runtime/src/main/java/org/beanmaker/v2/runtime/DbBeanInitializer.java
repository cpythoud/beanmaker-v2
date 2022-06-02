package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

import java.sql.ResultSet;

import java.util.function.Consumer;

public final class DbBeanInitializer {

    private final long id;
    private final DbBeanParameters parameters;
    private final DBAccess dbAccess;
    private final DBTransaction transaction;
    private final Consumer<ResultSet> initializer;

    public static void initialize(long id, DbBeanParameters parameters, DBAccess dbAccess, Consumer<ResultSet> initializer) {
       new DbBeanInitializer(id, parameters, dbAccess, initializer).initialize();
    }

    public static void initialize(long id, DbBeanParameters parameters, DBTransaction transaction, Consumer<ResultSet> initializer) {
        new DbBeanInitializer(id, parameters, transaction, initializer).initialize();
    }

    private DbBeanInitializer(long id, DbBeanParameters parameters, DBAccess dbAccess, Consumer<ResultSet> initializer) {
        if (id < 1)
            throw new IllegalArgumentException("ID must be > 0");

        this.id = id;
        this.parameters = parameters;
        this.dbAccess = dbAccess;
        transaction = null;
        this.initializer = initializer;
    }

    private DbBeanInitializer(long id, DbBeanParameters parameters, DBTransaction transaction, Consumer<ResultSet> initializer) {
        if (id < 1)
            throw new IllegalArgumentException("ID must be > 0");

        this.id = id;
        this.parameters = parameters;
        dbAccess = null;
        this.transaction = transaction;
        this.initializer = initializer;
    }

    private void initialize() {
        if (dbAccess != null) {
            if (transaction != null)
                throw new AssertionError("Impossible: both dbAccess & transaction are defined. This is a major bug, contact the developers.");
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
            return;
        }

        if (transaction != null) {
            transaction.addQuery(
                    "SELECT " + parameters.getDatabaseFieldList() + " FROM " + parameters.getDatabaseTableName() + " WHERE id=?",
                    stat -> stat.setLong(1, id),
                    rs -> {
                        if (rs.next())
                            initializer.accept(rs);
                        else
                            throw new IllegalArgumentException("No bean with ID #" + id);
                    }
            );
            return;
        }

        throw new AssertionError("Impossible: dbAccess & transaction are both null. This is a major bug, contact the developers.");
    }

}
