package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

public interface DbBeanLabelEditor extends DbBeanLabel {

    void setId(long id);
    void setId(long id, DbTransaction transaction);

    void cacheLabelsFromDB();
    void cacheLabelsFromDB(DbTransaction transaction);
    void clearCache();
    boolean cachedValuesExist();

    void updateLater(DbBeanLanguage dbBeanLanguage, String text);

    long updateDB(DbTransaction transaction);
    void commitTextsToDatabase(DbTransaction transaction);

    void reset();
    void fullReset();

    DbBeanLabelEditor duplicateContent();
    boolean isContentIdenticalTo(DbBeanLabel label);

}
