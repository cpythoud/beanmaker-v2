package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

public interface TableLocalOrderContext {

    long getId();

    String getCode();

    DbTransaction getDBTransaction();

}
