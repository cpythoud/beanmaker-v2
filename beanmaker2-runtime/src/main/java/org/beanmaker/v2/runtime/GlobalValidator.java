package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

public class GlobalValidator {

    private final DbBeanLocalization dbBeanLocalization;
    private final long id;

    public GlobalValidator(DbBeanLocalization dbBeanLocalization, long id) {
        this.dbBeanLocalization = dbBeanLocalization;
        this.id = id;
    }

    public boolean validate(
            List<FieldValidationFunction> validationFunctions,
            DBTransaction transaction)
    {
        boolean ok = true;
        for (var test: validationFunctions) {
            var result = test.validate(transaction);
            if (result.ok()) {
                if (result.isWarning()) {
                    dbBeanLocalization.addWarningMessage(
                            id,
                            dbBeanLocalization.formatMessage(result.getLabelName(), result.getLabelParameters())
                    );
                }
            } else {
                dbBeanLocalization.addErrorMessage(
                        id,
                        dbBeanLocalization.formatMessage(result.getLabelName(), result.getLabelParameters())
                );
                if (result.continueOnError())
                    ok = false;
                else
                    return false;
            }
        }
        return ok;
    }

}
