package org.beanmaker.v2.codegen;

import org.beanmaker.v2.database.sql.DbType;

import org.beanmaker.v2.util.Strings;

import java.util.List;

public class Column {

    // * int and long are used internally but are not allowed as user types
    public static final List<String> SUPPORTED_JAVA_TYPES =
            List.of("long", "Boolean", "Integer", "Long", "String", "Date", "Time", "Timestamp", "Money",
                    "DecimalValue");

    private static final String DEFAULT_LABEL_CLASS = "DbBeanLabel";
    private static final String DEFAULT_FILE_CLASS  = "DbBeanFile";

    private final ReservedDatabaseFieldManager fieldManager;
    private final DbType dbType;

    private final String sqlTypeName;
    private final String sqlName;
    private final int displaySize;
    private final int precision;
    private final int scale;
    private final boolean autoincrement;

    private String javaType;
    private String javaName;

    private boolean required;
    private final boolean shouldBeRequired;
    private boolean unique = false;

    private String associatedBeanClass;
    private String itemOrderAssociatedField;

    private final boolean id;
    private final boolean lastUpdate;
    private final boolean modifiedBy;
    private final boolean itemOrder;
    private final boolean versionField;
    private final boolean originalBeanId;

    private final boolean special;
    private final boolean incompatibleSqlType;

    private int decimals = 2;
    private boolean negativeAllowed = true;

    public Column(
            ReservedDatabaseFieldManager fieldManager,
            DbType dbType,
            String sqlTypeName,
            String sqlName,
            int displaySize,
            int precision,
            int scale,
            boolean autoincrement,
            boolean required
    ) {
        this.fieldManager = fieldManager;
        this.dbType = dbType;
        this.sqlTypeName = sqlTypeName;
        this.sqlName = sqlName;
        this.displaySize = displaySize;
        this.precision = precision;
        this.scale = scale;
        this.autoincrement = autoincrement;
        this.required = required;
        shouldBeRequired = required;

        var specialFields = fieldManager.allFieldNames();
        var specialFieldTypes = ReservedDatabaseFieldTypeMappings.getSqlTypeMap(dbType);

        if (specialFields.contains(sqlName)) {
            special = true;
            var fieldType = fieldManager.fieldType(sqlName);
            incompatibleSqlType = !specialFieldTypes.get(fieldType).contains(sqlTypeName);
            if (fieldType.equals(ReservedDatabaseField.ID)) {
                id = true;
                unique = true;
            } else {
                id = false;
            }
            lastUpdate = fieldType.equals(ReservedDatabaseField.LAST_UPDATE);
            modifiedBy = fieldType.equals(ReservedDatabaseField.MODIFIED_BY);
            if (fieldType.equals(ReservedDatabaseField.ITEM_ORDER)) {
                itemOrder = true;
                unique = true;  // * on first detection, will be changed to false when secondary field is specified
            } else {
                itemOrder = false;
            }
            versionField = fieldType.equals(ReservedDatabaseField.VERSION);
            originalBeanId = fieldType.equals(ReservedDatabaseField.ID_ORIGINAL_BEAN);
        } else {
            special = false;
            incompatibleSqlType = false;
            id = false;
            lastUpdate = false;
            modifiedBy = false;
            itemOrder = false;
            versionField = false;
            originalBeanId = false;
        }

        suggestType();
        suggestName();
        suggestAssociatedBeanClass();
    }

    public Column(Column col) {
        this.fieldManager = col.fieldManager;
        this.dbType = col.dbType;
        this.sqlTypeName = col.sqlTypeName;
        this.sqlName = col.sqlName;
        this.displaySize = col.displaySize;
        this.precision = col.precision;
        this.scale = col.scale;
        this.autoincrement = col.autoincrement;
        this.special = col.special;
        this.incompatibleSqlType = col.incompatibleSqlType;
        this.id = col.id;
        this.lastUpdate = col.lastUpdate;
        this.modifiedBy = col.modifiedBy;
        this.itemOrder = col.itemOrder;
        this.javaType = col.javaType;
        this.javaName = col.javaName;
        this.required = col.required;
        this.shouldBeRequired = col.shouldBeRequired;
        this.unique = col.unique;
        this.associatedBeanClass = col.associatedBeanClass;
        this.itemOrderAssociatedField = col.itemOrderAssociatedField;
        this.decimals = col.decimals;
        this.negativeAllowed = col.negativeAllowed;
        this.versionField = col.versionField;
        this.originalBeanId = col.originalBeanId;
    }

    public String getSqlTypeName() {
        return sqlTypeName;
    }

    public String getSqlName() {
        return sqlName;
    }

    public int getDisplaySize() {
        return displaySize;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean isAutoIncrement() {
        return autoincrement;
    }

    public boolean isSigned() {
        return !sqlTypeName.contains("UNSIGNED");
    }

    public String getJavaType() {
        return javaType;
    }

    public String getCapitalizedJavaType() {
        return Strings.capitalize(javaType);
    }

    public String getJavaName() {
        return javaName;
    }

    public String getCapitalizedJavaName() {
        return Strings.capitalize(javaName);
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isUniqueCodeField() {
        return getJavaName().equals("code") && isUnique();
    }

    public boolean isCodeField() {
        return getJavaName().equals("code");
    }

    public int getDecimals() {
        return decimals;
    }

    public boolean canBeNegative() {
        return negativeAllowed;
    }

    public void setJavaType(String javaType) {
        if (!SUPPORTED_JAVA_TYPES.contains(javaType))
            throw new IllegalArgumentException(javaType + " type cannot be used with BeanMaker");

        this.javaType = javaType;
    }

    public void setJavaName(String javaName) {
        if (Strings.isEmpty(javaName))
            throw new IllegalArgumentException("Empty java name not allowed");

        this.javaName = javaName;
    }

    public void setRequired(boolean required) {
        if (special && !required)
            throw new IllegalArgumentException("Special field marked as not required");

        this.required = required;
    }

    public void setUnique(boolean unique) {
        if (special) {
            if (id && !unique)
                throw new IllegalArgumentException("Schema error: ID field not unique");
            if (lastUpdate && unique)
                throw new IllegalArgumentException("Schema error: last_update field marked as unique");
            if (modifiedBy && unique)
                throw new IllegalArgumentException("Schema error: modified_by field marked as unique");
        }

        this.unique = unique;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public void canBeNegative(boolean negativeAllowed) {
        this.negativeAllowed = negativeAllowed;
    }

    public boolean isId() {
        return id;
    }

    public boolean isLastUpdate() {
        return lastUpdate;
    }

    public boolean isModifiedBy() {
        return modifiedBy;
    }

    public boolean isItemOrder() {
        return itemOrder;
    }

    public boolean isSpecial() {
        return special;
    }

    @Deprecated
    public boolean isBad() {
        return incompatibleSqlType();
    }

    public boolean incompatibleSqlType() {
        return incompatibleSqlType;
    }

    public boolean isBeanReference() {
        return isId() || sqlName.startsWith("id_");
    }

    public boolean isDecimalValue() {
        return javaType.equals("DecimalValue");
    }

    public boolean isVersionField() {
        return versionField;
    }

    public boolean isOriginalBeanId() {
        return originalBeanId;
    }

    @Override
    public String toString() {
        return "Column{" +
                "reservedDatabaseFieldManager=" + fieldManager +
                ", dbType=" + dbType +
                ", sqlTypeName='" + sqlTypeName + '\'' +
                ", sqlName='" + sqlName + '\'' +
                ", displaySize=" + displaySize +
                ", precision=" + precision +
                ", scale=" + scale +
                ", autoincrement=" + autoincrement +
                ", javaType='" + javaType + '\'' +
                ", javaName='" + javaName + '\'' +
                ", required=" + required +
                ", shouldBeRequired=" + shouldBeRequired +
                ", unique=" + unique +
                ", associatedBeanClass='" + associatedBeanClass + '\'' +
                ", itemOrderAssociatedField='" + itemOrderAssociatedField + '\'' +
                ", id=" + id +
                ", lastUpdate=" + lastUpdate +
                ", modifiedBy=" + modifiedBy +
                ", itemOrder=" + itemOrder +
                ", versionField=" + versionField +
                ", originalBeanId=" + originalBeanId +
                ", special=" + special +
                ", incompatibleSqlType=" + incompatibleSqlType +
                ", decimals=" + decimals +
                ", negativeAllowed=" + negativeAllowed +
                '}';
    }

    private void suggestType() {
        javaType = getSuggestedType();
    }

    public String getSuggestedType() {
        if (id || itemOrder || originalBeanId || sqlName.startsWith("id_"))
            return "long";
        if (versionField)
            return "int";

        return ReservedDatabaseFieldTypeMappings.suggestJavaType(dbType, sqlTypeName, precision);
    }

    public String getSuggestedName() {
        return Strings.uncapitalize(Strings.camelize(sqlName));
    }

    private void suggestName() {
        javaName = getSuggestedName();
    }

    public boolean couldHaveAssociatedBean() {
        return !special && sqlName.startsWith("id_");
    }

    public boolean couldBeLabelReference() {
        return couldHaveAssociatedBean() && sqlName.endsWith("_label");
    }

    public boolean couldBeFileReference() {
        return couldHaveAssociatedBean() && sqlName.endsWith("_file");
    }

    public boolean isLabelReference() {
        return associatedBeanClass != null && associatedBeanClass.equals(DEFAULT_LABEL_CLASS);
    }

    public boolean isFileReference() {
        return associatedBeanClass != null && associatedBeanClass.equals(DEFAULT_FILE_CLASS);
    }

    public String getSuggestedAssociatedBeanClass() {
        if (!couldHaveAssociatedBean())
            return "";

        if (couldBeLabelReference())
            return DEFAULT_LABEL_CLASS;

        if (couldBeFileReference())
            return DEFAULT_FILE_CLASS;

        return Strings.camelize(sqlName.substring(3));
    }

    private void suggestAssociatedBeanClass() {
        if (!couldHaveAssociatedBean())
            return;

        associatedBeanClass = getSuggestedAssociatedBeanClass();
    }

    public boolean hasAssociatedBean() {
        return !Strings.isEmpty(associatedBeanClass);
    }

    public String getAssociatedBeanClass() {
        if (Strings.isEmpty(associatedBeanClass))
            return "";

        return associatedBeanClass;
    }

    public void setAssociatedBeanClass(String associatedBeanClass) {
        if (!couldHaveAssociatedBean())
            throw new IllegalArgumentException("AssociatedBean " + associatedBeanClass + " not allowed for column " + sqlName);

        this.associatedBeanClass = associatedBeanClass;
    }

    public String getItemOrderAssociatedField() {
        if (Strings.isEmpty(itemOrderAssociatedField))
            return "";

        return itemOrderAssociatedField;
    }

    public void setItemOrderAssociatedField(String itemOrderAssociatedField) {
        if (!isItemOrder())
            throw new IllegalArgumentException("This column is not an item order field.");

        if (Strings.isEmpty(itemOrderAssociatedField)) {
            this.itemOrderAssociatedField = "";
            unique = true;
        } else {
            this.itemOrderAssociatedField = itemOrderAssociatedField;
            unique = false;
        }
    }

    public boolean hasItemOrderAssociatedField() {
        if (!isItemOrder())
            throw new IllegalArgumentException("Not an item_order column");

        return !Strings.isEmpty(itemOrderAssociatedField);
    }

    public boolean shouldBeRequired() {
        return shouldBeRequired;
    }

    public boolean isOtherBeanReference() {
        return isBeanReference() && !isSpecial() && !isLabelReference() && !isFileReference();
    }

}
