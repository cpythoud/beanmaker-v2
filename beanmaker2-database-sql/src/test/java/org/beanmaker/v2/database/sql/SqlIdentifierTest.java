package org.beanmaker.v2.database.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class SqlIdentifierTest {

    @Test
    public void testStrict() {
        var identifierPolicy = new StrictSqlIdentifierPolicy();
        assertEquals("my_table", identifierPolicy.table("my_table"));
        assertEquals("the_field", identifierPolicy.column("the_field"));
        assertEquals("table.col", identifierPolicy.column("table.col"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("users; DROP TABLE users"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("users u JOIN passwords p ON 1=1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.column("id OR 1=1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("schema.table"));
    }

    @Test
    public void testMySql() {
        var identifierPolicy = new org.beanmaker.v2.database.sql.MySqlIdentifierPolicy();
        assertEquals("`my_table`", identifierPolicy.table("my_table"));
        assertEquals("`the_field`", identifierPolicy.column("the_field"));
        assertEquals("`table`.`col`", identifierPolicy.column("table.col"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("users; DROP TABLE users"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("users u JOIN passwords p ON 1=1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.column("id OR 1=1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> identifierPolicy.table("schema.table"));
    }

}
