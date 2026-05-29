package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DBTransaction;

@FunctionalInterface
public interface FieldValidationFunction {

    FieldValidationResult validate(DBTransaction transaction);

}
