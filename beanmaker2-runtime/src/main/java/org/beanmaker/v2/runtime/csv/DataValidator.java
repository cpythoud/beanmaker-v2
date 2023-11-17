package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.runtime.DbBeanEditor;

@FunctionalInterface
public interface DataValidator {

    DataValidator ALWAYS_TRUST = (editor, dataEntry) -> true;
    DataValidator BASIC_TEST = (editor, dataEntry) -> editor.isDataOK();

    boolean validate(DbBeanEditor editor, DataEntry dataEntry);

}
