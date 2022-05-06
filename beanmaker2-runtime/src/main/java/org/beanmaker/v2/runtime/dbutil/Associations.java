package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanInterface;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;

import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class Associations {

    private Associations() { }

    public static boolean hasItem(String pairingTable, String field, long idBean, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field + " FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                ResultSet::next
        );
    }

    public static boolean hasItem(String pairingTable, String field, long idBean, DBTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field + " FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                ResultSet::next
        );
    }

    public static int getItemCount(String pairingTable, String field, long idBean, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT COUNT(" + field + ") FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }
        );
    }

    public static int getItemCount(String pairingTable, String field, long idBean, DBTransaction transaction) {
        return transaction.addQuery(
                "SELECT COUNT(" + field + ") FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, long idBean1, long idBean2, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field1 + " FROM " + pairingTable + " WHERE " + field1 + "=? AND " + field2 + "=?",
                stat -> {
                    stat.setLong(1, idBean1);
                    stat.setLong(2, idBean2);
                },
                ResultSet::next
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, long idBean1, long idBean2, DBTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field1 + " FROM " + pairingTable + " WHERE " + field1 + "=? AND " + field2 + "=?",
                stat -> {
                    stat.setLong(1, idBean1);
                    stat.setLong(2, idBean2);
                },
                ResultSet::next
        );
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBAccess dbAccess) {
        return arePaired(pairingTable, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static boolean arePaired(String pairingTable, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBTransaction transaction) {
        return arePaired(pairingTable, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    private static String getAssociationQuery(String table, String field1, String field2) {
        return "REPLACE INTO " + table + " (" + field1 + ", " + field2 + ") VALUES (?, ?)";
    }

    public static void createAssociation(String table, String field1, String field2, long id1, long id2, DBAccess dbAccess) {
        dbAccess.processUpdate(
                getAssociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void createAssociation(String table, String field1, String field2, long id1, long id2, DBTransaction transaction) {
        transaction.addUpdate(
                getAssociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void createAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBAccess dbAccess) {
        createAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static void createAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBTransaction transaction) {
        createAssociation(table, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    private static String getDissociationQuery(String table, String field1, String field2) {
        return "DELETE FROM " + table + " WHERE " + field1 + "=? AND " + field2 + "=?";
    }

    public static void removeAssociation(String table, String field1, String field2, long id1, long id2, DBAccess dbAccess) {
        dbAccess.processUpdate(
                getDissociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void removeAssociation(String table, String field1, String field2, long id1, long id2, DBTransaction transaction) {
        transaction.addUpdate(
                getDissociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void removeAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBAccess dbAccess) {
        removeAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbAccess);
    }

    public static void removeAssociation(String table, String field1, String field2, DbBeanInterface bean1, DbBeanInterface bean2, DBTransaction transaction) {
        removeAssociation(table, field1, field2, bean1.getId(), bean2.getId(), transaction);
    }

    public static boolean associationExists(String table, String field, long id, DBAccess dbAccess) {
        return dbAccess.processQuery(
                "SELECT " + field + " FROM " + table + " WHERE " + field + "=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean associationExists(String table, String field, long id, DBTransaction transaction) {
        return transaction.addQuery(
                "SELECT " + field + " FROM " + table + " WHERE " + field + "=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean associationExists(String table, String field, DbBeanInterface bean, DBAccess dbAccess) {
        return associationExists(table, field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(String table, String field, DbBeanInterface bean, DBTransaction transaction) {
        return associationExists(table, field, bean.getId(), transaction);
    }

    public static <T extends DbBeanInterface, A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            T referencedBean,
            A returnedBean,
            DBAccess dbAccess)
    {
        return getAssociatedBean(table, referenceIdField, referencedBean.getId(), returnedBean, dbAccess);
    }

    public static <T extends DbBeanInterface, A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            T referencedBean,
            A returnedBean,
            DBTransaction transaction)
    {
        return getAssociatedBean(table, referenceIdField, referencedBean.getId(), returnedBean, transaction);
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            long idReferencedBean,
            A returnedBean,
            DBAccess dbAccess)
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
            DBTransaction transaction)
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
            DBQuerySetup querySetup,
            A returnedBean,
            DBAccess dbAccess)
    {
        return SingleElements.getBean(query, querySetup, returnedBean.getClass(), dbAccess);
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String query,
            DBQuerySetup querySetup,
            A returnedBean,
            DBTransaction transaction)
    {
        return SingleElements.getBean(query, querySetup, returnedBean.getClass(), transaction);
    }

    public static boolean associationExists(Collection<String> tables, String field, long id, DBAccess dbAccess) {
        for (String table: tables)
            if (associationExists(table, field, id, dbAccess))
                return true;

        return false;
    }

    public static boolean associationExists(Collection<String> tables, String field, long id, DBTransaction transaction) {
        for (String table: tables)
            if (associationExists(table, field, id, transaction))
                return true;

        return false;
    }

    public static boolean associationExists(Collection<String> tables, String field, DbBeanInterface bean, DBAccess dbAccess) {
        return associationExists(tables, field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(Collection<String> tables, String field, DbBeanInterface bean, DBTransaction transaction) {
        return associationExists(tables, field, bean.getId(), transaction);
    }

    public static boolean associationExists(String field, long id, DBAccess dbAccess, String... tables) {
        return associationExists(Arrays.asList(tables), field, id, dbAccess);
    }

    public static boolean associationExists(String field, long id, DBTransaction transaction, String... tables) {
        return associationExists(Arrays.asList(tables), field, id, transaction);
    }

    public static boolean associationExists(String field, DbBeanInterface bean, DBAccess dbAccess, String... tables) {
        return associationExists(Arrays.asList(tables), field, bean.getId(), dbAccess);
    }

    public static boolean associationExists(String field, DbBeanInterface bean, DBTransaction transaction, String... tables) {
        return associationExists(Arrays.asList(tables), field, bean.getId(), transaction);
    }

}
