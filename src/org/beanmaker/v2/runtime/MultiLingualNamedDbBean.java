package org.beanmaker.v2.runtime;

public interface MultiLingualNamedDbBean extends DbBeanInterface {

    String getDisplayName(DbBeanLanguage dbBeanLanguage);
}
