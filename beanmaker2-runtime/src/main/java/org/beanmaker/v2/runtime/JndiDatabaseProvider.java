package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.Db;
import org.beanmaker.v2.database.sql.DbFromDataSource;

public abstract class JndiDatabaseProvider implements DatabaseProvider {

    protected abstract String getJndiName();

    @Override
    public Db getDatabaseReference() {
        return new DbFromDataSource(getJndiName());
    }

}
