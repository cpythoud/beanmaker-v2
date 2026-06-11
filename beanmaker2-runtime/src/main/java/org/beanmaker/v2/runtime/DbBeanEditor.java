package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.dbutil.SidManager;
import org.beanmaker.v2.runtime.dbutil.Transactions;

import org.beanmaker.v2.util.SecurityTokenGenerator;
import org.beanmaker.v2.util.Types;

import java.util.Collections;
import java.util.List;

public abstract class DbBeanEditor implements DbBeanEditorInterface {

    protected final DbBeanParameters dbBeanParameters;
    protected final DbBeanLocalization dbBeanLocalization;

    protected long id = 0;

    protected DbBeanEditor(DbBeanParameters parameters) {
        dbBeanParameters = parameters;
        dbBeanLocalization = parameters.getLocalization();
    }

    public final void setId(long id) {
        setId(id, null);
    }

    public abstract void setId(long id,  DbTransaction transaction);

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

    public long updateDB(DbTransaction transaction) {
        preUpdateConversions(transaction);

        if (id == 0) {
            initBeanVersioning();
            id = createRecord(transaction);
            return id;
        }

        if (id > 0) {
            updateRecord(transaction);
            return id;
        }

        throw new IllegalStateException("id < 0");
    }

    protected void initBeanVersioning() { }

    protected abstract long createRecord(DbTransaction transaction);

    protected abstract void updateRecord(DbTransaction transaction);

    public final void preUpdateConversions() {
        preUpdateConversions(null);
    }

    protected void preUpdateConversions(DbTransaction transaction) {
        if (!isDataOK(transaction))
            throw new IllegalArgumentException(ErrorMessage.toStrings(getErrorMessages()));
    }

    public final boolean isDataOK() {
        return isDataOK(null);
    }

    protected abstract boolean isDataOK(DbTransaction transaction);

    protected List<FieldValidationFunction> getDbBeanGlobalValidationFunctions() {
        return Collections.emptyList();
    }

    public List<ErrorMessage> getErrorMessages() {
        return dbBeanLocalization.getErrorMessages();
    }

    public List<WarningMessage> getWarningMessages() {
        return dbBeanLocalization.getWarningMessages();
    }

    public final void reset() {
        reset(null);
    }

    public abstract void reset(DbTransaction transaction);

    public void fullReset() {
        reset();
        id = 0;
    }

    public final void delete() {
        DbTransaction transaction = createDBTransaction();
        delete(transaction);
        transaction.commit();
        fullReset();
    }

    public void delete(DbTransaction transaction) {
        checkReferenced(transaction);
        checkVersionedBean();
        preDeleteExtraDbActions(transaction);
        transaction.processUpdate(
                "DELETE FROM " + dbBeanParameters.getDatabaseTableName() + " WHERE id=?",
                stat -> stat.setLong(1, id)
        );
        deleteLabels(transaction);
        deleteExtraDbActions(transaction);
    }

    protected void checkReferenced(DbTransaction transaction) {
        if (dbBeanParameters.isReferenced(this, transaction))
            throw new IllegalStateException("Bean cannot be deleted because it is referenced in other data sets");
    }

    protected void checkVersionedBean() {
        if (Types.implementsInterface(this, VersionedBean.class) && !(((VersionedBean) this).isLatestVersionedBean()))
            throw new IllegalStateException("Only latest version of versioned bean can be deleted");
    }

    protected void preDeleteExtraDbActions(DbTransaction transaction) { }

    protected void deleteLabels(DbTransaction transaction) { }

    protected void deleteExtraDbActions(DbTransaction transaction) { }

    protected void preCreateExtraDbActions(DbTransaction transaction) { }

    protected void createExtraDbActions(DbTransaction transaction, long id) { }

    protected void preUpdateExtraDbActions(DbTransaction transaction) { }

    protected void updateExtraDbActions(DbTransaction transaction) { }

    public void setCurrentDbBeanLanguage(DbBeanLanguage language) {
        dbBeanLocalization.setLanguage(language);
    }

    protected abstract DbTransaction createDBTransaction();

    protected void extraDuplicatingActions(
            DbTransaction transaction,
            BeanDuplicator duplicator,
            DbBeanEditorInterface editor
    ) { }

    protected String createSid() {
        return SidManager.createSid();
    }

}
