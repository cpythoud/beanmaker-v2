package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

public interface DbBeanEditor {

    long getId();
    void setId(long id);

    void resetId();

    void updateDB();
    long updateDB(DBTransaction transaction);

    void preUpdateConversions();

    boolean isDataOK();

    List<ErrorMessage> getErrorMessages();

    void reset();

    void fullReset();

    void delete();

    void setCurrentDbBeanLanguage(DbBeanLanguage language);

}
