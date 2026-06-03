package org.beanmaker.v2.database.sql;

import java.util.stream.Stream;

import static org.beanmaker.v2.database.sql.StrictSqlIdentifierPolicy.requireQualifiedSqlIdentifier;
import static org.beanmaker.v2.database.sql.StrictSqlIdentifierPolicy.requireSqlIdentifier;

public final class MySqlIdentifierPolicy implements SqlIdentifierPolicy {

    @Override
    public String table(String table) {
        return quoteQualifiedSqlIdentifier(requireQualifiedSqlIdentifier(table, "table"));
    }

    @Override
    public String column(String column) {
        return quoteSqlIdentifier(requireSqlIdentifier(column, "column"));
    }

    public static String quoteSqlIdentifier(String identifier) {
        return "`" + identifier + "`";
    }

    public static String quoteQualifiedSqlIdentifier(String identifier) {
        return String.join(
                ".",
                Stream.of(identifier.split("\\."))
                        .map(MySqlIdentifierPolicy::quoteSqlIdentifier)
                        .toList()
        );
    }

}
