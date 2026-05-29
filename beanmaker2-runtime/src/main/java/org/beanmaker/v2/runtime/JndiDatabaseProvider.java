package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DB;
import org.beanmaker.v2.database.sql.DBFromDataSource;

public abstract class JndiDatabaseProvider implements DatabaseProvider {

    protected abstract String getJndiName();

    @Override
    public DB getDatabaseReference() {
        return new DBFromDataSource(getJndiName());
    }

}
