package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.database.sql.DBTransaction;

public interface DbBeanCsvImport {

    void importData(DBTransaction dbTransaction);

    void importData(DBTransaction dbTransaction, DataValidator validator);

}
