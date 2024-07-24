package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBFromDataSource;

public abstract class JndiDatabaseProvider implements DatabaseProvider {

    protected abstract String getJndiName();

    @Override
    public DB getDatabaseReference() {
        return new DBFromDataSource(getJndiName());
    }

}
