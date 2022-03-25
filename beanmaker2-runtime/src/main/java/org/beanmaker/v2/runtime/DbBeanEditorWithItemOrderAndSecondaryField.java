package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

public abstract class DbBeanEditorWithItemOrderAndSecondaryField extends DbBeanEditorWithItemOrder {

    private final DB db;
    private final String tableName;

    protected DbBeanEditorWithItemOrderAndSecondaryField(DbBeanParameters parameters, DBAccess dbAccess, DB db) {
        super(parameters, dbAccess, db);
        this.db = db;
        tableName = parameters.getDatabaseTableName();
    }

    public void itemOrderReassociateWith(long idAssociatedBean) {
        dbBeanItemOrderManager.itemOrderReassociateWith(this, idAssociatedBean, db);
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
            if (getItemOrderSecondaryFieldID() == 0)
                dbBeanItemOrderManager.updateItemOrdersAbove(dbBeanItemOrderManager.getUpdateItemOrdersAboveQueryWithNullSecondaryField(), transaction, curItemOrder);
            else
                dbBeanItemOrderManager.updateItemOrdersAbove(dbBeanItemOrderManager.getUpdateItemOrdersAboveQuery(), transaction, curItemOrder, getItemOrderSecondaryFieldID());
        deleteExtraDbActions(transaction);
    }

}
