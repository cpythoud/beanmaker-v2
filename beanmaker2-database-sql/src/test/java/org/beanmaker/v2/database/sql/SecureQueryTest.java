package org.beanmaker.v2.database.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SecureQueryTest {

    @Test
    void parseReplacesTableFieldAndFieldListPlaceholders() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_LIST_1] FROM [TABLE_1] WHERE [FIELD_1]=? AND [FIELD_2]=?")
                .fieldList(1, "id", "name", "email")
                .table(1, "users")
                .field(1, "status")
                .field(2, "type")
                .build()
                .parse(DbType.GENERIC_SQL);

        assertEquals(
                "SELECT id, name, email FROM users WHERE status=? AND type=?",
                query
        );
    }

    @Test
    void parseAppliesMySqlIdentifierPolicy() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_LIST_1] FROM [TABLE_1] WHERE [FIELD_1]=?")
                .fieldList(1, "id", "user_name")
                .table(1, "users")
                .field(1, "account_status")
                .build()
                .parse(DbType.MYSQL);

        assertEquals(
                "SELECT `id`, `user_name` FROM `users` WHERE `account_status`=?",
                query
        );
    }

    @Test
    void parseThrowsWhenTablePlaceholderIsMissing() {
        SecureQuery query = SecureQuery.builder("SELECT [FIELD_1] FROM [TABLE_1]")
                .field(1, "id")
                .build();

        assertThrows(IllegalArgumentException.class, () -> query.parse(DbType.GENERIC_SQL));
    }

    @Test
    void parseThrowsWhenFieldPlaceholderIsMissing() {
        SecureQuery query = SecureQuery.builder("SELECT [FIELD_1] FROM [TABLE_1]")
                .table(1, "users")
                .build();

        assertThrows(IllegalArgumentException.class, () -> query.parse(DbType.GENERIC_SQL));
    }

    @Test
    void parseThrowsWhenFieldListPlaceholderIsMissing() {
        SecureQuery query = SecureQuery.builder("SELECT [FIELD_LIST_1] FROM [TABLE_1]")
                .table(1, "users")
                .build();

        assertThrows(IllegalArgumentException.class, () -> query.parse(DbType.GENERIC_SQL));
    }

    @Test
    void parseKeepsPreparedStatementPlaceholdersUnchanged() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_1] FROM [TABLE_1] WHERE [FIELD_1]=? AND [FIELD_2]=?")
                .table(1, "users")
                .field(1, "id")
                .field(2, "status")
                .build()
                .parse(DbType.GENERIC_SQL);

        assertEquals(
                "SELECT id FROM users WHERE id=? AND status=?",
                query
        );
    }

    // ... existing code ...

    @Test
    void parseAcceptsCompositeFieldName() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_1] FROM [TABLE_1] WHERE [FIELD_1]=?")
                .table(1, "users")
                .field(1, "users.id")
                .build()
                .parse(DbType.GENERIC_SQL);

        assertEquals(
                "SELECT users.id FROM users WHERE users.id=?",
                query
        );
    }

    @Test
    void parseAcceptsCompositeFieldNamesInFieldList() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_LIST_1] FROM [TABLE_1]")
                .table(1, "users")
                .fieldList(1, "users.id", "users.name", "users.email")
                .build()
                .parse(DbType.GENERIC_SQL);

        assertEquals(
                "SELECT users.id, users.name, users.email FROM users",
                query
        );
    }

    @Test
    void parseQuotesCompositeFieldNamesWithMySqlPolicy() {
        String query = SecureQuery.builder(
                        "SELECT [FIELD_LIST_1] FROM [TABLE_1] WHERE [FIELD_1]=?")
                .table(1, "users")
                .fieldList(1, "users.id", "users.name", "users.email")
                .field(1, "users.status")
                .build()
                .parse(DbType.MYSQL);

        assertEquals(
                "SELECT `users`.`id`, `users`.`name`, `users`.`email` FROM `users` WHERE `users`.`status`=?",
                query
        );
    }

// ... existing code ...
}
