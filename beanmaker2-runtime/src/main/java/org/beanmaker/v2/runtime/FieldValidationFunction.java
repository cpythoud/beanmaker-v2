package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

@FunctionalInterface
public interface FieldValidationFunction {

    FieldValidationResult validate(DBTransaction transaction);

}
