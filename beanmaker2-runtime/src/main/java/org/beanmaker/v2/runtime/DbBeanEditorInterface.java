package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

import java.util.List;

public interface DbBeanEditorInterface extends CodeBasedReference {

    void setId(long id);
    void setId(long id,  DbTransaction transaction);
    void resetId();

    void updateDB();
    long updateDB(DbTransaction transaction);

    void preUpdateConversions();
    boolean isDataOK();
    List<ErrorMessage> getErrorMessages();

    void reset();
    void reset(DbTransaction transaction);
    void fullReset();

    void delete();

    void setCurrentDbBeanLanguage(DbBeanLanguage language);

    default void setCode(String code) {
        throw new UnsupportedOperationException("bean doesn't contain a code field");
    }

    DbBeanEditorInterface duplicate(DbTransaction transaction);

}
