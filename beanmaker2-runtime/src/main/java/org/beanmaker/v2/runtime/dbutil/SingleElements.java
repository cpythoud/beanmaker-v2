package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanEditor;
import org.beanmaker.v2.runtime.DbBeanInterface;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public final class SingleElements {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType BEAN_CONSTRUCTOR = MethodType.methodType(void.class, long.class);

    private SingleElements() { }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            DBQuerySetup querySetup,
            Class<? extends DbBeanInterface> beanClass,
            DBAccess dbAccess)
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
            DBAccess dbAccess)
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

    private static <B extends DbBeanInterface> B getSingleBean(Class<? extends DbBeanInterface> beanClass, ResultSet rs) throws SQLException {
        long id = getSingleID(rs);
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, BEAN_CONSTRUCTOR);
            return (B) constructorHandle.invokeWithArguments(id);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            String query,
            DBQuerySetup querySetup,
            Class<? extends DbBeanInterface> beanClass,
            DBTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
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
            DBTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        rs -> {
                            return getSingleBean(beanClass, rs);
                        }
                )
        );
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            String query,
            DBQuerySetup querySetup,
            E returnedEditor,
            DBAccess dbAccess)
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

    private static <E extends DbBeanEditor> E getSingleEditor(E returnedEditor, ResultSet rs) throws SQLException {
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

    public static long getID(String query, DBQuerySetup querySetup, DBAccess dbAccess) {
        return dbAccess.processQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            String query,
            DBQuerySetup querySetup,
            E returnedEditor,
            DBTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs);
                        }
                )
        );
    }

    public static long getID(String query, DBQuerySetup querySetup, DBTransaction transaction) {
        return transaction.addQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }
    
}
