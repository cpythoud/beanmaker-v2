package org.beanmaker.v2.util;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

public final class Types {

    public static <T> List<T> getSubtypeList(List<? extends T> supertypes) {
        return new ArrayList<>(supertypes);
    }

    public static  <T> T createInstanceOf(String className, Class<T> interfaceClass) {
        T instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            if (interfaceClass.isAssignableFrom(clazz))
                instance = interfaceClass.cast(clazz.getDeclaredConstructor().newInstance());
            else
                throw new IllegalArgumentException("The provided class doesn't implement " + interfaceClass.getName());
        } catch (ClassNotFoundException ex) {
            System.out.println("No class found with the specified name: " + className);
        } catch (NoSuchMethodException ex) {
            System.out.println(className + " doesn't have a default constructor");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            System.out.println("Failed to create an instance of class: " + className);
        }
        return instance;
    }

}
