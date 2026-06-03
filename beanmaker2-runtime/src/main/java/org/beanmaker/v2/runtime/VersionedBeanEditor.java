package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.dbutil.Transactions;

public interface VersionedBeanEditor extends VersionedBean {

    DbBeanEditorInterface duplicate(DbTransaction transaction);

    VersionedBeanEditor newVersionedEditor();

    VersionedBeanEditor newVersionedEditor(DbTransaction transaction);

    static VersionedBeanEditor commitNewVersion(VersionedBeanEditor editor, DbTransaction dbTransaction) {
        VersionedBeanEditor[] newEditor = { null };
        Transactions.wrap(transaction -> {
            newEditor[0] = editor.newVersionedEditor(transaction);
        }, dbTransaction);
        return newEditor[0];
    }

    static VersionedBeanEditor initializeNewVersion(VersionedBeanEditor editor, DbTransaction transaction) {
        var newEditor = editor.duplicate(transaction);
        // * if id != 0, updateDB() has already been called (typically in extraDuplicatingActions())
        if (newEditor.getId() == 0)
            newEditor.updateDB(transaction);
        return (VersionedBeanEditor) newEditor;
    }

}
