package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbAccess;
import org.beanmaker.v2.database.sql.DbQuerySetup;
import org.beanmaker.v2.database.sql.DbTransaction;

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
            DbAccess dbAccess)
    {
        return Optional.ofNullable(
                dbAccess.processQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleBean(beanClass, rs);
                        }
                )
        );
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            Class<? extends DbBeanInterface> beanClass,
            DbAccess dbAccess)
    {
        return Optional.ofNullable(
                dbAccess.processQuery(
                        query,
                        rs -> {
                            return getSingleBean(beanClass, rs);
                        }
                )
        );
    }

    private static <B extends DbBeanInterface> B getSingleBean(
            Class<? extends DbBeanInterface> beanClass,
            ResultSet rs)
            throws SQLException
    {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        return Beans.createBean(beanClass, id);
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            DbQuerySetup querySetup,
            Class<? extends DbBeanInterface> beanClass,
            DbTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleBean(beanClass, rs, transaction);
                        }
                )
        );
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            Class<? extends DbBeanInterface> beanClass,
            DbTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        rs -> {
                            return getSingleBean(beanClass, rs, transaction);
                        }
                )
        );
    }

    private static <B extends DbBeanInterface> B getSingleBean(
            Class<? extends DbBeanInterface> beanClass,
            ResultSet rs,
            DbTransaction transaction)
            throws SQLException
    {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        return Beans.createBean(beanClass, id, transaction);
    }

    public static <E extends DbBeanEditorInterface> Optional<E> getEditor(
            String query,
            DbQuerySetup querySetup,
            E returnedEditor,
            DbAccess dbAccess)
    {
        return Optional.ofNullable(
                dbAccess.processQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs);
                        }
                )
        );
    }

    private static <E extends DbBeanEditorInterface> E getSingleEditor(E returnedEditor, ResultSet rs) throws SQLException {
        long id = getSingleID(rs);
        if (id == 0)
            return null;
        
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

    public static long getID(String query, DbQuerySetup querySetup, DbAccess dbAccess) {
        return dbAccess.processQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }

    public static <E extends DbBeanEditorInterface> Optional<E> getEditor(
            String query,
            DbQuerySetup querySetup,
            E returnedEditor,
            DbTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs, transaction);
                        }
                )
        );
    }

    private static <E extends DbBeanEditorInterface> E getSingleEditor(
            E returnedEditor,
            ResultSet rs,
            DbTransaction transaction)
            throws SQLException
    {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        returnedEditor.setId(id, transaction);
        return returnedEditor;
    }

    public static long getID(String query, DbQuerySetup querySetup, DbTransaction transaction) {
        return transaction.addQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }
    
}
