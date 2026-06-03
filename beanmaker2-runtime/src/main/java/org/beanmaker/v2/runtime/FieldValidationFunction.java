package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

@FunctionalInterface
public interface FieldValidationFunction {

    FieldValidationResult validate(DbTransaction transaction);

}
