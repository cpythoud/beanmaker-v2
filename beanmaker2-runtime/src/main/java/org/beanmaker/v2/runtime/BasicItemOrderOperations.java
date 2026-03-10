package org.beanmaker.v2.runtime;

public interface BasicItemOrderOperations {

    long getId();
    long getItemOrder();

    boolean isFirstInItemOrder();
    boolean isLastInItemOrder();

    default boolean isItemOrderLinkedToSecondaryField() {
        return false;
    }

    default long getItemOrderSecondaryFieldID() {
        throw new UnsupportedOperationException();
    }

}
