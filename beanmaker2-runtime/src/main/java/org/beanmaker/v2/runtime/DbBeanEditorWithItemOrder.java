package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.Db;
import org.beanmaker.v2.database.sql.DbAccess;
import org.beanmaker.v2.database.sql.DbTransaction;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public abstract class DbBeanEditorWithItemOrder extends DbBeanEditor implements BasicItemOrderOperations {

    private final DbAccess dbAccess;
    private final Db db;
    private final String tableName;

    protected final ItemOrderManager dbBeanItemOrderManager;

    protected long itemOrder;

    protected DbBeanEditorWithItemOrder(DbBeanParameters parameters, DbAccess dbAccess, Db db) {
        super(parameters);
        this.dbAccess = dbAccess;
        this.db = db;
        tableName = parameters.getDatabaseTableName();
        dbBeanItemOrderManager = parameters.getItemOrderManager();
    }

    // !! public instead of protected because required for CSV import !!
    // TODO: analyse implication - to be taken into account into item_order rewrite/debug/refactoring
    public void setItemOrder(long itemOrder) {
        this.itemOrder = itemOrder;
    }

    @Override
    public long getItemOrder() {
        return itemOrder;
    }

    public boolean isItemOrderRequired() {
        return true;
    }

    @Override
    public boolean isFirstInItemOrder() {
        return dbBeanItemOrderManager.isFirstInItemOrder(this);
    }

    @Override
    public boolean isLastInItemOrder() {
        return dbBeanItemOrderManager.isLastInItemOrder(this, dbAccess);
    }

    public void itemOrderMoveUp() {
        dbBeanItemOrderManager.itemOrderMoveUp(this, db);
    }

    public void itemOrderMoveDown() {
        dbBeanItemOrderManager.itemOrderMoveDown(this, db);
    }

    public void itemOrderMoveAfter(BasicItemOrderOperations bean) {
        dbBeanItemOrderManager.itemOrderMoveAfter(this, bean, db);
    }

    public void itemOrderMoveBefore(BasicItemOrderOperations bean) {
        dbBeanItemOrderManager.itemOrderMoveBefore(this, bean, db);
    }

    public void setItemOrderSecondaryFieldID(long secondaryFieldID) {
        if (!StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass().getName().equals("org.beanmaker.v2.runtime.ItemOrderManager"))
            throw new IllegalCallerException("Only org.beanmaker.v2.runtime.ItemOrderManager should call updateRecordForItemOrder()");

        // TODO: implement operations ?!?
    }

    public void updateRecordForItemOrder(DbTransaction transaction) {
        if (!StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass().getName().equals("org.beanmaker.v2.runtime.ItemOrderManager"))
            throw new IllegalCallerException("Only org.beanmaker.v2.runtime.ItemOrderManager should call updateRecordForItemOrder()");

        updateDB(transaction);
    }

    @Override
    public void delete(DbTransaction transaction) {
        checkReferenced(transaction);
        checkVersionedBean();
        preDeleteExtraDbActions(transaction);
        long curItemOrder;
        if (isLastInItemOrder())
            curItemOrder = 0;
        else
            curItemOrder = itemOrder;
        transaction.addUpdate("DELETE FROM " + tableName + " WHERE id=?", stat -> stat.setLong(1, id));
        if (curItemOrder > 0)
            dbBeanItemOrderManager.updateItemOrdersAbove(dbBeanItemOrderManager.getUpdateItemOrdersAboveQuery(), transaction, curItemOrder);
        deleteLabels(transaction);
        deleteExtraDbActions(transaction);
    }

}
