package org.beanmaker.v2.codegen;

import java.util.List;

public interface ReservedDatabaseFieldManager {

    ReservedDatabaseFieldManager DEFAULT = new DefaultReservedDatabaseFieldManager();

    String fieldName(ReservedDatabaseField field);

    ReservedDatabaseField fieldType(String fieldName);

    List<String> allFieldNames();

    default boolean isReservedField(String field) {
        return allFieldNames().contains(field);
    }

}
