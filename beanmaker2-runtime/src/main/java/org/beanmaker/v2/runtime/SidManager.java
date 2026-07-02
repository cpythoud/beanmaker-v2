package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbTransaction;
import org.beanmaker.v2.database.sql.SecureQuery;

import org.beanmaker.v2.runtime.dbutil.SingleElements;

import org.beanmaker.v2.util.Strings;

import rodeo.password.pgencheck.CharacterGroups;
import rodeo.password.pgencheck.PasswordMaker;
import rodeo.password.pgencheck.RandomUIntGenerator;

import java.security.SecureRandom;

import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class SidManager {

    private static class SidUIntGenerator implements RandomUIntGenerator {

        private static final SecureRandom RANDOM = new SecureRandom();

        @Override
        public int getNextUInt(int max) {
            return random().nextInt(max);
        }

        @Override
        public Random random() {
            return RANDOM;
        }
    }

    private static final RandomUIntGenerator U_INT_GENERATOR = new SidUIntGenerator();

    private static final PasswordMaker SID_CREATOR =
            PasswordMaker.factory()
                    .addCharGroup(CharacterGroups.LOWER_CASE + CharacterGroups.UPPER_CASE, 1)
                    .addCharGroup(CharacterGroups.DIGITS)
                    .setLength(25)
                    .setRandomUIntGenerator(U_INT_GENERATOR)
                    .create();

    private SidManager() { }

    public static String createSid() {
        return SID_CREATOR.create();
    }

    public static String createUniqueSid(DbExecutor dbExecutor, String table) {
        String sid = createSid();
        while (sidExist(dbExecutor, table, sid))
            sid = createSid();
        return sid;
    }

    public static boolean sidExist(DbExecutor dbExecutor, String table, String sid) {
        return dbExecutor.processQuery(
                SecureQuery.builder("SELECT sid FROM [TABLE_1] WHERE sid=?").table(1, table).build(),
                stat -> stat.setString(1, sid),
                ResultSet::next
        );
    }

    public static long getId(DbExecutor dbExecutor, String idOrSid, DbBeanParameters parameters) {
        long id = Strings.getLongVal(idOrSid);
        if (id > 0) {
            if (parameters.requireSid())
                throw new SidRequiredException("SID required; numeric ID is not allowed: " + idOrSid);
            return id;
        }

        return SingleElements.getID(
                SecureQuery.builder("SELECT id FROM [TABLE_1] WHERE sid=?")
                        .table(1, parameters.getDatabaseTableName())
                        .build(),
                stat -> stat.setString(1, idOrSid),
                dbExecutor
        );
    }

    public static void createMissingSids(DbTransaction transaction, String table) {
        var ids = getIds(transaction, table, true);
        var existingSids = getExistingSids(transaction, table);
        createSids(transaction, table, ids, existingSids);
    }

    private static Set<Long> getIds(DbTransaction transaction, String table, boolean emptyOnly) {
        var query = SecureQuery.builder(
                "SELECT id FROM [TABLE_1]" + (emptyOnly ? " WHERE sid IS NULL OR TRIM(sid) = ''" : ""))
                .table(1, table)
                .build();

        return transaction.processQuery(
                query,
                rs -> {
                    var ids = new HashSet<Long>();
                    while (rs.next())
                        ids.add(rs.getLong(1));
                    return ids;
                }
        );
    }

    private static Set<String> getExistingSids(DbTransaction transaction, String table) {
        return transaction.processQuery(
                SecureQuery.builder("SELECT sid FROM [TABLE_1] WHERE sid IS NOT NULL").table(1, table).build(),
                rs -> {
                    var sids = new HashSet<String>();
                    while (rs.next())
                        sids.add(rs.getString(1));
                    return sids;
                }
        );
    }

    private static void createSids(DbTransaction transaction, String table, Set<Long> ids, Set<String> existingSids) {
        transaction.processUpdates(
                SecureQuery.builder("UPDATE [TABLE_1] SET sid=? WHERE id=?").table(1, table).build(),
                stat -> {
                    for (long id: ids) {
                        String sid = createSid();
                        while (existingSids.contains(sid))
                            sid = createSid();
                        stat.setString(1, sid);
                        stat.setLong(2, id);
                        stat.executeUpdate();
                        existingSids.add(sid);
                    }
                }
        );
    }

    public static void resetAllSids(DbTransaction transaction, String table) {
        var ids = getIds(transaction, table, false);
        var existingSids = getExistingSids(transaction, table);
        createSids(transaction, table, ids, existingSids);
    }

    public static String zeroOrSid(String sid) {
        if (Strings.isEmpty(sid))
            return "0";

        return sid;
    }

}
