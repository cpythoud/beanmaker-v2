package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbAccess;
import org.beanmaker.v2.database.sql.DbQuerySetup;
import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.DbBeanInterface;

import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class Associations {

    private Associations() { }

    public static boolean hasItem(String pairingTable, String field, long idBean, DbAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field + " FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                ResultSet::next
        );
    }

    public static boolean hasItem(String pairingTable, String field, long idBean, DbTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field + " FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                ResultSet::next
        );
    }

    public static int getItemCount(String pairingTable, String field, long idBean, DbAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT COUNT(" + field + ") FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }
        );
    }

    public static int getItemCount(String pairingTable, String field, long idBean, DbTransaction transaction) {
        return transaction.addQuery(
                "SELECT COUNT(" + field + ") FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, long idBean1, long idBean2, DbAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field1 + " FROM " + pairingTable + " WHERE " + field1 + "=? AND " + field2 + "=?",
                stat -> {
                    stat.setLong(1, idBean1);
                    stat.setLong(2, idBean2);
                },
                ResultSet::next
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, long idBean1, long idBean2, DbTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field1 + " FROM " + pairingTable + " WHERE " + field1 + "=? AND " + field2 + "=?",
                stat -> {
                    stat.setLong(1, idBean1);
                    stat.setLong(2, idBean2);
                },
                ResultSet::next
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbAccess dbAccess) {
        return arePaired(pairingTable, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbTransaction transaction) {
        return arePaired(pairingTable, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    private static String getAssociationQuery(String table, String field1, String field2) {
        return "REPLACE INTO " + table + " (" + field1 + ", " + field2 + ") VALUES (?, ?)";
    }

    public static void createAssociation(String table, String field1, String field2, long id1, long id2, DbAccess dbAccess) {
        dbAccess.processUpdate(
                getAssociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void createAssociation(String table, String field1, String field2, long id1, long id2, DbTransaction transaction) {
        transaction.addUpdate(
                getAssociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void createAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbAccess dbAccess) {
        createAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static void createAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbTransaction transaction) {
        createAssociation(table, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    private static String getDissociationQuery(String table, String field1, String field2) {
        return "DELETE FROM " + table + " WHERE " + field1 + "=? AND " + field2 + "=?";
    }

    public static void removeAssociation(String table, String field1, String field2, long id1, long id2, DbAccess dbAccess) {
        dbAccess.processUpdate(
                getDissociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void removeAssociation(String table, String field1, String field2, long id1, long id2, DbTransaction transaction) {
        transaction.addUpdate(
                getDissociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void removeAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbAccess dbAccess) {
        removeAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static void removeAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DbTransaction transaction) {
        removeAssociation(table, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    public static boolean associationExists(String table, String field, long id, DbAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field + " FROM " + table + " WHERE " + field + "=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean associationExists(String table, String field, long id, DbTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field + " FROM " + table + " WHERE " + field + "=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean associationExists(String table, String field, DbBeanInterface bean, DbAccess dbAccess) {
        return associationExists(table, field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(String table, String field, DbBeanInterface bean, DbTransaction transaction) {
        return associationExists(table, field, bean.getId(), transaction);
    }

    public static <T extends DbBeanInterface, A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            T referencedBean,
            A returnedBean,
            DbAccess dbAccess)
    {
        return getAssociatedBean(table, referenceIdField, referencedBean.getId(), returnedBean, dbAccess);
    }

    public static <T extends DbBeanInterface, A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            T referencedBean,
            A returnedBean,
            DbTransaction transaction)
    {
        return getAssociatedBean(table, referenceIdField, referencedBean.getId(), returnedBean, transaction);
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            long idReferencedBean,
            A returnedBean,
            DbAccess dbAccess)
    {
        return getAssociatedBean(
                "SELECT id FROM " + table + " WHERE " + referenceIdField + "=?",
                stat -> stat.setLong(1, idReferencedBean),
                returnedBean,
                dbAccess
        );
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            long idReferencedBean,
            A returnedBean,
            DbTransaction transaction)
    {
        return getAssociatedBean(
                "SELECT id FROM " + table + " WHERE " + referenceIdField + "=?",
                stat -> stat.setLong(1, idReferencedBean),
                returnedBean,
                transaction
        );
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String query,
            DbQuerySetup querySetup,
            A returnedBean,
            DbAccess dbAccess)
    {
        return SingleElements.getBean(query, querySetup, returnedBean.getClass(), dbAccess);
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String query,
            DbQuerySetup querySetup,
            A returnedBean,
            DbTransaction transaction)
    {
        return SingleElements.getBean(query, querySetup, returnedBean.getClass(), transaction);
    }

    public static boolean associationExists(Collection<String> tables, String field, long id, DbAccess dbAccess) {
        for (String table: tables)
            if (associationExists(table, field, id, dbAccess))
                return true;

        return false;
    }

    public static boolean associationExists(Collection<String> tables, String field, long id, DbTransaction transaction) {
        for (String table: tables)
            if (associationExists(table, field, id, transaction))
                return true;

        return false;
    }

    public static boolean associationExists(Collection<String> tables, String field, DbBeanInterface bean, DbAccess dbAccess) {
        return associationExists(tables, field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(Collection<String> tables, String field, DbBeanInterface bean, DbTransaction transaction) {
        return associationExists(tables, field, bean.getId(), transaction);
    }

    public static boolean associationExists(String field, long id, DbAccess dbAccess, String... tables) {
        return associationExists(Arrays.asList(tables), field, id, dbAccess);
    }

    public static boolean associationExists(String field, long id, DbTransaction transaction, String... tables) {
        return associationExists(Arrays.asList(tables), field, id, transaction);
    }

    public static boolean associationExists(String field, DbBeanInterface bean, DbAccess dbAccess, String... tables) {
        return associationExists(Arrays.asList(tables), field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(String field, DbBeanInterface bean, DbTransaction transaction, String... tables) {
        return associationExists(Arrays.asList(tables), field, bean.getId(), transaction);
    }

}
