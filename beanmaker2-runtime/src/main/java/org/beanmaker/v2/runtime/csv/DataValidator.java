package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.runtime.DbBeanEditor;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface DataValidator {

    DataValidator ALWAYS_TRUST = (editor, dataEntry) -> true;
    DataValidator BASIC_TEST = (editor, dataEntry) -> editor.isDataOK();

    static Map<String, Boolean> getDefaultBooleanValues() {
        var map = new HashMap<String, Boolean>();
        map.put("true", true);
        map.put("false", false);
        map.put("1", true);
        map.put("0", false);
        map.put("yes", true);
        map.put("no", false);
        return map;
    }

    boolean validate(DbBeanEditor editor, DataEntry dataEntry);

}
