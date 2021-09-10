package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

public interface DbBeanLabel {

    void setId(long id);
    long getId();
    String getName();

    String get(DbBeanLanguage dbBeanLanguage);
    String get(DbBeanLanguage dbBeanLanguage, Object... parameters);
    String get(DbBeanLanguage dbBeanLanguage, List<Object> parameters);

    void cacheLabelsFromDB();
    void clearCache();

    void updateLater(DbBeanLanguage dbBeanLanguage, String text);

    long updateDB(DBTransaction transaction);
    void commitTextsToDatabase(DBTransaction transaction);

    boolean hasDataFor(DbBeanLanguage dbBeanLanguage);

    void reset();
    void fullReset();
}
