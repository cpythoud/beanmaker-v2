package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DbBeanParameters {

    DbBeanLocalization getLocalization();

    DbBeanLocalization getLocalization(DbBeanLanguage language);

    default ItemOrderManager getItemOrderManager() {
        throw new UnsupportedOperationException();
    }

    String getDatabaseTableName();

    String getDatabaseFieldList();

    List<String> getNamingFields();

    List<String> getOrderingFields();

    default String getOrderByFields() {
        return Strings.concatWithSeparator(", ", getOrderingFields());
    }

    record DatabaseReference(String table, String field) { }

    static List<DatabaseReference> createDatabaseReferenceList(String field, String... tables) {
        var list = new ArrayList<DatabaseReference>();
        for (String table: tables)
            list.add(new DatabaseReference(table, field));
        return list;
    }

    default List<DatabaseReference> getDatabaseReferences() {
        return Collections.emptyList();
    }

    default String getReferenceQuery(DatabaseReference reference) {
        return "SELECT * FROM %s WHERE %s=?".formatted(reference.table(), reference.field());
    }

    default boolean isReferenced(DbBeanInterface bean, DBAccess dbAccess) {
        for (var reference: getDatabaseReferences()) {
            if (dbAccess.processQuery(
                    getReferenceQuery(reference),
                    stat -> stat.setLong(1, bean.getId()),
                    ResultSet::next
            )) return true;
        }
        return false;
    }

    default boolean isReferenced(DbBeanInterface bean, DBTransaction transaction) {
        for (var reference: getDatabaseReferences()) {
            if (transaction.addQuery(
                    getReferenceQuery(reference),
                    stat -> stat.setLong(1, bean.getId()),
                    ResultSet::next
            )) return true;
        }
        return false;
    }

}
