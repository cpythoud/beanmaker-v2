package org.beanmaker.v2.runtime;

import org.beanmaker.v2.runtime.dbutil.SingleElements;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

public interface VersionedBean extends IdBasedReference {

    int getBeanVersion();

    long getIdOriginalBean();

    long getIdLatestVersionedBean();

    long getIdLatestVersionedBean(DBTransaction transaction);

    boolean isLatestVersionedBean();

    boolean isLatestVersionedBean(DBTransaction transaction);

    boolean needsNewBeanVersion();

    boolean needsNewBeanVersion(DBTransaction transaction);

    static long getIdLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DBAccess dbAccess) {
        return getIdLatest(dbAccess, parameters.getDatabaseTableName(), getOriginalID(bean));
    }

    static long getIdLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DBTransaction transaction) {
        return getIdLatest(transaction, parameters.getDatabaseTableName(), getOriginalID(bean));
    }

    // TODO: rewrite by composing other functions in this class
    static boolean isLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DBAccess dbAccess) {
        long idLatest = getIdLatest(dbAccess, parameters.getDatabaseTableName(), getOriginalID(bean));
        return isTheLatestBean(bean, idLatest);
    }

    // TODO: rewrite by composing other functions in this class
    static boolean isLatestVersionedBean(VersionedBean bean, DbBeanParameters parameters, DBTransaction transaction) {
        long idLatest = getIdLatest(transaction, parameters.getDatabaseTableName(), getOriginalID(bean));
        return isTheLatestBean(bean, idLatest);
    }

    private static long getIdLatest(DBAccess dbAccess, String table, long originalID) {
        return SingleElements.getID(
                "SELECT id FROM %s WHERE id_original_bean=? ORDER BY bean_version DESC LIMIT 1".formatted(table),
                stat -> stat.setLong(1, originalID),
                dbAccess
        );
    }

    private static long getIdLatest(DBTransaction transaction, String table, long originalID) {
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
