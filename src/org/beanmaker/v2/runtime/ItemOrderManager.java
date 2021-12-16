package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ItemOrderManager {

    private final String table;
    private final String secondaryField;

    public ItemOrderManager(String table) {
        this(table, null);
    }

    public ItemOrderManager(String table, String secondaryField) {
        this.table = table;
        this.secondaryField = secondaryField;
    }

    // * Queries

    public String getItemOrderMaxQuery() {
        String baseQuery = "SELECT MAX(item_order) FROM " + table;
        if (secondaryField == null)
            return baseQuery;

        return baseQuery + " WHERE " + secondaryField + "=?";
    }

    public String getItemOrderMaxQueryWithNullSecondaryField() {
        return "SELECT MAX(item_order) FROM " + table + " WHERE " + secondaryField + " IS NULL";
    }

    private String getIdFromItemOrderQuery() {
        String baseQuery = "SELECT id FROM " + table + " WHERE item_order=?";
        if (secondaryField == null)
            return baseQuery;

        return baseQuery + " AND " + secondaryField + " IS NULL";
    }

    // ! Non référencé, TODO: supprimer après tests
    /*private String getIdFromItemOrderQueryWithNullSecondaryField() {
        return "SELECT id FROM " + table + " WHERE item_order=? AND " + secondaryField + " IS NULL";
    }*/

    public String getUpdateItemOrdersAboveQuery() {
        String baseQuery = "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ?";
        if (secondaryField == null)
            return baseQuery;

        return baseQuery + " AND " + secondaryField + " IS NULL";
    }

    public String getUpdateItemOrdersAboveQueryWithNullSecondaryField() {
        return "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ? AND " + secondaryField + " IS NULL";
    }

    private String getDecreaseItemOrderBetweenQuery() {
        String baseQuery = "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ? AND item_order < ?";
        if (secondaryField == null)
            return baseQuery;

        return baseQuery + " AND " + secondaryField + "=?";
    }

    private String getDecreaseItemOrderBetweenQueryWithNullSecondaryField() {
        return "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ? AND item_order < ? AND " + secondaryField + " IS NULL";
    }

    private String getIncreaseItemOrderBetweenQuery() {
        String baseQuery = "UPDATE " + table + " SET item_order=item_order+1 WHERE item_order > ? AND item_order < ?";
        if (secondaryField == null)
            return baseQuery;

        return baseQuery + " AND " + secondaryField + "=?";
    }

    private String getIncreaseItemOrderBetweenQueryWithNullSecondaryField() {
        return "UPDATE " + table + " SET item_order=item_order+1 WHERE item_order > ? AND item_order < ? AND " + secondaryField + " IS NULL";
    }

    private String getPushItemOrdersUpQuery() {
        return "UPDATE " + table + " SET item_order=item_order+1 WHERE item_order > ? AND " + secondaryField + "=?";
    }

    private String getPushItemOrdersUpQueryWithNullSecondaryField() {
        return "UPDATE " + table + " SET item_order=item_order+1 WHERE item_order > ? AND " + secondaryField + " IS NULL";
    }

    private String getPushItemOrdersDownQuery() {
        return "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ? AND " + secondaryField + "=?";
    }

    private String getPushItemOrdersDownQueryWithNullSecondaryField() {
        return "UPDATE " + table + " SET item_order=item_order-1 WHERE item_order > ? AND " + secondaryField + " IS NULL";
    }

    // * Database operations

    // ! doublon, TODO: supprimer après tests de validation
    /*public static long getMaxItemOrder(DBAccess dbAccess, String query) {
        return dbAccess.processQuery(
                query,
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }*/

    private long getMaxItemOrder(DBAccess dbAccess, String query, long... parameters) {
        return dbAccess.processQuery(
                query,
                stat -> setupParameters(stat, 1, toList(parameters)),
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    public long getMaxItemOrder(DBTransaction transaction, String query, long... parameters) {
        return transaction.addQuery(
                query,
                stat -> setupParameters(stat, 1, toList(parameters)),
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    private void setupParameters(PreparedStatement stat, int startIndex, List<Long> parameters) throws SQLException {
        int index = startIndex - 1;
        for (long parameter: parameters)
            stat.setLong(++index, parameter);
    }

    private List<Long> toList(long... parameters) {
        List<Long> list = new ArrayList<Long>();
        for (long parameter: parameters)
            list.add(parameter);
        return list;
    }

    // ! doublons + non référencés, TODO: supprimer après tests de validation
    /*private long getMaxItemOrder(DBTransaction transaction, String query) {
        return transaction.addQuery(
                query,
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }

    private long getMaxItemOrder(DBTransaction transaction, String query, long... parameters) {
        return transaction.addQuery(
                query,
                stat -> setupParameters(stat, 1, toList(parameters)),
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                });
    }*/

    private long getItemOrderSwapValue(long itemOrder, boolean moveUp) {
        if (moveUp)
            return itemOrder - 1;

        return itemOrder + 1;
    }

    private void itemOrderMoveUp(DB db, DbBeanEditorWithItemOrder editor) {
        itemOrderMove(db, editor, null, true);
    }

    private void itemOrderMove(
            DB db,
            DbBeanEditorWithItemOrder editor,
            List<Long> parameters,
            boolean moveUp)
    {
        DBTransaction transaction = new DBTransaction(db);

        long swapPositionWithBeanId = transaction.addQuery(
                getIdFromItemOrderQuery(),
                stat -> {
                    stat.setLong(1, getItemOrderSwapValue(editor.getItemOrder(), moveUp));
                    if (parameters != null)
                        setupParameters(stat, 2, parameters);
                },
                rs -> {
                    if (rs.next())
                        return rs.getLong(1);

                    throw new IllegalArgumentException("No such item order # " + composeIdForException(editor.getId(), parameters) + ". Cannot effect change.  Please check database integrity.");
                });

        if (moveUp) {
            incItemOrder(transaction, swapPositionWithBeanId, table);
            decItemOrder(transaction, editor.getId(), table);
        } else {
            decItemOrder(transaction, swapPositionWithBeanId, table);
            incItemOrder(transaction, editor.getId(), table);
        }

        transaction.commit();
    }

    private String composeIdForException(long id, List<Long> parameters) {
        if (parameters == null)
            return "[" + id + "]";

        StringBuilder buf = new StringBuilder();
        buf.append("[").append(id).append(", ");
        for (long parameter: parameters)
            buf.append(parameter).append(", ");
        buf.delete(buf.length() - 2, buf.length());
        buf.append("]");

        return buf.toString();
    }

    private void incItemOrder(DBTransaction transaction, long id, String table) {
        setItemOrder(transaction, id, table, getItemOrder(transaction, id, table) + 1);
    }

    private void decItemOrder(DBTransaction transaction, long id, String table) {
        setItemOrder(transaction, id, table, getItemOrder(transaction, id, table) - 1);
    }

    private long getItemOrder(DBTransaction transaction, long id, String table) {
        return transaction.addQuery(
                "SELECT item_order FROM " + table + " WHERE id=?",
                stat ->  stat.setLong(1, id),
                rs -> {
                    if (rs.next())
                        return rs.getLong(1);

                    throw new IllegalArgumentException("No such ID #" + id);
                });
    }

    private void setItemOrder(DBTransaction transaction, long id, String table, long itemOrder) {
        transaction.addUpdate(
                "UPDATE " + table + " SET item_order=? WHERE id=?",
                stat -> {
                    stat.setLong(1, itemOrder);
                    stat.setLong(2, id);
                });
    }

    private void itemOrderMoveUp(DB db, DbBeanEditorWithItemOrder editor, long... parameters) {
        itemOrderMove(db, editor, toList(parameters), true);
    }

    private void itemOrderMoveDown(DB db, DbBeanEditorWithItemOrder editor) {
        itemOrderMove(db, editor, null, false);
    }

    private void itemOrderMoveDown(DB db, DbBeanEditorWithItemOrder editor, long... parameters) {
        itemOrderMove(db, editor, toList(parameters), false);
    }

    // ! doublon, TODO: supprimer après tests de validation
    /*private void updateItemOrdersAbove(String query, DBTransaction transaction, long threshold) {
        updateItemOrdersAbove(query, transaction, threshold, null);
    }*/

    public void updateItemOrdersAbove(String query, DBTransaction transaction, long threshold, long... parameters) {
        transaction.addUpdate(
                query,
                stat -> {
                    stat.setLong(1, threshold);
                    if (parameters.length > 0) {
                        int index = 1;
                        for (long parameter: parameters)
                            stat.setLong(++index, parameter);
                    }
                });
    }

    // ! doublon, TODO: supprimer après tests de validation
    /*public static void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound) {
        updateItemOrdersInBetween(query, transaction, lowerBound, upperBound, null);
    }*/

    private void updateItemOrdersInBetween(String query, DBTransaction transaction, long lowerBound, long upperBound, long... parameters) {
        transaction.addUpdate(
                query,
                stat -> {
                    stat.setLong(1, lowerBound);
                    stat.setLong(2, upperBound);
                    if (parameters.length > 0) {
                        int index = 2;
                        for (long parameter: parameters)
                            stat.setLong(++index, parameter);
                    }
                });
    }

    // * Bean & BeanEditor function implementations

    public boolean isFirstInItemOrder(BasicItemOrderOperations itemOrderEntity) {
        if (itemOrderEntity.getId() == 0)
            throw new IllegalArgumentException("Item Order operations not allowed on beans that have not been saved to the database");

        return itemOrderEntity.getItemOrder() == 1;
    }

    public boolean isLastInItemOrder(BasicItemOrderOperations itemOrderEntity, DBAccess dbAccess) {
        if (itemOrderEntity.getId() == 0)
            throw new IllegalArgumentException("Item Order operations not allowed on beans that have not been saved to the database");

        if (secondaryField == null)
            return itemOrderEntity.getItemOrder() == getMaxItemOrder(dbAccess, getItemOrderMaxQuery());

        if (itemOrderEntity.getItemOrderSecondaryFieldID() == 0) {
            return itemOrderEntity.getItemOrder() == getMaxItemOrder(dbAccess, getItemOrderMaxQueryWithNullSecondaryField());

        }
        return itemOrderEntity.getItemOrder() == getMaxItemOrder(dbAccess, getItemOrderMaxQuery(), itemOrderEntity.getItemOrderSecondaryFieldID());
    }

    public void itemOrderMoveUp(DbBeanEditorWithItemOrder editor, DB db) {
        if (editor.getId() == 0)
            throw new IllegalArgumentException("Item Order operations not allowed on beans that have not been saved to the database");

        if (isFirstInItemOrder(editor))
            throw new IllegalArgumentException("Cannot move Item Order above position 1 which it currently occupies");

        if (secondaryField == null)
            itemOrderMoveUp(db, editor);
        else {
            if (editor.getItemOrderSecondaryFieldID() > 0)
                itemOrderMoveUp(db, editor, editor.getItemOrderSecondaryFieldID());
            else
                itemOrderMoveUp(db, editor);
        }

        editor.setItemOrder(editor.getItemOrder() - 1);
    }

    public void itemOrderMoveDown(DbBeanEditorWithItemOrder editor, DB db) {
        if (editor.getId() == 0)
            throw new IllegalArgumentException("Item Order operations not allowed on beans that have not been saved to the database");

        if (isLastInItemOrder(editor, new DBAccess(db)))
            throw new IllegalArgumentException("Cannot move Item Order below max position: " + editor.getItemOrder());

        if (secondaryField == null)
            itemOrderMoveDown(db, editor);
        else {
            if (editor.getItemOrderSecondaryFieldID() > 0)
                itemOrderMoveDown(db, editor, editor.getItemOrderSecondaryFieldID());
            else
                itemOrderMoveDown(db, editor);
        }

        editor.setItemOrder(editor.getItemOrder() + 1);
    }

    public void itemOrderMoveAfter(DbBeanEditorWithItemOrder editor, BasicItemOrderOperations otherItem, DB db) {
        if (secondaryField == null) {
            if (editor.getItemOrder() > otherItem.getItemOrder())
                itemOrderMove(editor, otherItem.getItemOrder() + 1, getIncreaseItemOrderBetweenQuery(), otherItem.getItemOrder(), editor.getItemOrder(), db);
            else
                itemOrderMove(editor, otherItem.getItemOrder(), getDecreaseItemOrderBetweenQuery(), editor.getItemOrder(), otherItem.getItemOrder() + 1, db);
        } else {
            if (editor.getItemOrderSecondaryFieldID() == otherItem.getItemOrderSecondaryFieldID()) {
                if (editor.getItemOrder() > otherItem.getItemOrder()) {
                    if (editor.getItemOrderSecondaryFieldID() == 0)
                        itemOrderMove(editor, otherItem.getItemOrder() + 1, getIncreaseItemOrderBetweenQueryWithNullSecondaryField(), otherItem.getItemOrder(), editor.getItemOrder(), db);
                    else
                        itemOrderMove(editor, otherItem.getItemOrder() + 1, getIncreaseItemOrderBetweenQuery(), otherItem.getItemOrder(), editor.getItemOrder(), db, editor.getItemOrderSecondaryFieldID());
                } else
                if (editor.getItemOrderSecondaryFieldID() == 0)
                    itemOrderMove(editor, otherItem.getItemOrder(), getDecreaseItemOrderBetweenQueryWithNullSecondaryField(), editor.getItemOrder(), otherItem.getItemOrder() + 1, db);
                else
                    itemOrderMove(editor, otherItem.getItemOrder(), getDecreaseItemOrderBetweenQuery(), editor.getItemOrder(), otherItem.getItemOrder() + 1, db, editor.getItemOrderSecondaryFieldID());
            } else
                itemOrderMove(editor, otherItem.getItemOrder() + 1, otherItem, db);
        }
    }

    public void itemOrderMoveBefore(DbBeanEditorWithItemOrder editor, BasicItemOrderOperations otherItem, DB db) {
        if (secondaryField == null) {
            if (editor.getItemOrder() > otherItem.getItemOrder())
                itemOrderMove(editor, otherItem.getItemOrder(), getIncreaseItemOrderBetweenQuery(), otherItem.getItemOrder() - 1, editor.getItemOrder(), db);
            else
                itemOrderMove(editor, otherItem.getItemOrder() - 1, getDecreaseItemOrderBetweenQuery(), editor.getItemOrder(), otherItem.getItemOrder(), db);
        } else {
            if (editor.getItemOrderSecondaryFieldID() == otherItem.getItemOrderSecondaryFieldID()) {
                if (editor.getItemOrder() > otherItem.getItemOrder()) {
                    if (editor.getItemOrderSecondaryFieldID() == 0)
                        itemOrderMove(editor, otherItem.getItemOrder(), getIncreaseItemOrderBetweenQueryWithNullSecondaryField(), otherItem.getItemOrder() - 1, editor.getItemOrder(), db);
                    else
                        itemOrderMove(editor, otherItem.getItemOrder(), getIncreaseItemOrderBetweenQuery(), otherItem.getItemOrder() - 1, editor.getItemOrder(), db, editor.getItemOrderSecondaryFieldID());
                } else
                if (editor.getItemOrderSecondaryFieldID() == 0)
                    itemOrderMove(editor, otherItem.getItemOrder() - 1, getDecreaseItemOrderBetweenQueryWithNullSecondaryField(), editor.getItemOrder(), otherItem.getItemOrder(), db);
                else
                    itemOrderMove(editor, otherItem.getItemOrder() - 1, getDecreaseItemOrderBetweenQuery(), editor.getItemOrder(), otherItem.getItemOrder(), db, editor.getItemOrderSecondaryFieldID());
            } else
                itemOrderMove(editor, otherItem.getItemOrder(), otherItem, db);
        }
    }

    private void itemOrderMove(DbBeanEditorWithItemOrder editor, long newItemOrder, String query, long lowerBound, long upperBound, DB db, long... parameters) {
        DBTransaction transaction = new DBTransaction(db);
        updateItemOrdersInBetween(query, transaction, lowerBound, upperBound, parameters);
        itemOrderMoveCompleteTransaction(editor, newItemOrder, transaction);
    }

    private void itemOrderMove(DbBeanEditorWithItemOrder editor, long newItemOrder, BasicItemOrderOperations otherItem, DB db) {
        if (editor.getItemOrderSecondaryFieldID() == 0)
            itemOrderMove(editor, newItemOrder, getPushItemOrdersUpQueryWithNullSecondaryField(), newItemOrder - 1, getPushItemOrdersDownQuery(), otherItem, db);
        else if (otherItem.getItemOrderSecondaryFieldID() == 0)
            itemOrderMove(editor, newItemOrder, getPushItemOrdersUpQuery(), newItemOrder - 1, getPushItemOrdersDownQueryWithNullSecondaryField(), otherItem, db);
        else
            itemOrderMove(editor, newItemOrder, getPushItemOrdersUpQuery(), newItemOrder - 1, getPushItemOrdersDownQuery(), otherItem, db);
    }

    private void itemOrderMove(DbBeanEditorWithItemOrder editor, long newItemOrder, String queryDest, long destLowerBound, String queryOrig, BasicItemOrderOperations item, DB db) {
        DBTransaction transaction = new DBTransaction(db);
        if (editor.getItemOrderSecondaryFieldID() == 0)
            updateItemOrdersAbove(queryDest, transaction, destLowerBound);
        else
            updateItemOrdersAbove(queryDest, transaction, destLowerBound, editor.getItemOrderSecondaryFieldID());
        if (item.getItemOrderSecondaryFieldID() == 0)
            updateItemOrdersAbove(queryOrig, transaction, editor.getItemOrder());
        else
            updateItemOrdersAbove(queryOrig, transaction, editor.getItemOrder(), editor.getItemOrderSecondaryFieldID());
        itemOrderMoveCompleteTransaction(editor, newItemOrder, transaction);
    }

    public void itemOrderReassociateWith(DbBeanEditorWithItemOrder editor, long secondaryFieldID, DB db) {
        if (editor.getId() == 0)
            throw new IllegalArgumentException("Bean must be saved in DB before reassociation.");
        if (editor.getItemOrderSecondaryFieldID() == secondaryFieldID)
            throw new IllegalArgumentException("Association already exists.");

        itemOrderMove(editor, secondaryFieldID, db);
    }

    private void itemOrderMove(DbBeanEditorWithItemOrder editor, long secondaryFieldID, DB db) {
        DBTransaction transaction = new DBTransaction(db);
        long newItemOrder;
        if (secondaryFieldID == 0)
            newItemOrder = getMaxItemOrder(transaction, getItemOrderMaxQueryWithNullSecondaryField()) + 1;
        else
            newItemOrder = getMaxItemOrder(transaction, getItemOrderMaxQuery(), secondaryFieldID) + 1;
        if (editor.getItemOrderSecondaryFieldID() == 0)
            updateItemOrdersAbove(getUpdateItemOrdersAboveQueryWithNullSecondaryField(), transaction, editor.getItemOrder());
        else
            updateItemOrdersAbove(getUpdateItemOrdersAboveQuery(), transaction, editor.getItemOrder(), editor.getItemOrderSecondaryFieldID());
        editor.setItemOrderSecondaryFieldID(secondaryFieldID);
        itemOrderMoveCompleteTransaction(editor, newItemOrder, transaction);
    }

    private void itemOrderMoveCompleteTransaction(DbBeanEditorWithItemOrder editor, long newItemOrder, DBTransaction transaction) {
        editor.setItemOrder(newItemOrder);
        editor.updateRecordForItemOrder(transaction);
        transaction.commit();
    }

}
