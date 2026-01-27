package org.beanmaker.v2.runtime;

import org.beanmaker.v2.runtime.dbutil.Transactions;

import org.dbbeans.sql.DBTransaction;

public interface VersionedBeanEditor extends VersionedBean {

    DbBeanEditorInterface duplicate(DBTransaction transaction);

    VersionedBeanEditor newVersionedEditor();

    static DbBeanEditorInterface newVersionedEditor(VersionedBeanEditor editor, DBTransaction dbTransaction) {
        DbBeanEditorInterface[] newEditor = { null };
        Transactions.wrap(transaction -> {
            newEditor[0] = editor.duplicate(transaction);
            newEditor[0].updateDB(transaction);
        }, dbTransaction);
        return newEditor[0];
    }

}
