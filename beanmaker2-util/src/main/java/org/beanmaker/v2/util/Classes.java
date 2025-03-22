package org.beanmaker.v2.util;

public class Classes {

    /**
     * Instantiates an object of the specified class using its no-argument constructor.
     *
     * @param className the fully qualified name of the class to instantiate
     * @return a new instance of the specified class
     * @throws RuntimeException if the class cannot be found, accessed, or instantiated
     */
    public static Object instantiateClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate class: " + className, e);
        }
    }

}
