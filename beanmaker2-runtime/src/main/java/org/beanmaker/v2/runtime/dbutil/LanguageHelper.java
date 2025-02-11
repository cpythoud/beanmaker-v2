package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LanguageHelper {
    private final String table;
    private final String isoField;
    private final String regionField;
    private final String defaultLanguageField;
    private final Class<? extends DbBeanLanguage> languageClass;
    private final DBAccess dbAccess;

    private LanguageHelper(
            String table,
            String isoField,
            String regionField,
            String defaultLanguageField,
            Class<? extends DbBeanLanguage> languageClass,
            DBAccess dbAccess)
    {
        this.table = table;
        this.isoField = isoField;
        this.regionField = regionField;
        this.defaultLanguageField = defaultLanguageField;
        this.languageClass = languageClass;
        this.dbAccess = dbAccess;
    }

    public static Builder builder() {
        return new Builder();
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

    public static String getTag(String iso, String region) {
        if (Strings.isEmpty(region))
            return iso;
        return iso + "-" + region;
    }

    public static String getTag(Locale locale) {
        return getTag(locale.getLanguage(), locale.getCountry());
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

    public <B extends DbBeanLanguage> B getDefaultLanguage() {
        return SingleElements.<B>getBean(
                "SELECT id FROM %s WHERE %s=?".formatted(table, defaultLanguageField),
                stat -> stat.setBoolean(1, true),
                languageClass,
                dbAccess
        ).orElseThrow();
    }

    public <B extends DbBeanLanguage> B getWorkingLanguage(Enumeration<Locale> locales) {
        return getWorkingLanguage(Collections.list(locales));
    }

    @SuppressWarnings("unchecked")
    public <B extends DbBeanLanguage> B getWorkingLanguage(List<Locale> locales) {
        B language = null;
        for (var locale : locales) {
            if (language == null)
                language = (B) fromTag(getTag(locale)).orElse(null);
        }
        if (language == null) {
            for (var locale : locales) {
                if (language == null)
                    language = (B) fromTag(locale.getLanguage()).orElse(null);
            }
        }
        if (language == null)
            language = getDefaultLanguage();

        return language;
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
        private String defaultLanguageField = "default_language";
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

        public Builder defaultLanguageField(String defaultLanguageField) {
            this.defaultLanguageField = defaultLanguageField;
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
            if (defaultLanguageField == null)
                throw new NullPointerException("defaultLanguageField is null");
            if (dbAccess == null)
                throw new NullPointerException("dbAccess is null");

            return new LanguageHelper(table, isoField, regionField, defaultLanguageField, languageClass, dbAccess);
        }
    }

}
