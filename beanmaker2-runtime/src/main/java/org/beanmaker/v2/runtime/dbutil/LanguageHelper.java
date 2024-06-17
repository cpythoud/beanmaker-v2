package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.dbbeans.sql.DBAccess;

import java.util.Optional;

public class LanguageHelper {
    private final String table;
    private final String isoField;
    private final String regionField;
    private final Class<? extends DbBeanLanguage> languageClass;
    private final DBAccess dbAccess;

    private LanguageHelper(
            String table,
            String isoField,
            String regionField,
            Class<? extends DbBeanLanguage> languageClass,
            DBAccess dbAccess)
    {
        this.table = table;
        this.isoField = isoField;
        this.regionField = regionField;
        this.languageClass = languageClass;
        this.dbAccess = dbAccess;
    }

    public static Builder builder() {
        return new Builder();
    }

    public <B extends DbBeanLanguage> Optional<B> fromTag(String tag) {
        var isoRegionPair = getIsoRegionPair(tag);
        if (isoRegionPair.region() == null)
            return SingleElements.getBean(
                    "SELECT id FROM %s WHERE %s=? AND %s IS NULL".formatted(table, isoField, regionField),
                    stat -> stat.setString(1, isoRegionPair.iso()),
                    languageClass,
                    dbAccess
            );

        return SingleElements.getBean(
                "SELECT id FROM %s WHERE %s=? AND %s=?".formatted(table, isoField, regionField),
                stat -> {
                    stat.setString(1, isoRegionPair.iso());
                    stat.setString(2, isoRegionPair.region());
                },
                languageClass,
                dbAccess
        );
    }

    public record IsoRegionPair(String iso, String region) { }

    public static IsoRegionPair getIsoRegionPair(String tag) {
        String[] parts = tag.split("-");
        if (parts.length == 1)
            return new IsoRegionPair(parts[0], null);
        if (parts.length == 2)
            return new IsoRegionPair(parts[0], parts[1]);

        throw new IllegalArgumentException("Invalid ISO tag: " + tag);
    }

    public DbBeanLanguage getBareLanguage(DbBeanLanguage language) {
        if (language.isBareLanguage())
            return language;

        var bareLanguage = fromTag(language.getIso()).orElse(null);
        if (bareLanguage == null)
            return language;
        return bareLanguage;
    }

    public static class Builder {
        private String table = "languages";
        private String isoField = "iso";
        private String regionField = "region";
        private Class<? extends DbBeanLanguage> languageClass;
        private DBAccess dbAccess;

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder isoField(String isoField) {
            this.isoField = isoField;
            return this;
        }

        public Builder regionField(String regionField) {
            this.regionField = regionField;
            return this;
        }

        public Builder languageClass(Class<? extends DbBeanLanguage> languageClass) {
            this.languageClass = languageClass;
            return this;
        }

        public Builder dbAccess(DBAccess dbAccess) {
            this.dbAccess = dbAccess;
            return this;
        }

        public LanguageHelper build() {
            if (table == null)
                throw new NullPointerException("table is null");
            if (isoField == null)
                throw new NullPointerException("isoField is null");
            if (regionField == null)
                throw new NullPointerException("regionField is null");
            if (languageClass == null)
                throw new NullPointerException("language class is null");
            if (dbAccess == null)
                throw new NullPointerException("dbAccess is null");

            return new LanguageHelper(table, isoField, regionField, languageClass, dbAccess);
        }
    }

}
