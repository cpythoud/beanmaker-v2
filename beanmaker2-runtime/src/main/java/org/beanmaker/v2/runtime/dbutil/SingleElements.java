package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbQuery;
import org.beanmaker.v2.database.sql.DbQuerySetup;
import org.beanmaker.v2.database.sql.DbTransaction;
import org.beanmaker.v2.database.sql.SecureQuery;

import org.beanmaker.v2.runtime.DbBeanEditorInterface;
import org.beanmaker.v2.runtime.DbBeanInterface;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public final class SingleElements {

    private SingleElements() { }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            DbQuerySetup querySetup,
            Class<? extends DbBeanInterface> beanClass,
            DbExecutor dbExecutor)
    {
        return Optional.ofNullable(
                dbExecutor.processQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleBean(beanClass, rs, dbExecutor);
                        }
                )
        );
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            Class<? extends DbBeanInterface> beanClass,
            DbExecutor dbExecutor)
    {
        return getBean(DbQuery.of(query), beanClass, dbExecutor);
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            SecureQuery query,
            Class<? extends DbBeanInterface> beanClass,
            DbExecutor dbExecutor)
    {
        return getBean(DbQuery.of(query), beanClass, dbExecutor);
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            DbQuery query,
            Class<? extends DbBeanInterface> beanClass,
            DbExecutor dbExecutor)
    {
        return Optional.ofNullable(
                dbExecutor.processQuery(
                        query,
                        rs -> {
                            return getSingleBean(beanClass, rs, dbExecutor);
                        }
                )
        );
    }

    private static <B extends DbBeanInterface> B getSingleBean(
            Class<? extends DbBeanInterface> beanClass,
            ResultSet rs,
            DbExecutor dbExecutor)
            throws SQLException
    {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        if (dbExecutor instanceof DbTransaction transaction)
            return Beans.createBean(beanClass, id, transaction);

        return Beans.createBean(beanClass, id);
    }

    public static <E extends DbBeanEditorInterface> Optional<E> getEditor(
            String query,
            DbQuerySetup querySetup,
            E returnedEditor,
            DbExecutor dbExecutor)
    {
        return getEditor(DbQuery.of(query), querySetup, returnedEditor, dbExecutor);
    }

    public static <E extends DbBeanEditorInterface> Optional<E> getEditor(
            SecureQuery query,
            DbQuerySetup querySetup,
            E returnedEditor,
            DbExecutor dbExecutor)
    {
        return getEditor(DbQuery.of(query), querySetup, returnedEditor, dbExecutor);
    }

    public static <E extends DbBeanEditorInterface> Optional<E> getEditor(
            DbQuery query,
            DbQuerySetup querySetup,
            E returnedEditor,
            DbExecutor dbExecutor)
    {
        return Optional.ofNullable(
                dbExecutor.processQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs, dbExecutor);
                        }
                )
        );
    }

    private static <E extends DbBeanEditorInterface> E getSingleEditor(
            E returnedEditor,
            ResultSet rs,
            DbExecutor dbExecutor
    ) throws SQLException
    {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        if (dbExecutor instanceof DbTransaction transaction)
            returnedEditor.setId(id, transaction);
        else
            returnedEditor.setId(id);

        return returnedEditor;
    }

    private static long getSingleID(ResultSet rs) throws SQLException {
        long id = 0;
        int count = 0;
        while (rs.next()) {
            id = rs.getLong(1);
            ++count;
        }

        if (count > 1)
            throw new IllegalStateException("Too many results: " + count);

        return id;
    }

    public static long getID(String query, DbQuerySetup querySetup, DbExecutor dbExecutor) {
        return getID(DbQuery.of(query), querySetup, dbExecutor);
    }

    public static long getID(SecureQuery query, DbQuerySetup querySetup, DbExecutor dbExecutor) {
        return getID(DbQuery.of(query), querySetup, dbExecutor);
    }

    public static long getID(DbQuery query, DbQuerySetup querySetup, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }
    
}
