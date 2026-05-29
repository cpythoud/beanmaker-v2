package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DB;

public interface DatabaseProvider {

    DB getDatabaseReference();

}
