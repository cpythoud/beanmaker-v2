package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DBTransaction;

public interface TableLocalOrderContext {

    long getId();

    String getCode();

    DBTransaction getDBTransaction();

}
