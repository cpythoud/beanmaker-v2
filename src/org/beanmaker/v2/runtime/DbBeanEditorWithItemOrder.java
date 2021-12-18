package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

public abstract class DbBeanEditorWithItemOrder extends DbBeanEditor implements BasicItemOrderOperations {

    private final DBAccess dbAccess;
    private final DB db;
    private final String tableName;

    protected final ItemOrderManager dbBeanItemOrderManager;

    protected long itemOrder;

    protected DbBeanEditorWithItemOrder(DbBeanParameters parameters, DBAccess dbAccess, DB db) {
        super(parameters);
        this.dbAccess = dbAccess;
        this.db = db;
        tableName = parameters.getDatabaseTableName();
        dbBeanItemOrderManager = parameters.getItemOrderManager();
    }

    protected void setItemOrder(long itemOrder) {
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
        checkCaller();
    }

    public void updateRecordForItemOrder(DBTransaction transaction) {
        checkCaller();
    }

    private void checkCaller() {
        if (!StackWalker.getInstance().getCallerClass().getName().equals("org.beanmaker.v2.runtime.ItemOrderManager"))
            throw new IllegalCallerException("Only org.beanmaker.v2.runtime.ItemOrderManager should call updateRecordForItemOrder()");
    }

    @Override
    protected void delete(DBTransaction transaction) {
        preDeleteExtraDbActions(transaction);
        long curItemOrder;
        if (isLastInItemOrder())
            curItemOrder = 0;
        else
            curItemOrder = itemOrder;
        transaction.addUpdate("DELETE FROM " + tableName + " WHERE id=?", stat -> stat.setLong(1, id));
        if (curItemOrder > 0)
            dbBeanItemOrderManager.updateItemOrdersAbove(dbBeanItemOrderManager.getUpdateItemOrdersAboveQuery(), transaction, curItemOrder);
        deleteExtraDbActions(transaction);
    }

}
