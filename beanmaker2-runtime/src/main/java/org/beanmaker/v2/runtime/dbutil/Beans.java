package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanEditor;
import org.beanmaker.v2.runtime.DbBeanInterface;

import org.dbbeans.sql.DBTransaction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Beans {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType CONSTRUCTOR = MethodType.methodType(void.class);
    private static final MethodType ID_CONSTRUCTOR = MethodType.methodType(void.class, long.class);
    private static final MethodType TRANSACTION_CONSTRUCTOR =
            MethodType.methodType(void.class, long.class, DBTransaction.class);

    private Beans() { }

    @SuppressWarnings("unchecked")
    public static <B extends DbBeanInterface> B createBean(Class<? extends DbBeanInterface> beanClass, long id) {
        if (id < 0)
            throw new IllegalArgumentException("id < 0, must be positive or 0");
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, ID_CONSTRUCTOR);
            return (B) constructorHandle.invokeWithArguments(id);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <B extends DbBeanInterface> B createBean(
            Class<? extends DbBeanInterface> beanClass,
            long id,
            DBTransaction transaction
    )
    {
        if (id < 0)
            throw new IllegalArgumentException("id < 0, must be positive or 0");
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, TRANSACTION_CONSTRUCTOR);
            return (B) constructorHandle.invokeWithArguments(id, transaction);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends DbBeanEditor> E createEditor(Class<? extends DbBeanEditor> beanClass) {
        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, CONSTRUCTOR);
            return (E) constructorHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends DbBeanEditor> E createEditor(Class<? extends DbBeanEditor> beanClass, long id) {
        if (id < 0)
            throw new IllegalArgumentException("id < 0, must be positive or 0");
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, ID_CONSTRUCTOR);
            return (E) constructorHandle.invokeWithArguments(id);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends DbBeanEditor> E createEditor(
            Class<? extends DbBeanEditor> beanClass,
            long id,
            DBTransaction transaction
    )
    {
        if (id < 0)
            throw new IllegalArgumentException("id < 0, must be positive or 0");
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, TRANSACTION_CONSTRUCTOR);
            return (E) constructorHandle.invokeWithArguments(id, transaction);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
