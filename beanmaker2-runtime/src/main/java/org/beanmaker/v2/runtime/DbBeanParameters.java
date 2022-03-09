package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import java.util.List;

public interface DbBeanParameters {

    DbBeanLocalization getLocalization();

    DbBeanLocalization getLocalization(DbBeanLanguage language);

    default ItemOrderManager getItemOrderManager() {
        throw new UnsupportedOperationException();
    }

    String getDatabaseTableName();

    String getDatabaseFieldList();

    List<String> getNamingFields();

    List<String> getOrderingFields();

    default String getOrderByFields() {
        return Strings.concatWithSeparator(", ", getOrderingFields());
    }

}
