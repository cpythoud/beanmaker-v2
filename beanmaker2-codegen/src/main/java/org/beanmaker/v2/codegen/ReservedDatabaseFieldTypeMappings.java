package org.beanmaker.v2.codegen;

import org.beanmaker.v2.database.sql.DbType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ReservedDatabaseFieldTypeMappings {

    private static final Map<DbType, Map<ReservedDatabaseField, List<String>>> DB_MAPPINGS;

    static {
        var mysql = new EnumMap<ReservedDatabaseField, List<String>>(ReservedDatabaseField.class);
        mysql.put(
                ReservedDatabaseField.ID,
                List.of("TINYINT UNSIGNED", "SMALLINT UNSIGNED", "MEDIUMINT UNSIGNED", "INT UNSIGNED")
        );
        mysql.put(
                ReservedDatabaseField.SID,
                List.of("TINYINT UNSIGNED", "SMALLINT UNSIGNED", "MEDIUMINT UNSIGNED", "INT UNSIGNED")
        );
        mysql.put(
                ReservedDatabaseField.LAST_UPDATE,
                List.of("BIGINT UNSIGNED")
        );
        mysql.put(
                ReservedDatabaseField.MODIFIED_BY,
                List.of("CHAR", "VARCHAR")
        );
        mysql.put(
                ReservedDatabaseField.ITEM_ORDER,
                List.of("TINYINT UNSIGNED", "SMALLINT UNSIGNED", "MEDIUMINT UNSIGNED", "INT UNSIGNED")
        );
        mysql.put(
                ReservedDatabaseField.VERSION,
                List.of("TINYINT UNSIGNED", "SMALLINT UNSIGNED", "MEDIUMINT UNSIGNED")
        );
        mysql.put(
                ReservedDatabaseField.ID_ORIGINAL_BEAN,
                List.of("TINYINT UNSIGNED", "SMALLINT UNSIGNED", "MEDIUMINT UNSIGNED", "INT UNSIGNED")
        );

        var map = new EnumMap<DbType, Map<ReservedDatabaseField, List<String>>>(DbType.class);
        map.put(DbType.GENERIC_SQL, Collections.unmodifiableMap(mysql));  // TODO: adjust later
        map.put(DbType.MYSQL, Collections.unmodifiableMap(mysql));
        // TODO: create entries for other databases (i.e., PostgreSQL & SQLite)
        DB_MAPPINGS = Collections.unmodifiableMap(map);
    }

    public static Map<ReservedDatabaseField, List<String>> getSqlTypeMap(DbType dbType) {
        return Objects.requireNonNull(DB_MAPPINGS.get(dbType));
    }

    public static String suggestJavaType(DbType dbType, String sqlTypeName, int precision) {
        if (dbType.equals(DbType.GENERIC_SQL) || dbType.equals(DbType.MYSQL))
            return mysqlSuggestJavaType(sqlTypeName, precision);

        throw new UnsupportedOperationException("Suggestions not yet implemented for database type: " + dbType);
    }

    private static String mysqlSuggestJavaType(String sqlTypeName, int precision) {
        String type = sqlTypeName.split(" ")[0];

        if (type.endsWith("INT")) {
            if (type.equals("BIGINT") || (type.equals("INT") && (sqlTypeName.contains("UNSIGNED"))))
                return "Long";
            if (sqlTypeName.equals("TINYINT UNSIGNED") && precision == 1)
                return "Boolean";
            return "Integer";
        }

        return switch (type) {
            case "DATE" -> "Date";
            case "TIME" -> "Time";
            case "DATETIME", "TIMESTAMP" -> "Timestamp";
            default -> "String";
        };
    }

}
