package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbAccess;
import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.dbutil.SingleElements;

public interface VersionedBean extends IdBasedReference {

    int getBeanVersion();

    long getIdOriginalBean();

    long getIdLatestVersionedBean();

    long getIdLatestVersionedBean(DbTransaction transaction);

    boolean isLatestVersionedBean();

    boolean isLatestVersionedBean(DbTransaction transaction);

    boolean needsNewBeanVersion();

    boolean needsNewBeanVersion(DbTransaction transaction);

    default boolean isBeanVersioningActive() {
        return true;
    }

    static long getIdLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DbAccess dbAccess) {
        long idLatest = getIdLatest(dbAccess, parameters.getDatabaseTableName(), getOriginalID(bean));
        if (idLatest == 0)
            return bean.getId();
        return idLatest;
    }

    static long getIdLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DbTransaction transaction) {
        long idLatest = getIdLatest(transaction, parameters.getDatabaseTableName(), getOriginalID(bean));
        if (idLatest == 0)
            return bean.getId();
        return idLatest;
    }

    // TODO: rewrite by composing other functions in this class
    static boolean isLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DbAccess dbAccess) {
        long idLatest = getIdLatest(dbAccess, parameters.getDatabaseTableName(), getOriginalID(bean));
        return isTheLatestBean(bean, idLatest);
    }

    // TODO: rewrite by composing other functions in this class
    static boolean isLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DbTransaction transaction) {
        long idLatest = getIdLatest(transaction, parameters.getDatabaseTableName(), getOriginalID(bean));
        return isTheLatestBean(bean, idLatest);
    }

    private static long getIdLatest(DbAccess dbAccess, String table, long originalID) {
        return SingleElements.getID(
                "SELECT id FROM %s WHERE id_original_bean=? ORDER BY bean_version DESC LIMIT 1".formatted(table),
                stat -> stat.setLong(1, originalID),
                dbAccess
        );
    }

    private static long getIdLatest(DbTransaction transaction, String table, long originalID) {
        return SingleElements.getID(
                "SELECT id FROM %s WHERE id_original_bean=? ORDER BY bean_version DESC LIMIT 1".formatted(table),
                stat -> stat.setLong(1, originalID),
                transaction
        );
    }

    private static long getOriginalID(VersionedBean bean) {
        return bean.getIdOriginalBean() == 0 ? bean.getId() : bean.getIdOriginalBean();
    }

    private static boolean isTheLatestBean(VersionedBean bean, long idLatest) {
        if (idLatest == 0) {
            if (bean.getIdOriginalBean() == 0)
                return true;

            throw new AssertionError("Impossible: at least one bean version should exist");
        }
        return idLatest == bean.getId();
    }

}
