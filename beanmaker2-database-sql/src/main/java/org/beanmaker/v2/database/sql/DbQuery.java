package org.beanmaker.v2.database.sql;

import java.util.Objects;

public sealed interface DbQuery permits DbQuery.Raw, DbQuery.Secure {

    String parse(DbType dbType);

    static DbQuery of(String query) {
        return new Raw(query);
    }

    static DbQuery of(SecureQuery query) {
        return new Secure(query);
    }

    record Raw(String query) implements DbQuery {
        public Raw {
            Objects.requireNonNull(query);
        }

        @Override
        public String parse(DbType dbType) {
            return query;
        }
    }

    record Secure(SecureQuery query) implements DbQuery {
        public Secure {
            Objects.requireNonNull(query);
        }

        @Override
        public String parse(DbType dbType) {
            return query.parse(dbType);
        }
    }

}