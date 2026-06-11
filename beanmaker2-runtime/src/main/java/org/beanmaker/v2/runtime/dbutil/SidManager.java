package org.beanmaker.v2.runtime.dbutil;

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

    private static final SecureQuery.Builder EXISTING_SIDS_QUERY =
            SecureQuery.builder("SELECT sid FROM [TABLE_1] WHERE sid IS NOT NULL");
    private static final SecureQuery.Builder ADD_SID_UPDATE =
            SecureQuery.builder("UPDATE [TABLE_1] SET sid=? WHERE sid IS NULL");

    public static void createMissingSids(DbTransaction transaction, String table) {
        var existingSids = transaction.processQuery(
                EXISTING_SIDS_QUERY.table(1, table).build(),
                rs -> {
                    var sids = new HashSet<String>();
                    while (rs.next())
                        sids.add(rs.getString(1));
                    return sids;
                }
        );
        transaction.processUpdate(
                ADD_SID_UPDATE.table(1, table).build(),
                stat -> {
                    String sid = createSid();
                    while (existingSids.contains(sid))
                        sid = createSid();
                    stat.setString(1, sid);
                    existingSids.add(sid);
                }
        );
    }

    private static final SecureQuery.Builder RESET_SID_UPDATE = SecureQuery.builder("UPDATE [TABLE_1] SET sid=?");

    public static void resetAllSids(DbTransaction transaction, String table) {
        var newSids = new HashSet<String>();
        transaction.processUpdate(
                RESET_SID_UPDATE.table(1, table).build(),
                stat -> {
                    String sid = createSid();
                    while (newSids.contains(sid))
                        sid = createSid();
                    stat.setString(1, sid);
                    newSids.add(sid);
                }
        );
    }

}
