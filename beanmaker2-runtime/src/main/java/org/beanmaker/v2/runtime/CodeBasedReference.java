package org.beanmaker.v2.runtime;

public interface CodeBasedReference extends IdBasedReference {

    default boolean hasBeanUniqueCodeField() {
        return false;
    }

    default boolean hasVersionedBeanCodeField() {
        return false;
    }

    default boolean hasBeanCodeField() {
        return hasBeanUniqueCodeField() || hasVersionedBeanCodeField();
    }

    default String getCode() {
        throw new UnsupportedOperationException("bean doesn't contain a code field");
    }

}
