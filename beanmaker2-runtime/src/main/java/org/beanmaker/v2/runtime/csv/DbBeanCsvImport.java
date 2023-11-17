package org.beanmaker.v2.runtime.csv;

import org.dbbeans.sql.DBTransaction;

public interface DbBeanCsvImport {

    void importData(DBTransaction dbTransaction);

    void importData(DBTransaction dbTransaction, DataValidator validator);

}
