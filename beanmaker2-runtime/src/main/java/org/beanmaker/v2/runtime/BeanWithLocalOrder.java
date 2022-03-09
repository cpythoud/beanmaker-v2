package org.beanmaker.v2.runtime;

public interface BeanWithLocalOrder {

    long getId();

    String getLocalOrderTable();

    long getItemOrder(final TableLocalOrderContext context);
}
