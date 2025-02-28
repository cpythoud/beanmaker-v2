package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.dbbeans.sql.DB;
import org.dbbeans.sql.DBAccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LanguageHelperTest {

    private static final DB DB = new DB() {
        @Override
        public Connection getConnection() throws SQLException {
            throw new UnsupportedOperationException("Not supported. Test instance.");
        }
    };

    private static final DbBeanLanguage DBBEAN_LANGUAGE = new DbBeanLanguage() {
        @Override
        public String getName() {
            return "English";
        }

        @Override
        public String getIso() {
            return "en";
        }

        @Override
        public long getId() {
            return 1;
        }

        @Override
        public String getNameForIdNamePairsAndTitles(DbBeanLanguage language) {
            return "English";
        }
    };

    private DBAccess dbAccess;
    private LanguageHelper languageHelper;

    @BeforeAll
    void setUp() {
        dbAccess = new DBAccess(DB);
        languageHelper = LanguageHelper.builder().languageClass(DBBEAN_LANGUAGE.getClass()).dbAccess(dbAccess).build();
    }

    @Test
    void testInitialValues() {
        assertEquals("languages", languageHelper.getTable());
        assertEquals("iso", languageHelper.getIsoField());
        assertEquals("region", languageHelper.getRegionField());
        assertEquals("default_language", languageHelper.getDefaultLanguageField());
        assertEquals(DBBEAN_LANGUAGE.getClass(), languageHelper.getLanguageClass());
        assertEquals(dbAccess, languageHelper.getDbAccess());
    }

    @Test
    void testIsoRegionParsing() {
        var pair = LanguageHelper.getIsoRegionPair("en-US");
        assertEquals("en", pair.iso());
        assertEquals("US", pair.region());
        var pair2 = LanguageHelper.getIsoRegionPair("en");
        assertEquals("en", pair2.iso());
        assertNull(pair2.region());
    }

    @Test
    void testIsoRegionLanguageQuery() {
        assertEquals(
                "SELECT id FROM languages WHERE iso=? AND region=?",
                languageHelper.getLanguageTagQuery(new LanguageHelper.IsoRegionPair("en", "US"))
        );
        assertEquals(
                "SELECT id FROM languages WHERE iso=? AND region IS NULL",
                languageHelper.getLanguageTagQuery(new LanguageHelper.IsoRegionPair("en", null))
        );
    }

}
