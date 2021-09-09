package org.beanmaker.v2.runtime;

public interface DbBeanWithItemOrderInterface<T> extends DbBeanInterface {

    long getItemOrder();

    boolean isFirstItemOrder();
    boolean isLastItemOrder();

    void itemOrderMoveUp();
    void itemOrderMoveDown();

    void itemOrderMoveAfter(T bean);
    void itemOrderMoveBefore(T bean);
}
