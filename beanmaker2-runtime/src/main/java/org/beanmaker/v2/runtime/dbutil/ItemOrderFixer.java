package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbTransaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemOrderFixer {

    private final DbTransaction transaction;
    private final String tableName;
    private final String secondaryField;

    private final List<List<Long>> idLists = new ArrayList<>();

    public ItemOrderFixer(DbTransaction transaction, String tableName) {
        this(transaction, tableName, null);
    }

    public ItemOrderFixer(DbTransaction transaction, String tableName, String secondaryField) {
        this.transaction = transaction;
        this.tableName = tableName;
        this.secondaryField = secondaryField;

        collectIds();
    }

    private void collectIds() {
        if (secondaryField == null) {
            idLists.add(getAllIdsInOrder());
            return;
        }

        var secondaryIds = getSecondaryIds();
        for (long id: secondaryIds)
            idLists.add(getIdsForSecondaryIdInOrder(id));
    }

    private List<Long> getAllIdsInOrder() {
        return transaction.processQuery(
                "SELECT id FROM " + tableName + " ORDER BY item_order",
                ListOf::longs
        );
    }

    private Set<Long> getSecondaryIds() {
        var secondaryIds = new HashSet<Long>();
        transaction.processQuery(
                "SELECT DISTINCT " + secondaryField + " FROM " + tableName,
                rs -> {
                    while (rs.next()) {
                        long id = rs.getLong(1);
                        if (rs.wasNull())
                            secondaryIds.add(0L);
                        else
                            secondaryIds.add(id);
                    }
                }
        );
        return secondaryIds;
    }

    private List<Long> getIdsForSecondaryIdInOrder(long id) {
        String query = id == 0L ?
                "SELECT id FROM " + tableName + " WHERE " + secondaryField + " IS NULL ORDER BY item_order" :
                "SELECT id FROM " + tableName + " WHERE " + secondaryField + " = ? ORDER BY item_order";

        return transaction.processQuery(
                query,
                stat -> {
                    if (id > 0)
                        stat.setLong(1, id);
                },
                ListOf::longs
        );
    }

    public void fixOrder() {
        for (var idList: idLists)
            fixOrder(idList);

        transaction.commit();
    }

    private void fixOrder(List<Long> idList) {
        long[] itemOrder = { 0 };
        for (long id: idList) {
            transaction.processUpdate(
                    "UPDATE " + tableName + " SET item_order = ? WHERE id = ?",
                    stat -> {
                        stat.setLong(1, ++itemOrder[0]);
                        stat.setLong(2, id);
                    }
            );
        }
    }

}
