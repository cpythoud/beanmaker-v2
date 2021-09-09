package org.beanmaker.v2.runtime;

public interface MultilingualIdNamePairBean {

    long getId();

    String getNameForPair(DbBeanLanguage dbBeanLanguage);
}
