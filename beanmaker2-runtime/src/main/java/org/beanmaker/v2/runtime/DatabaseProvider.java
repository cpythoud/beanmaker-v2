package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;

public interface DatabaseProvider {

    DB getDatabaseReference();

}
