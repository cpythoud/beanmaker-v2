package org.beanmaker.v2.codegen;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultReservedDatabaseFieldManager implements ReservedDatabaseFieldManager {

    private static final String ID_FIELD = "id";
    private static final String SID_FIELD = "sid";
    private static final String UPDATE_FIELD = "bean_last_update";
    private static final String MODIFIER_FIELD = "bean_modified_by";
    private static final String ORDERING_FIELD = "item_order";
    private static final String VERSION_FIELD = "bean_version";
    private static final String ID_FIRST_VERSION_FIELD = "id_original_bean";

    private static final Map<ReservedDatabaseField, String> TYPE_TO_NAME_MAP;
    private static final Map<String, ReservedDatabaseField> NAME_TO_TYPE_MAP;
    static {
        var fields = new EnumMap<ReservedDatabaseField, String>(ReservedDatabaseField.class);

        fields.put(ReservedDatabaseField.ID, ID_FIELD);
        fields.put(ReservedDatabaseField.SID, SID_FIELD);
        fields.put(ReservedDatabaseField.LAST_UPDATE, UPDATE_FIELD);
        fields.put(ReservedDatabaseField.MODIFIED_BY, MODIFIER_FIELD);
        fields.put(ReservedDatabaseField.ITEM_ORDER, ORDERING_FIELD);
        fields.put(ReservedDatabaseField.VERSION, VERSION_FIELD);
        fields.put(ReservedDatabaseField.ID_ORIGINAL_BEAN, ID_FIRST_VERSION_FIELD);

        if (fields.size() != ReservedDatabaseField.values().length)
            throw new AssertionError("Missing database field mapping");

        TYPE_TO_NAME_MAP = Collections.unmodifiableMap(fields);

        var inverseFields = new HashMap<String, ReservedDatabaseField>();
        for (var entry: fields.entrySet()) {
            if (inverseFields.put(entry.getValue(), entry.getKey()) != null)
                throw new AssertionError("Duplicate database field mapping: " + entry.getValue());
        }

        NAME_TO_TYPE_MAP = Collections.unmodifiableMap(inverseFields);
    }

    private static final List<String> FIELD_LIST =
            List.of(ID_FIELD, SID_FIELD, UPDATE_FIELD, MODIFIER_FIELD, ORDERING_FIELD, VERSION_FIELD,
                    ID_FIRST_VERSION_FIELD);

    @Override
    public String fieldName(ReservedDatabaseField field) {
        return Objects.requireNonNull(TYPE_TO_NAME_MAP.get(field));
    }

    @Override
    public ReservedDatabaseField fieldType(String fieldName) {
        var type = NAME_TO_TYPE_MAP.get(fieldName);
        if (type == null)
            throw new IllegalArgumentException("Field '" + fieldName + "' is not a special field");

        return type;
    }

    @Override
    public List<String> allFieldNames() {
        return FIELD_LIST;
    }

}
