package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.database.sql.DbTransaction;

public interface DbBeanCsvImport {

    void importData(DbTransaction dbTransaction);

    void importData(DbTransaction dbTransaction, DataValidator validator);

}
