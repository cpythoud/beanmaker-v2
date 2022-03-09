package org.beanmaker.v2.runtime;

public interface DbBeanMultilingual {

    long getId();

    long getIdLabel();

    DbBeanLabel getLabel();

    String getLabel(DbBeanLanguage dbBeanLanguage);

}
