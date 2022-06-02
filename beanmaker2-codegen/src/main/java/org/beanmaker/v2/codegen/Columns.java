package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.English;
import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Columns {

    private final DatabaseServer server;
    private final String db;
    private final String table;

    private final List<Column> columns;
    private final List<OneToManyRelationship> detectedOneToManyRelationships;
    private final List<OneToManyRelationship> oneToManyRelationships;
    private final List<ExtraField> extraFields = new ArrayList<>();

    private static final List<String> NAMING_CANDIDATE_FIELDS = Arrays.asList("name", "description", "code");


    public Columns(DatabaseServer server, String db, String table) {
        this.server = server;
        this.db = db;
        this.table = table;
        columns = server.getColumns(db, table);
        // ! For now, we deactivate detection of possible one-to-many relationship
        // ! because the feature is a nuisance on large table sets and should probably be abandoned
        // ? To be reintroduced in ProjectParameters ?
        /*detectedOneToManyRelationships = server.getDetectedOneToManyRelationship(db, table);
        oneToManyRelationships = new ArrayList<>(detectedOneToManyRelationships);*/
        detectedOneToManyRelationships = Collections.emptyList();
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
        List<Column> copy = new ArrayList<>();

        for (Column column: columns)
            copy.add(new Column(column));

        return copy;
    }

    public int getCount() {
        return columns.size();
    }

    public Column getColumn(int index) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        return new Column(columns.get(index - 1));
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

    public void setItemOrderAssociatedField(int index, String itemOrderAssociatedField) {
        if (index < 1 || index > columns.size())
            throw new IndexOutOfBoundsException("There is no column number " + index);

        if (columns.get(index - 1).isItemOrder())
            columns.get(index - 1).setItemOrderAssociatedField(itemOrderAssociatedField);
        else
            throw new IllegalArgumentException("Column #" + index + " is not an item order field.");
    }

    public boolean hasBadField() {
        for (Column column: columns) {
            if (column.isBad())
                return true;
        }

        return false;
    }

    public boolean hasId() {
        for (Column column: columns) {
            if (column.isId())
                return true;
        }

        return false;
    }

    public boolean hasLastUpdate() {
        for (Column column: columns) {
            if (column.isLastUpdate())
                return true;
        }

        return false;
    }

    public boolean hasModifiedBy() {
        for (Column column: columns) {
            if (column.isModifiedBy())
                return true;
        }

        return false;
    }

    public boolean hasItemOrder() {
        for (Column column: columns) {
            if (column.isItemOrder())
                return true;
        }

        return false;
    }

    public Optional<Column> getItemOrderColumn() {
        for (Column column: columns) {
            if (column.isItemOrder())
                return Optional.of(column);
        }

        return Optional.empty();
    }

    public boolean hasDuplicatedSpecialField() {
        int idCount = 0;
        int lastUpdateCount = 0;
        int modifiedByCount = 0;
        int itemOrderCount = 0;

        for (Column column: columns) {
            if (column.isId())
                idCount++;
            if (column.isLastUpdate())
                lastUpdateCount++;
            if (column.isModifiedBy())
                modifiedByCount++;
            if (column.isItemOrder())
                itemOrderCount++;
        }

        return idCount > 1 || lastUpdateCount > 1 || modifiedByCount > 1 || itemOrderCount > 1;
    }

    public boolean hasLabels() {
        for (Column column: columns)
            if (column.isLabelReference())
                return true;

        return false;
    }

    public boolean hasLabelField() {
        for (Column column: columns)
            if (column.getJavaName().equals("idLabel"))
                return true;

        return false;
    }

    public boolean hasFiles() {
        for (Column column: columns)
            if (column.isFileReference())
                return true;

        return false;
    }

    public boolean hasFileField() {
        for (Column column: columns)
            if (column.getJavaName().equals("idFile"))
                return true;

        return false;
    }

    public boolean hasOtherBeanReference() {
        for (Column column: columns)
            if (column.isBeanReference() && !(column.isId() || column.isLabelReference() || column.isFileReference()))
                return true;

        return false;
    }

    public boolean isOK() {
        return hasId() && !hasBadField() && !hasDuplicatedSpecialField();
    }

    public Set<String> getJavaTypes() {
        Set<String> types = new HashSet<String>();

        for (Column column: columns)
            types.add(column.getJavaType());

        return types;
    }

    public boolean containsNumericalData() {
        for (Column column: columns)
            if (column.getJavaType().equals("Integer") || column.getJavaType().equals("Long"))
                if (!column.isSpecial() && !column.getJavaName().startsWith("id"))
                    return true;

        return false;
    }

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
        Set<String> tableNames = new HashSet<String>();
        for (OneToManyRelationship relationship: relationships)
            tableNames.add(relationship.getTable());
        return tableNames;
    }

    public boolean hasOneToManyRelationships() {
        return oneToManyRelationships.size() > 0;
    }

    public String getNamingField() {
        for (String candidate: NAMING_CANDIDATE_FIELDS)
            for (Column col: columns)
                if (col.getSqlName().equalsIgnoreCase(candidate))
                    return candidate;

        return "id";
    }

    public List<String> getOrderByFields() {
        var list = new ArrayList<String>();

        if (hasItemOrder()) {
            var itemOrder = getItemOrderField();
            if (!itemOrder.isUnique())
                list.add(itemOrder.getItemOrderAssociatedField());
            list.add("item_order");
        } else
            list.add(getNamingField());

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

    public void removeExtrafield(String name) {
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
        removeExtrafield(extraField.getName());
    }

    public void removeExtraField(int index) {
        if (index < 0 || index > extraFields.size())
            throw new IndexOutOfBoundsException("Bounds : 0-" + (extraFields.size() - 1) + ", index : " + index);

        extraFields.remove(index);
    }

    public boolean hasExtraFields() {
        return extraFields.size() > 0;
    }

    public String getSuggestedBeanName() {
        return getSuggestedBeanName(table);
    }

    public static String getSuggestedBeanName(String table) {
        return Strings.camelize(English.singularize(table));
    }

    public boolean hasCodeField() {
        for (Column column: columns)
            if (column.getJavaName().equals("code"))
                return true;

        return false;
    }

    public List<Column> getLabels() {
        var columns = new ArrayList<Column>();

        for (Column column: getList())
            if (column.isLabelReference())
                columns.add(column);

        return columns;
    }

}
