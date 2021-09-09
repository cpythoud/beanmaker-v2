package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;
import java.util.Locale;

public interface DbBeanInterface {

    void setId(long id);

    long getId();

    void resetId();

    void updateDB();
    long updateDB(DBTransaction transaction);

    void preUpdateConversions();

    boolean isDataOK();

    List<ErrorMessage> getErrorMessages();

    void reset();

    void fullReset();

    void delete();

    void setLocale(Locale locale);
}
