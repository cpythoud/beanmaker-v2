package org.beanmaker.v2.runtime;

import org.beanmaker.v2.runtime.dbutil.Transactions;

import org.dbbeans.sql.DBTransaction;

import java.util.Collections;
import java.util.List;

public abstract class DbBeanEditor implements DbBeanEditorInterface {

    protected final DbBeanLocalization dbBeanLocalization;
    private final String tableName;

    protected long id = 0;

    protected DbBeanEditor(DbBeanParameters parameters) {
        dbBeanLocalization = parameters.getLocalization();
        tableName = parameters.getDatabaseTableName();
    }

    public final void setId(long id) {
        setId(id, null);
    }

    public abstract void setId(long id,  DBTransaction transaction);

    public void resetId() {
        id = 0;
    }

    protected void refreshFromDataBase() {
        if (id == 0)
            throw new IllegalArgumentException("Cannot refresh bean not yet commited to database");

        setId(id);
    }

    public long getId() {
        return id;
    }

    public final void updateDB() {
        Transactions.wrap(
                this::updateDB,
                createDBTransaction()
        );
    }

    public long updateDB(DBTransaction transaction) {
        preUpdateConversions(transaction);

        if (id == 0) {
            id = createRecord(transaction);
            return id;
        }

        if (id > 0) {
            updateRecord(transaction);
            return id;
        }

        throw new IllegalStateException("id < 0");
    }

    protected abstract long createRecord(DBTransaction transaction);

    protected abstract void updateRecord(DBTransaction transaction);

    public final void preUpdateConversions() {
        preUpdateConversions(null);
    }

    protected void preUpdateConversions(DBTransaction transaction) {
        if (!isDataOK(transaction))
            throw new IllegalArgumentException(ErrorMessage.toStrings(getErrorMessages()));
    }

    public final boolean isDataOK() {
        return isDataOK(null);
    }

    protected abstract boolean isDataOK(DBTransaction transaction);

    protected List<FieldValidationFunction> getDbBeanGlobalValidationFunctions() {
        return Collections.emptyList();
    }

    public List<ErrorMessage> getErrorMessages() {
        return dbBeanLocalization.getErrorMessages();
    }

    public final void reset() {
        reset(null);
    }

    public abstract void reset(DBTransaction transaction);

    public void fullReset() {
        reset();
        id = 0;
    }

    public final void delete() {
        DBTransaction transaction = createDBTransaction();
        delete(transaction);
        transaction.commit();
        fullReset();
    }

    protected void delete(DBTransaction transaction) {
        preDeleteExtraDbActions(transaction);
        transaction.addUpdate("DELETE FROM " + tableName + " WHERE id=?", stat -> stat.setLong(1, id));
        deleteExtraDbActions(transaction);
    }

    protected void preDeleteExtraDbActions(DBTransaction transaction) { }

    protected void deleteExtraDbActions(DBTransaction transaction) { }

    protected void preCreateExtraDbActions(DBTransaction transaction) { }

    protected void createExtraDbActions(DBTransaction transaction, long id) { }

    protected void preUpdateExtraDbActions(DBTransaction transaction) { }

    protected void updateExtraDbActions(DBTransaction transaction) { }

    public void setCurrentDbBeanLanguage(DbBeanLanguage language) {
        dbBeanLocalization.setLanguage(language);
    }

    protected abstract DBTransaction createDBTransaction();

}
