package org.beanmaker.v2.runtime;

public interface MasterTableView {

    void setLanguage(DbBeanLanguage language);

    String getSummaryInfo();
    String getMasterTable();

}
