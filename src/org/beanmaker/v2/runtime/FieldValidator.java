package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

import java.util.function.Function;

public class FieldValidator {

    private final DbBeanLocalization dbBeanLocalization;
    private final long id;
    private final String fieldLabel;
    private final String fieldName;
    private final boolean empty;
    private final boolean required;
    private final boolean shouldBeUnique;
    private final boolean isUnique;

    public FieldValidator(
            DbBeanLocalization dbBeanLocalization,
            long id,
            String fieldLabel,
            String fieldName,
            boolean empty,
            boolean required,
            boolean shouldBeUnique,
            boolean isUnique)
    {
        this.dbBeanLocalization = dbBeanLocalization;
        this.id = id;
        this.fieldLabel = fieldLabel;
        this.fieldName = fieldName;
        this.empty = empty;
        this.required = required;
        this.shouldBeUnique = shouldBeUnique;
        this.isUnique = isUnique;
    }

    public boolean validate(
            List<Function<DBTransaction, FieldValidationResult>> validationFunctions,
            DBTransaction transaction)
    {
        var emptinessEvaluation = new EmptinessEvaluation();
        if (emptinessEvaluation.shouldReturn)
            return emptinessEvaluation.returnValue;

        return executeValidityChecks(validationFunctions, transaction) && executeUnicityCheck();
    }

    private class EmptinessEvaluation {
        boolean shouldReturn = false;
        boolean returnValue = true;

        EmptinessEvaluation() {
            if (empty) {
                shouldReturn = true;
                if (required) {
                    dbBeanLocalization.addErrorMessage(id, fieldName, fieldLabel, dbBeanLocalization.getRequiredErrorMessage(fieldName));
                    returnValue = false;
                }
            }
        }
    }

    private boolean executeValidityChecks(
            List<Function<DBTransaction, FieldValidationResult>> validationFunctions,
            DBTransaction transaction)
    {
        boolean ok = true;
        for (var test: validationFunctions) {
            var result = test.apply(transaction);
            if (!result.ok()) {
                dbBeanLocalization.addErrorMessage(
                        id,
                        fieldName,
                        fieldLabel,
                        dbBeanLocalization.getBadFormatErrorMessage(result.getLabelName(), result.getLabelParameters())
                );
                ok = false;
            }
        }
        return ok;
    }

    private boolean executeUnicityCheck() {
        if (shouldBeUnique && !isUnique) {
            dbBeanLocalization.addErrorMessage(id, fieldName, fieldLabel, dbBeanLocalization.getNotUniqueErrorMessage(fieldName));
            return false;
        }
        return true;
    }
}
