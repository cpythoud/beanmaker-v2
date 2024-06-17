package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanInterface;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Beans {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType BEAN_CONSTRUCTOR = MethodType.methodType(void.class, long.class);

    private Beans() { }

    @SuppressWarnings("unchecked")
    public static <B extends DbBeanInterface> B createBean(Class<? extends DbBeanInterface> beanClass, long id) {
        if (id < 0)
            throw new IllegalArgumentException("id < 0, must positive or 0");
        if (id == 0)
            return null;

        try {
            MethodHandle constructorHandle = LOOKUP.findConstructor(beanClass, BEAN_CONSTRUCTOR);
            return (B) constructorHandle.invokeWithArguments(id);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
