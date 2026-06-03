package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.Db;

public interface DatabaseProvider {

    Db getDatabaseReference();

}
