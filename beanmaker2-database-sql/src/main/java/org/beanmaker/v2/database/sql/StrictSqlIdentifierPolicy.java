package org.beanmaker.v2.database.sql;

import java.util.regex.Pattern;

public final class StrictSqlIdentifierPolicy implements SqlIdentifierPolicy {

    private static final Pattern SQL_IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final Pattern QUALIFIED_SQL_IDENTIFIER =
            Pattern.compile("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*");


    @Override
    public String table(String table) {
        return requireQualifiedSqlIdentifier(table, "table");
    }

    @Override
    public String column(String column) {
        return requireSqlIdentifier(column, "column");
    }

    public static String requireSqlIdentifier(String identifier, String description) {
        if (identifier == null)
            throw new IllegalArgumentException("SQL " + description + " cannot be null.");

        if (!SQL_IDENTIFIER.matcher(identifier).matches())
            throw new IllegalArgumentException("Invalid SQL " + description + ": " + identifier);

        return identifier;
    }

    public static String requireQualifiedSqlIdentifier(String identifier, String description) {
        if (identifier == null)
            throw new IllegalArgumentException("SQL " + description + " cannot be null.");

        if (!QUALIFIED_SQL_IDENTIFIER.matcher(identifier).matches())
            throw new IllegalArgumentException("Invalid SQL " + description + ": " + identifier);

        return identifier;
    }

}
