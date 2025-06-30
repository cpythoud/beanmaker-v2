package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface DbBeanEditorWithUniqueCode extends DbBeanEditorInterface {

    String getCode();
    void setCode(String code);

    boolean isCodeEmpty();
    boolean isCodeUnique();
    boolean isCodeUnique(DBTransaction transaction);

}
