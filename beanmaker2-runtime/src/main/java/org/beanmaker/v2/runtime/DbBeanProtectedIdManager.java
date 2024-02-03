package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Dates;

import org.dbbeans.sql.DBAccess;

import rodeo.password.pgencheck.CharacterGroups;
import rodeo.password.pgencheck.PasswordMaker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.sql.ResultSet;

public class DbBeanProtectedIdManager<B extends DbBeanWithProtectedIdInterface> {

    private static final PasswordMaker CODE_GENERATOR =
            PasswordMaker.factory()
                    .setLength(32)
                    .addCharGroup(CharacterGroups.UPPER_CASE)
                    .addCharGroup(CharacterGroups.LOWER_CASE)
                    .addCharGroup(CharacterGroups.DIGITS)
                    .create();

    private final DBAccess dbAccess;
    private final String table;
    private final MethodHandle beanConstructorHandle;

    public DbBeanProtectedIdManager(DBAccess dbAccess, String table, Class<B> beanClass) {
        this.dbAccess = dbAccess;
        this.table = table;

        var lookup = MethodHandles.lookup();
        var constructor = MethodType.methodType(void.class, long.class);
        try {
            beanConstructorHandle = lookup.findConstructor(beanClass, constructor);
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getCode(B bean) {
        return getCode(bean.getId());
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

    public boolean matchesProtectionCode(B bean, String code) {
        return codeMatchesId(code, bean.getId());
    }

    public boolean codeMatchesId(String code, long id) {
        return getId(code) == id;
    }

    public B initFromCode(String code) {
        try {
            return (B) beanConstructorHandle.invokeWithArguments(getId(code));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String getCodeFromDB(long id) {
        return dbAccess.processQuery(
                "SELECT code FROM " + table + " WHERE protected_id=?",
                stat -> stat.setLong(1, id),
                rs -> {
                    if (rs.next())
                        return rs.getString(1);

                    return null;
                }
        );
    }

    private String createCode(long id) {
        String code = CODE_GENERATOR.create();

        if (!exists(code))
            dbAccess.processUpdate(
                    "INSERT INTO " + table + " (protected_id, code, creation_date) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, id);
                        stat.setString(2, code);
                        stat.setTimestamp(3, Dates.getCurrentTimestamp());
                    }
            );

        return code;
    }

    private boolean exists(String code) {
        return dbAccess.processQuery(
                "SELECT protected_id FROM " + table + " WHERE code=?",
                stat -> stat.setString(1, code),
                ResultSet::next
        );
    }

    private long getIdFromDB(String code) {
        return dbAccess.processQuery(
                "SELECT protected_id FROM " + table + " WHERE code=?",
                stat -> stat.setString(1, code),
                rs -> {
                    if (rs.next())
                        return rs.getLong(1);

                    return 0L;
                }
        );
    }

}
