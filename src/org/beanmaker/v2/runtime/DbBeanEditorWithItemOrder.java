package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface DbBeanEditorWithItemOrder extends DbBeanEditor, BasicItemOrderOperations {

    default void setItemOrder(long itemOrder) {
        checkCaller();
    }

    void itemOrderMoveUp();
    void itemOrderMoveDown();

    void itemOrderMoveAfter(BasicItemOrderOperations editor);
    void itemOrderMoveBefore(BasicItemOrderOperations editor);

    default void setItemOrderSecondaryFieldID(long secondaryFieldID) {
        checkCaller();
    }

    default void updateRecordForItemOrder(DBTransaction transaction) {
        checkCaller();
    }

    private void checkCaller() {
        if (!StackWalker.getInstance().getCallerClass().getName().equals("org.beanmaker.v2.runtime.ItemOrderManager"))
            throw new IllegalCallerException("Only org.beanmaker.v2.runtime.ItemOrderManager should call updateRecordForItemOrder()");
    }

}
