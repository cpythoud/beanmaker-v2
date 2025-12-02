package org.beanmaker.v2.runtime;

public interface DbBeanMultilingual extends CodeBasedReference {

    long getIdLabel();

    DbBeanLabel getLabel();

    String getLabel(DbBeanLanguage dbBeanLanguage);
    String getSafeLabel(DbBeanLanguage dbBeanLanguage);

}
