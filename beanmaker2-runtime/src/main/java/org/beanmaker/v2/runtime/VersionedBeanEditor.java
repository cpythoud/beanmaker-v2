package org.beanmaker.v2.runtime;

import org.beanmaker.v2.runtime.dbutil.Transactions;

import org.dbbeans.sql.DBTransaction;

public interface VersionedBeanEditor extends VersionedBean {

    DbBeanEditorInterface duplicate(DBTransaction transaction);

    VersionedBeanEditor newVersionedEditor();

    VersionedBeanEditor newVersionedEditor(DBTransaction transaction);

    static VersionedBeanEditor commitNewVersion(VersionedBeanEditor editor, DBTransaction dbTransaction) {
        VersionedBeanEditor[] newEditor = { null };
        Transactions.wrap(transaction -> {
            newEditor[0] = editor.newVersionedEditor(transaction);
        }, dbTransaction);
        return newEditor[0];
    }

    static VersionedBeanEditor initializeNewVersion(VersionedBeanEditor editor, DBTransaction transaction) {
        var newEditor = editor.duplicate(transaction);
        // * if id != 0, updateDB() has already been called (typically in extraDuplicatingActions())
        if (newEditor.getId() == 0)
            newEditor.updateDB(transaction);
        return (VersionedBeanEditor) newEditor;
    }

}
