package org.beanmaker.v2.runtime;

public interface BasicItemOrderOperations {

    long getId();
    long getItemOrder();

    boolean isFirstInItemOrder();
    boolean isLastInItemOrder();

    default long getItemOrderSecondaryFieldID() {
        throw new UnsupportedOperationException();
    }

}
