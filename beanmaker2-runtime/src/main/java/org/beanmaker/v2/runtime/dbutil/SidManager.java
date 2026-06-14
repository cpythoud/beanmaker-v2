package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbTransaction;
import org.beanmaker.v2.database.sql.SecureQuery;

import org.beanmaker.v2.util.SecurityTokenGenerator;

import java.util.HashSet;

public final class SidManager {

    private SidManager() { }

    public static String createSid() {
        String token = SecurityTokenGenerator.create();
        while (token.contains("_"))
            token = SecurityTokenGenerator.create();
        return token;
    }

    public static void createMissingSids(DbTransaction transaction, String table) {
        var existingSids = transaction.processQuery(
                SecureQuery.builder("SELECT sid FROM [TABLE_1] WHERE sid IS NOT NULL").table(1, table).build(),
                rs -> {
                    var sids = new HashSet<String>();
                    while (rs.next())
                        sids.add(rs.getString(1));
                    return sids;
                }
        );
        transaction.processUpdate(
                SecureQuery.builder("UPDATE [TABLE_1] SET sid=? WHERE sid IS NULL").table(1, table).build(),
                stat -> {
                    String sid = createSid();
                    while (existingSids.contains(sid))
                        sid = createSid();
                    stat.setString(1, sid);
                    existingSids.add(sid);
                }
        );
    }

    public static void resetAllSids(DbTransaction transaction, String table) {
        var newSids = new HashSet<String>();
        transaction.processUpdate(
                SecureQuery.builder("UPDATE [TABLE_1] SET sid=?").table(1, table).build(),
                stat -> {
                    String sid = createSid();
                    while (newSids.contains(sid))
                        sid = createSid();
                    stat.setString(1, sid);
                    newSids.add(sid);
                }
        );
    }

    public static long getId(DbExecutor dbExecutor, String table, String sid) {
        var query = SecureQuery.builder("SELECT id FROM [TABLE_1] WHERE sid=?").table(1, table).build();
        return SingleElements.getID(
                query,
                stat -> stat.setString(1, sid),
                dbExecutor
        );
    }

}
