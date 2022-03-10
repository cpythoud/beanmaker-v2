package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Dates;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQueryRetrieveData;
import org.dbbeans.sql.DBQuerySetup;

import rodeo.password.pgencheck.CharacterGroups;
import rodeo.password.pgencheck.PasswordMaker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProtectedIdManager {

    private static final PasswordMaker CODE_GENERATOR =
            PasswordMaker.factory()
                    .setLength(32)
                    .addCharGroup(CharacterGroups.UPPER_CASE)
                    .addCharGroup(CharacterGroups.LOWER_CASE)
                    .addCharGroup(CharacterGroups.DIGITS)
                    .create();

    private final DBAccess dbAccess;
    private final String table;

    public ProtectedIdManager(DBAccess dbAccess, String table) {
        this.dbAccess = dbAccess;
        this.table = table;
    }

    public String getCode(long id) {
        String code = getCodeFromDB(id);

        if (code == null)
            code = createCode(id);

        return code;
    }

    public long getId(String code) {
        return getIdFromDB(code);
    }

    public boolean codeMatchesId(String code, long id) {
        return getId(code) == id;
    }

    private String getCodeFromDB(long id) {
        return dbAccess.processQuery(
                "SELECT code FROM " + table + " WHERE protected_id=?",
                new DBQuerySetup() {
                    @Override
                    public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                        stat.setLong(1, id);
                    }
                },
                new DBQueryRetrieveData<String>() {
                    @Override
                    public String processResultSet(ResultSet rs) throws SQLException {
                        if (rs.next())
                            return rs.getString(1);

                        return null;
                    }
                }
        );
    }

    private String createCode(long id) {
        String code = CODE_GENERATOR.create();

        if (!exists(code))
            dbAccess.processUpdate(
                    "INSERT INTO " + table + " (protected_id, code, creation_date) VALUES (?, ?, ?)",
                    new DBQuerySetup() {
                        @Override
                        public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                            stat.setLong(1, id);
                            stat.setString(2, code);
                            stat.setTimestamp(3, Dates.getCurrentTimestamp());
                        }
                    }
            );

        return code;
    }

    private boolean exists(String code) {
        return dbAccess.processQuery(
                "SELECT protected_id FROM " + table + " WHERE code=?",
                new DBQuerySetup() {
                    @Override
                    public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                        stat.setString(1, code);
                    }
                },
                new DBQueryRetrieveData<Boolean>() {
                    @Override
                    public Boolean processResultSet(ResultSet rs) throws SQLException {
                        return rs.next();
                    }
                }
        );
    }

    private long getIdFromDB(String code) {
        return dbAccess.processQuery(
                "SELECT protected_id FROM " + table + " WHERE code=?",
                new DBQuerySetup() {
                    @Override
                    public void setupPreparedStatement(PreparedStatement stat) throws SQLException {
                        stat.setString(1, code);
                    }
                },
                new DBQueryRetrieveData<Long>() {
                    @Override
                    public Long processResultSet(ResultSet rs) throws SQLException {
                        if (rs.next())
                            return rs.getLong(1);

                        return 0L;
                    }
                }
        );
    }

}
