package org.beanmaker.v2.database.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SecureQuery {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\[(TABLE|FIELD|FIELD_LIST)_(\\d+)]");

    private final String query;
    private final Map<Integer, String> tables;
    private final Map<Integer, String> fields;
    private final Map<Integer, List<String>> fieldLists;

    private SecureQuery(
            String query,
            Map<Integer, String> tables,
            Map<Integer, String> fields,
            Map<Integer, List<String>> fieldLists)
    {
        this.query = query;
        this.tables = Map.copyOf(tables);
        this.fields = Map.copyOf(fields);
        this.fieldLists = copyFieldLists(fieldLists);
    }

    private static Map<Integer, List<String>> copyFieldLists(Map<Integer, List<String>> fieldLists) {
        Map<Integer, List<String>> copy = new HashMap<>();
        for (var entry : fieldLists.entrySet()) {
            copy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(copy);
    }

    public static Builder builder(String query) {
        return new Builder(query);
    }

    public static class Builder {
        private final String query;
        private final Map<Integer, String> tables = new HashMap<>();
        private final Map<Integer, String> fields = new HashMap<>();
        private final Map<Integer, List<String>> fieldLists = new HashMap<>();

        private Builder(String query) {
            this.query = Objects.requireNonNull(query);
        }

        public Builder table(int index, String table) {
            if (index < 0)
                throw new IllegalArgumentException("Index cannot be negative");

            tables.put(index, Objects.requireNonNull(table));
            return this;
        }

        public Builder field(int index, String field) {
            if (index < 0)
                throw new IllegalArgumentException("Index cannot be negative");

            fields.put(index, Objects.requireNonNull(field));
            return this;
        }

        public Builder fieldList(int index, String... fields) {
            if (index < 0)
                throw new IllegalArgumentException("Index cannot be negative");
            Objects.requireNonNull(fields);
            if (fields.length == 0)
                throw new IllegalArgumentException("Field list cannot be empty");

            fieldLists.put(index, List.of(fields));
            return this;
        }

        public Builder fieldList(int index, Collection<String> fields) {
            if (index < 0)
                throw new IllegalArgumentException("Index cannot be negative");
            Objects.requireNonNull(fields);
            if (fields.isEmpty())
                throw new IllegalArgumentException("Field list cannot be empty");

            fieldLists.put(index, List.copyOf(fields));
            return this;
        }

        public SecureQuery build() {
            return new SecureQuery(query, tables, fields, fieldLists);
        }

    }

    public String parse(DbType dbType) {
        var matcher = PLACEHOLDER_PATTERN.matcher(query);
        var parsedQuery = new StringBuilder();
        var policy = dbType.getSqlIdentifierPolicy();

        while (matcher.find()) {
            String type = matcher.group(1);
            int index = Integer.parseInt(matcher.group(2));

            String replacement = switch (type) {
                case "TABLE" -> policy.table(getRequiredValue(tables, index, "table"));
                case "FIELD" -> policy.column(getRequiredValue(fields, index, "field"));
                case "FIELD_LIST" -> getRequiredValue(fieldLists, index, "field list")
                        .stream()
                        .map(policy::column)
                        .collect(Collectors.joining(", "));
                default -> throw new IllegalArgumentException("Unsupported placeholder type: " + type);
            };

            matcher.appendReplacement(parsedQuery, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(parsedQuery);
        return parsedQuery.toString();
    }

    private static <T> T getRequiredValue(Map<Integer, T> values, int index, String type) {
        T value = values.get(index);
        if (value == null)
            throw new IllegalArgumentException("No " + type + " defined for index: " + index);

        return value;
    }

}
