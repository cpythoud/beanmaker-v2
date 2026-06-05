package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbQuerySetup;

import org.beanmaker.v2.runtime.DbBeanInterface;

import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class Associations {

    private Associations() { }

    public static boolean hasItem(String pairingTable, String field, long idBean, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                "SELECT " + field + " FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                ResultSet::next
        );
    }

    public static int getItemCount(String pairingTable, String field, long idBean, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                "SELECT COUNT(" + field + ") FROM " + pairingTable + " WHERE " + field + "=?",
                stat -> stat.setLong(1, idBean),
                rs -> {
                    rs.next();
                    return rs.getInt(1);
                }
        );
    }

    public static boolean arePaired(
            String pairingTable,
            String field1,
            String field2,
            long idBean1,
            long idBean2,
            DbExecutor dbExecutor)
    {
        return dbExecutor.processQuery(
                "SELECT " + field1 + " FROM " + pairingTable + " WHERE " + field1 + "=? AND " + field2 + "=?",
                stat -> {
                    stat.setLong(1, idBean1);
                    stat.setLong(2, idBean2);
                },
                ResultSet::next
        );
    }

    public static boolean arePaired(
            String pairingTable,
            String field1,
            String field2,
            DbBeanInterface bean1,
            DbBeanInterface bean2,
            DbExecutor dbExecutor)
    {
        return arePaired(pairingTable, field1, field2, bean1.getId(), bean2.getId(), dbExecutor);
    }

    private static String getAssociationQuery(String table, String field1, String field2) {
        return "REPLACE INTO " + table + " (" + field1 + ", " + field2 + ") VALUES (?, ?)";
    }

    public static void createAssociation(
            String table,
            String field1,
            String field2,
            long id1,
            long id2,
            DbExecutor dbExecutor)
    {
        dbExecutor.processUpdate(
                getAssociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void createAssociation(
            String table,
            String field1,
            String field2,
            DbBeanInterface bean1,
            DbBeanInterface bean2,
            DbExecutor dbExecutor)
    {
        createAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbExecutor);
    }

    private static String getDissociationQuery(String table, String field1, String field2) {
        return "DELETE FROM " + table + " WHERE " + field1 + "=? AND " + field2 + "=?";
    }

    public static void removeAssociation(
            String table,
            String field1,
            String field2,
            long id1,
            long id2,
            DbExecutor dbExecutor)
    {
        dbExecutor.processUpdate(
                getDissociationQuery(table, field1, field2),
                stat -> {
                    stat.setLong(1, id1);
                    stat.setLong(2, id2);
                }
        );
    }

    public static void removeAssociation(
            String table,
            String field1,
            String field2,
            DbBeanInterface bean1,
            DbBeanInterface bean2,
            DbExecutor dbExecutor)
    {
        removeAssociation(table, field1, field2, bean1.getId(), bean2.getId(), dbExecutor);
    }

    public static boolean associationExists(String table, String field, long id, DbExecutor dbExecutor) {
        return dbExecutor.processQuery(
                "SELECT " + field + " FROM " + table + " WHERE " + field + "=?",
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public static boolean associationExists(String table, String field, DbBeanInterface bean, DbExecutor dbExecutor) {
        return associationExists(table, field, bean.getId(), dbExecutor);
    }

    public static <T extends DbBeanInterface, A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            T referencedBean,
            A returnedBean,
            DbExecutor dbExecutor)
    {
        return getAssociatedBean(table, referenceIdField, referencedBean.getId(), returnedBean, dbExecutor);
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String table,
            String referenceIdField,
            long idReferencedBean,
            A returnedBean,
            DbExecutor dbExecutor)
    {
        return getAssociatedBean(
                "SELECT id FROM " + table + " WHERE " + referenceIdField + "=?",
                stat -> stat.setLong(1, idReferencedBean),
                returnedBean,
                dbExecutor
        );
    }

    public static <A extends DbBeanInterface> Optional<A> getAssociatedBean(
            String query,
            DbQuerySetup querySetup,
            A returnedBean,
            DbExecutor dbExecutor)
    {
        return SingleElements.getBean(query, querySetup, returnedBean.getClass(), dbExecutor);
    }

    public static boolean associationExists(Collection<String> tables, String field, long id, DbExecutor dbExecutor) {
        for (String table: tables)
            if (associationExists(table, field, id, dbExecutor))
                return true;

        return false;
    }

    public static boolean associationExists(
            Collection<String> tables,
            String field,
            DbBeanInterface bean,
            DbExecutor dbExecutor)
    {
        return associationExists(tables, field, bean.getId(), dbExecutor);
    }

    public static boolean associationExists(String field, long id, DbExecutor dbExecutor, String... tables) {
        return associationExists(Arrays.asList(tables), field, id, dbExecutor);
    }

    public static boolean associationExists(String field, DbBeanInterface bean, DbExecutor dbExecutor, String... tables) {
        return associationExists(Arrays.asList(tables), field, bean.getId(), dbExecutor);
    }

}
