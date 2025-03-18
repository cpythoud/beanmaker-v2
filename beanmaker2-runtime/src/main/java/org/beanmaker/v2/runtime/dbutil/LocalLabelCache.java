package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.dbbeans.sql.DBAccess;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LocalLabelCache {

    private final Map<Long, String> cache = new HashMap<>();

    public static LocalLabelCache createSimpleCache(String tableName, DbBeanLanguage language, DBAccess dbAccess) {
        return builder(tableName, language, dbAccess).build();
    }

    public static LocalLabelCache createSmartCache(
            String tableName,
            DbBeanLanguage language,
            DbBeanLanguage defaultLanguage,
            DBAccess dbAccess)
    {
        return builder(tableName, language, dbAccess).smartCache(defaultLanguage).build();
    }

    public static CacheBuilder builder(String tableName, DbBeanLanguage language, DBAccess dbAccess) {
        return new CacheBuilder(tableName, language, dbAccess);
    }

    public static class CacheBuilder {
        private String labelDataTable = "label_data";
        private String labelDataTableDataField = "`data`";
        private String labelDataTableLabelField = "id_label";
        private String labelDataTableLanguageField = "id_language";
        private String labelField = "id_label";
        private final String tableName;
        private final DbBeanLanguage language;
        private final DBAccess dbAccess;
        private DbBeanLanguage defaultLanguage = null;

        private CacheBuilder(String tableName, DbBeanLanguage language, DBAccess dbAccess) {
            this.tableName = tableName;
            this.language = language;
            this.dbAccess = dbAccess;
        }

        public CacheBuilder labelDataTable(String labelDataTable) {
            this.labelDataTable = labelDataTable;
            return this;
        }

        public CacheBuilder labelDataTableDataField(String labelDataTableDataField) {
            this.labelDataTableDataField = labelDataTableDataField;
            return this;
        }

        public CacheBuilder labelDataTableLabelField(String labelDataTableLabelField) {
            this.labelDataTableLabelField = labelDataTableLabelField;
            return this;
        }

        public CacheBuilder labelDataTableLanguageField(String labelDataTableLanguageField) {
            this.labelDataTableLanguageField = labelDataTableLanguageField;
            return this;
        }

        public CacheBuilder labelField(String labelField) {
            this.labelField = labelField;
            return this;
        }

        public CacheBuilder smartCache(DbBeanLanguage defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
            return this;
        }

        // * SELECT table.id, label_data.data FROM table
        // * INNER JOIN label_data ON label_data.id_label=id_xxx_label
        // * WHERE label_data.id_language=?
        private static final String QUERY_TEMPLATE =
                "SELECT %s.id, %s.%s FROM %s INNER JOIN %s ON %s.%s=%s WHERE %s.%s=?";

        private String composeQuery() {
            return QUERY_TEMPLATE.formatted(
                    tableName,
                    labelDataTable,
                    labelDataTableDataField,
                    tableName,
                    labelDataTable,
                    labelDataTable,
                    labelDataTableLabelField,
                    labelField,
                    labelDataTable,
                    labelDataTableLanguageField
            );
        }

        public LocalLabelCache build() {
            return new LocalLabelCache(composeQuery(), language, dbAccess, defaultLanguage);
        }
    }

    private LocalLabelCache(String query, DbBeanLanguage language, DBAccess dbAccess, DbBeanLanguage defaultLanguage) {
        System.out.println(query);
        populateCache(query, language, dbAccess, defaultLanguage);
    }

    private void populateCache(
            String query,
            DbBeanLanguage language,
            DBAccess dbAccess,
            DbBeanLanguage defaultLanguage)
    {
        var languages = new LinkedHashSet<DbBeanLanguage>();
        languages.add(language);
        if (defaultLanguage != null) {
            languages.add(language.getBareLanguage());
            languages.add(defaultLanguage);
        }

        for (var actualLanguage : languages) {
            dbAccess.processQuery(
                    query,
                    stat -> stat.setLong(1, actualLanguage.getId()),
                    rs -> {
                        while (rs.next())
                            cache.putIfAbsent(rs.getLong(1), rs.getString(2));
                    }
            );
        }
    }

    // ! L'ID est l'ID de l'élément dans la table, pas l'ID du label !
    public String getLabel(long id) {
        return cache.get(id);
    }

}
