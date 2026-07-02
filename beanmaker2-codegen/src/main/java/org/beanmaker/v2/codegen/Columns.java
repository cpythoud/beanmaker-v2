package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.English;
import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Columns implements Iterable<Column> {

    private static final List<String> NAMING_CANDIDATE_FIELDS =
            List.of("name", "id_name_label", "id_label", "description", "id_description_label", "code");

    private final DatabaseServer server;
    private final String db;
    private final String table;
    private final ReservedDatabaseFieldManager fieldManager;

    private final List<Column> columns;
    private final List<OneToManyRelationship> detectedOneToManyRelationships;
    private final List<OneToManyRelationship> oneToManyRelationships;
    private final List<ExtraField> extraFields = new ArrayList<>();

    public Columns(DatabaseServer server, String db, String table) {
        this(server, db, table, ReservedDatabaseFieldManager.DEFAULT);
    }

    public Columns(DatabaseServer server, String db, String table, ReservedDatabaseFieldManager fieldManager) {
        this.server = server;
        this.db = db;
        this.table = table;
        this.fieldManager = fieldManager;
        columns = server.getColumns(db, table);
        // ! For now, we deactivate detection of possible one-to-many relationship
        // ! because the feature is a nuisance on large table sets and should probably be abandoned
        // ? To be reintroduced in ProjectParameters ?
        /*detectedOneToManyRelationships = server.getDetectedOneToManyRelationship(db, table);
        oneToManyRelationships = new ArrayList<>(detectedOneToManyRelationships);*/
        detectedOneToManyRelationships = List.of();
        oneToManyRelationships = new ArrayList<>();
    }

    public DatabaseServer getServer() {
        return server;
    }

    public String getDb() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public List<Column> getList() {
        return List.copyOf(columns);
    }

    public int getCount() {
        return columns.size();
    }

    public Column getColumn(int index) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        return new Column(columns.get(index - 1));
    }

    public Optional<Column> getColumn(String dbFieldName) {
        for (Column column: getList())
            if (column.getSqlName().equals(dbFieldName))
                return Optional.of(column);

        return Optional.empty();
    }

    public void setJavaName(int index, String name) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).setJavaName(name);
    }

    public void setJavaType(int index, String type) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).setJavaType(type);
    }

    public void setRequired(int index, boolean required) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).setRequired(required);
    }

    @Deprecated
    public void resetRequired() {
        for (Column column: columns)
            if (!column.isSpecial())
                column.setRequired(false);
    }

    public void setUnique(int index, boolean unique) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).setUnique(unique);
    }

    @Deprecated
    public void resetUnique() {
        for (Column column: columns)
            if (!column.isSpecial())
                column.setUnique(false);
    }

    public void setAssociatedBeanClass(int index, String associatedBeanClass) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        if (columns.get(index - 1).couldHaveAssociatedBean())
            columns.get(index - 1).setAssociatedBeanClass(associatedBeanClass);
        else
            throw new IllegalArgumentException("Column #" + index + " cannot have an associated bean.");
    }

    public void setDecimals(int index, int decimals) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).setDecimals(decimals);
    }

    public void canBeNegative(int index, boolean negative) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        columns.get(index - 1).canBeNegative(negative);
    }

    public void setItemOrderAssociatedField(int index, String itemOrderAssociatedField) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        if (columns.get(index - 1).isItemOrder())
            columns.get(index - 1).setItemOrderAssociatedField(itemOrderAssociatedField);
        else
            throw new IllegalArgumentException("Column #" + index + " is not an item order field.");
    }

    public void setItemOrderAssociatedField(String itemOrderAssociatedField) {
        for (var column: columns)
            if (column.isItemOrder()) {
                column.setItemOrderAssociatedField(itemOrderAssociatedField);
                return;
            }

        throw new IllegalArgumentException("Columns do not contain an item order field.");
    }

    @Deprecated
    public boolean hasBadField() {
        return !checkForIncompatibleTypes().isEmpty();
    }

    public boolean hasId() {
        return columns.stream().anyMatch(Column::isId);
    }

    public boolean hasLastUpdate() {
        return columns.stream().anyMatch(Column::isLastUpdate);
    }

    public boolean hasModifiedBy() {
        return columns.stream().anyMatch(Column::isModifiedBy);
    }

    public boolean hasItemOrder() {
        return columns.stream().anyMatch(Column::isItemOrder);
    }

    public Optional<Column> getItemOrderColumn() {
        for (Column column: columns) {
            if (column.isItemOrder())
                return Optional.of(column);
        }

        return Optional.empty();
    }

    @Deprecated
    public boolean hasDuplicatedSpecialField() {
        return !checkDuplicatedSpecialFields().isEmpty();
    }

    public boolean hasLabels() {
        return columns.stream().anyMatch(Column::isLabelReference);
    }

    public boolean hasLabelField() {
        return hasJavaField("idLabel");
    }

    public boolean hasFiles() {
        return columns.stream().anyMatch(Column::isFileReference);
    }

    public boolean hasFileField() {
        return hasJavaField("idFile");
    }

    public boolean hasOtherBeanReference() {
        return columns.stream().anyMatch(Column::isOtherBeanReference);
    }

    public boolean hasDecimalValue() {
        return columns.stream().anyMatch(Column::isDecimalValue);
    }

    @Deprecated
    public boolean isOK() {
        return !getFormatErrors().isEmpty();
    }

    public Set<String> getJavaTypes() {
        var types = new HashSet<String>();

        for (Column column: columns)
            types.add(column.getJavaType());

        return types;
    }

    @Deprecated
    public boolean containsNumericalData() {
        for (Column column: columns)
            if (column.getJavaType().equals("Integer") || column.getJavaType().equals("Long"))
                if (!column.isSpecial() && !column.getJavaName().startsWith("id"))
                    return true;

        return false;
    }

    @Deprecated
    public boolean containsFinancialData() {
        for (Column column: columns)
            if (column.getJavaType().equals("Money"))
                return true;

        return false;
    }

    public Set<String> getSqlTypes() {
        Set<String> types = new HashSet<>();

        for (Column column: columns)
            types.add(column.getSqlTypeName());

        return types;
    }

    public List<String> getJavaFieldNames() {
        List<String> names = new ArrayList<>();

        for (Column column: columns)
            names.add(column.getJavaName());

        return names;
    }

    public void addOneToManyRelationship(OneToManyRelationship rel) {
        if (getJavaFieldNames().contains(rel.getJavaName()))
            throw new IllegalArgumentException("The bean already contains a field named " + rel.getJavaName());
        if (!server.getTables(db).contains(rel.getTable()))
            throw new IllegalArgumentException("Database " + db + " doesn't contain a table named " + rel.getTable());

        oneToManyRelationships.add(rel);
    }

    public void changeOneToManyRelationship(int index, OneToManyRelationship rel) {
        if (index < 0 || index > oneToManyRelationships.size())
            throw new IndexOutOfBoundsException("Bounds : 0-" + (oneToManyRelationships.size() - 1) + ", index : " + index);
        if (getJavaFieldNames().contains(rel.getJavaName()))
            throw new IllegalArgumentException("The bean already contains a field named " + rel.getJavaName());
        if (!server.getTables(db).contains(rel.getTable()))
            throw new IllegalArgumentException("Database " + db + " doesn't contain a table named " + rel.getTable());

        oneToManyRelationships.set(index, rel);
    }

    public void removeOneToManyRelationship(int index) {
        if (index < 0 || index > oneToManyRelationships.size())
            throw new IndexOutOfBoundsException("Bounds : 0-" + (oneToManyRelationships.size() - 1) + ", index : " + index);

        oneToManyRelationships.remove(index);
    }

    public void clearOneToManyRelationships() {
        oneToManyRelationships.clear();
    }

    public List<OneToManyRelationship> getOneToManyRelationships() {
        return Collections.unmodifiableList(oneToManyRelationships);
    }

    public List<OneToManyRelationship> getDetectedOneToManyRelationships() {
        return Collections.unmodifiableList(detectedOneToManyRelationships);
    }

    public Set<String> getOneToManyRelationshipTableNames() {
        return getOneToManyRelationshipTableNames(oneToManyRelationships);
    }

    public Set<String> getDetectedOneToManyRelationshipTableNames() {
        return getOneToManyRelationshipTableNames(detectedOneToManyRelationships);
    }

    private Set<String> getOneToManyRelationshipTableNames(List<OneToManyRelationship> relationships) {
        var tableNames = new HashSet<String>();
        for (OneToManyRelationship relationship: relationships)
            tableNames.add(relationship.getTable());
        return tableNames;
    }

    public boolean hasOneToManyRelationships() {
        return !oneToManyRelationships.isEmpty();
    }

    public String getNamingField() {
        for (String candidate: NAMING_CANDIDATE_FIELDS)
            for (Column col: columns)
                if (col.getSqlName().equalsIgnoreCase(candidate))
                    return candidate;

        return fieldManager.fieldName(ReservedDatabaseField.ID);
    }

    public List<String> getOrderByFields() {
        var list = new ArrayList<String>();

        if (hasItemOrder()) {
            var itemOrder = getItemOrderField();
            if (!itemOrder.isUnique())
                list.add(itemOrder.getItemOrderAssociatedField());
            list.add(fieldManager.fieldName(ReservedDatabaseField.ITEM_ORDER));
        } else {
            var candidateList = List.of(fieldManager.fieldName(ReservedDatabaseField.ITEM_ORDER),
                    "name", "description", "code");
            for (String candidate: candidateList) {
                for (Column col: columns)
                    if (col.getSqlName().equalsIgnoreCase(candidate)) {
                        list.add(candidate);
                        return list;
                    }
            }
        }

        if (list.isEmpty())
            list.add(fieldManager.fieldName(ReservedDatabaseField.ID));
        return list;
    }

    public Column getItemOrderField() {
        for (Column column: columns)
            if (column.isItemOrder())
                return new Column(column);

        throw new IllegalArgumentException("Column set does not contain an item order field.");
    }

    public List<ExtraField> getExtraFields() {
        return Collections.unmodifiableList(extraFields);
    }

    public void addExtraField(ExtraField extraField) {
        if (isAlreadyPresent(extraField))
            throw new IllegalArgumentException("An extra field with name " + extraField.getName() + " already exists.");

        extraFields.add(extraField);
    }

    private boolean isAlreadyPresent(ExtraField extraField) {
        for (ExtraField ef: extraFields)
            if (ef.getName().equals(extraField.getName()))
                return true;

        return false;
    }

    @Deprecated  // * typo in function name
    public void removeExtrafield(String name) {
        removeExtraField(name);
    }

    public void removeExtraField(String name) {
        int index = getExtraFieldIndex(name);
        if (index > -1)
            extraFields.remove(index);
        else
            throw new IllegalArgumentException("No extra field with name " + name);
    }

    private int getExtraFieldIndex(String name) {
        int index = 0;
        for (ExtraField ef: extraFields) {
            ++index;
            if (ef.getName().equals(name))
                return index;
        }

        return -1;
    }

    public void removeExtraField(ExtraField extraField) {
        removeExtraField(extraField.getName());
    }

    public void removeExtraField(int index) {
        if (index < 0 || index > extraFields.size())
            throw new IndexOutOfBoundsException("Bounds : 0-" + (extraFields.size() - 1) + ", index : " + index);

        extraFields.remove(index);
    }

    public boolean hasExtraFields() {
        return !extraFields.isEmpty();
    }

    public String getSuggestedBeanName() {
        return getSuggestedBeanName(table);
    }

    public static String getSuggestedBeanName(String table) {
        return Strings.camelize(English.singularize(table));
    }

    public boolean hasCodeField() {
        return hasSQLField("code");
    }

    public boolean hasUniqueCodeField() {
        return columns.stream().anyMatch(column -> column.getJavaName().equals("code") && column.isUnique());
    }

    public boolean hasVersionedCodeField() {
        return isVersioned() && hasJavaField("code");
    }

    public boolean hasActiveField() {
        return columns.stream()
                .anyMatch(column -> column.getJavaName().equals("active") && column.getJavaType().equals("Boolean"));
    }

    public List<Column> getLabels() {
        return columns.stream().filter(Column::isLabelReference).toList();
    }

    public List<Column> getDecimalValues() {
        return columns.stream().filter(Column::isDecimalValue).toList();
    }

    public boolean hasSQLField(String sqlField) {
        return columns.stream().anyMatch(column -> column.getSqlName().equals(sqlField));
    }

    public boolean hasJavaField(String javaField) {
        return columns.stream().anyMatch(column -> column.getJavaName().equals(javaField));
    }

    @Override
    public Iterator<Column> iterator() {
        return columns.iterator();
    }

    public boolean isVersioned() {
        var status = getVersionedStatus();
        return status.versionField && status.originalBeanIdField;
    }

    private record VersionedStatus(boolean versionField, boolean originalBeanIdField) { }

    private VersionedStatus getVersionedStatus() {
        boolean hasVersionField = false;
        boolean hasOriginalBeanIdField = false;

        for (Column column: columns) {
            if (column.isVersionField())
                hasVersionField = true;
            if (column.isOriginalBeanId())
                hasOriginalBeanIdField = true;
        }

        return new VersionedStatus(hasVersionField, hasOriginalBeanIdField);
    }

    public List<FieldFormatError.FieldAssociatedError> getFormatErrors() {
        var errors = new ArrayList<FieldFormatError.FieldAssociatedError>();

        errors.addAll(checkIdPresent());
        errors.addAll(checkForIncompatibleTypes());
        errors.addAll(checkDuplicatedSpecialFields());
        errors.addAll(checkVersioningFields());

        return errors;
    }

    private List<FieldFormatError.FieldAssociatedError> checkIdPresent() {
        if (!hasId())
            return List.of(
                    FieldFormatError.MISSING_ID.associateField(fieldManager.fieldName(ReservedDatabaseField.ID))
            );

        return List.of();
    }

    private List<FieldFormatError.FieldAssociatedError> checkForIncompatibleTypes() {
        var errors = new ArrayList<FieldFormatError.FieldAssociatedError>();
        for (Column column: columns) {
            if (column.incompatibleSqlType())
                errors.add(FieldFormatError.BAD_SQL_TYPE.associateField(column.getSqlName()));
        }

        return errors;
    }

    private List<FieldFormatError.FieldAssociatedError> checkDuplicatedSpecialFields() {
        var errors = new ArrayList<FieldFormatError.FieldAssociatedError>();
        for (String field: fieldManager.allFieldNames()) {
            int count = 0;
            for (Column column: columns) {
                if (column.getSqlName().equals(field))
                    ++count;
            }
            if (count > 1)
                errors.add(FieldFormatError.DUPLICATE_SPECIAL_FIELD.associateField(field));
        }

        return errors;
    }

    private List<FieldFormatError.FieldAssociatedError> checkVersioningFields() {
        var status = getVersionedStatus();

        if (status.versionField && !status.originalBeanIdField)
            return List.of(FieldFormatError.MISSING_VERSIONING_COUNTERPART.associateField(
                    fieldManager.fieldName(ReservedDatabaseField.ID_ORIGINAL_BEAN)));
        if (status.originalBeanIdField && !status.versionField)
            return List.of(FieldFormatError.MISSING_VERSIONING_COUNTERPART.associateField(
                    fieldManager.fieldName(ReservedDatabaseField.VERSION)));

        return List.of();
    }

    public Column getBeanVersionField() {
        if (!isVersioned())
            throw new IllegalStateException("Bean cannot be versioned. Check the versioning fields.");
        for (Column column: columns) {
            if (column.isVersionField())
                return column;
        }
        throw new AssertionError("Missing bean_version field in versioned bean: impossible situation.");
    }

    public boolean isItemOrderAssociatedField(Column column) {
        if (!hasItemOrder())
            return false;

        var itemOrderColumn = getItemOrderField();
        return column.getSqlName().equals(itemOrderColumn.getItemOrderAssociatedField());
    }

    public Optional<Column> getItemOrderAssociatedField() {
        if (hasItemOrder()) {
            for (Column column: columns)
                if (isItemOrderAssociatedField(column))
                    return Optional.of(column);
        }

        return Optional.empty();
    }

    public boolean hasSidField() {
        return hasSQLField(fieldManager.fieldName(ReservedDatabaseField.SID));
    }

}
