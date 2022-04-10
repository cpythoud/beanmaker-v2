package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanEditor;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public class SingleElements {

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            String query,
            DBQuerySetup querySetup,
            E returnedEditor,
            DBAccess dbAccess)
    {
        return Optional.ofNullable(
                dbAccess.processQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs);
                        }
                )
        );
    }

    private static <E extends DbBeanEditor> E getSingleEditor(E returnedEditor, ResultSet rs) throws SQLException {
        long id = getSingleID(rs);
        if (id == 0)
            return null;
        
        returnedEditor.setId(id);
        return returnedEditor;
    }

    private static long getSingleID(ResultSet rs) throws SQLException {
        long id = 0;
        int count = 0;
        while (rs.next()) {
            id = rs.getLong(1);
            ++count;
        }

        if (count > 1)
            throw new IllegalStateException("Too many results: " + count);

        return id;
    }

    public static long getID(String query, DBQuerySetup querySetup, DBAccess dbAccess) {
        return dbAccess.processQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            String query,
            DBQuerySetup querySetup,
            E returnedEditor,
            DBTransaction transaction)
    {
        return Optional.ofNullable(
                transaction.addQuery(
                        query,
                        querySetup,
                        rs -> {
                            return getSingleEditor(returnedEditor, rs);
                        }
                )
        );
    }

    public static long getID(String query, DBQuerySetup querySetup, DBTransaction transaction) {
        return transaction.addQuery(
                query,
                querySetup,
                SingleElements::getSingleID
        );
    }

    private SingleElements() { }
    
}
