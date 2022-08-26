package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface DbBeanLabelEditor extends DbBeanLabel {

    void setId(long id);

    void cacheLabelsFromDB();
    void clearCache();

    void updateLater(DbBeanLanguage dbBeanLanguage, String text);

    long updateDB(DBTransaction transaction);
    void commitTextsToDatabase(DBTransaction transaction);

    void reset();
    void fullReset();

}
