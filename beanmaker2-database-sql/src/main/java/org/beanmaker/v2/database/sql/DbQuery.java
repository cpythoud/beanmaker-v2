package org.beanmaker.v2.database.sql;

import java.util.Objects;

public class DbQuery {

    private final String queryString;
    private final SecureQuery secureQuery;

    private DbQuery(String queryString, SecureQuery secureQuery) {
        this.queryString = queryString;
        this.secureQuery = secureQuery;
    }

    public static DbQuery of(String query) {
        return new DbQuery(query, null);
    }

    public static DbQuery of(SecureQuery query) {
        return new DbQuery(null, query);
    }

    boolean stringBased() {
        return queryString != null;
    }

    boolean secureQueryBased() {
        return secureQuery != null;
    }

    String string() {
        return Objects.requireNonNull(queryString);
    }

    SecureQuery secureQuery() {
        return Objects.requireNonNull(secureQuery);
    }

}
