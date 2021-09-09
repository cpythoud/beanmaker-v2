package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface TableLocalOrderContext {

    long getId();

    String getCode();

    DBTransaction getDBTransaction();
}
