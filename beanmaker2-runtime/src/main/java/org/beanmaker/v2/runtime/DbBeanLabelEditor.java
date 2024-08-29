package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface DbBeanLabelEditor extends DbBeanLabel {

    void setId(long id);
    void setId(long id, DBTransaction transaction);

    void cacheLabelsFromDB();
    void cacheLabelsFromDB(DBTransaction transaction);
    void clearCache();
    boolean cachedValuesExist();

    void updateLater(DbBeanLanguage dbBeanLanguage, String text);

    long updateDB(DBTransaction transaction);
    void commitTextsToDatabase(DBTransaction transaction);

    void reset();
    void fullReset();

}
