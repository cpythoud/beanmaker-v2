package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;
import java.util.function.Function;

public class GlobalValidator {

    private final DbBeanLocalization dbBeanLocalization;
    private final long id;

    public GlobalValidator(DbBeanLocalization dbBeanLocalization, long id) {
        this.dbBeanLocalization = dbBeanLocalization;
        this.id = id;
    }

    public boolean validate(
            List<Function<DBTransaction, FieldValidationResult>> validationFunctions,
            DBTransaction transaction)
    {
        boolean ok = true;
        for (var test: validationFunctions) {
            var result = test.apply(transaction);
            if (!result.ok()) {
                dbBeanLocalization.addErrorMessage(
                        id,
                        dbBeanLocalization.getBadFormatErrorMessage(result.getLabelName(), result.getLabelParameters())
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
