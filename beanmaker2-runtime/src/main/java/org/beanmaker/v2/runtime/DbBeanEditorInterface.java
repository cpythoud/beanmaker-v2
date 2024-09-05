package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

public interface DbBeanEditorInterface {

    void setId(long id);
    void setId(long id,  DBTransaction transaction);
    void resetId();
    long getId();

    void updateDB();
    long updateDB(DBTransaction transaction);

    void preUpdateConversions();
    boolean isDataOK();
    List<ErrorMessage> getErrorMessages();

    void reset();
    void reset(DBTransaction transaction);
    void fullReset();

    void delete();

    void setCurrentDbBeanLanguage(DbBeanLanguage language);

}
