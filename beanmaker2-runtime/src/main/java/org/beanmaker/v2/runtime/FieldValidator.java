package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;

public class FieldValidator {

    private final DbBeanLocalization dbBeanLocalization;
    private final long id;
    private final String fieldName;
    private final String fieldLabel;
    private final boolean empty;
    private final boolean required;
    private final boolean shouldBeUnique;
    private final boolean isUnique;

    public static class Builder {
        private DbBeanLocalization dbBeanLocalization = null;
        private long id = -1;
        private String fieldName = null;
        private String fieldLabel = null;
        private boolean empty = false;
        private boolean required = false;
        private boolean shouldBeUnique = false;
        private boolean isUnique = false;

        private Builder() { }

        public Builder dbBeanLocalization(DbBeanLocalization dbBeanLocalization) {
            this.dbBeanLocalization = dbBeanLocalization;
            return this;
        }

        public Builder id(long id) {
            if (id < 0)
                throw new IllegalArgumentException("ID must be >= 0");

            this.id = id;
            return this;
        }

        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder fieldLabel(String fieldLabel) {
            this.fieldLabel = fieldLabel;
            return this;
        }

        public Builder empty(boolean empty) {
            this.empty = empty;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder shouldBeUnique(boolean shouldBeUnique) {
            this.shouldBeUnique = shouldBeUnique;
            return this;
        }

        public Builder isUnique(boolean isUnique) {
            this.isUnique = isUnique;
            return this;
        }

        public FieldValidator build() {
            if (dbBeanLocalization == null)
                throw new IllegalArgumentException("dbBeanLocalization is missing");

            if (id < 0)
                throw new IllegalArgumentException("ID must be >= 0");

            if (fieldName == null)
                throw new IllegalArgumentException("fieldName is missing");

            if (fieldLabel == null)
                throw new IllegalArgumentException("fieldLabel is missing");

            return new FieldValidator(
                    dbBeanLocalization,
                    id,
                    fieldName,
                    fieldLabel,
                    empty,
                    required,
                    shouldBeUnique,
                    isUnique
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private FieldValidator(
            DbBeanLocalization dbBeanLocalization,
            long id,
            String fieldName,
            String fieldLabel,
            boolean empty,
            boolean required,
            boolean shouldBeUnique,
            boolean isUnique)
    {
        this.dbBeanLocalization = dbBeanLocalization;
        this.id = id;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.empty = empty;
        this.required = required;
        this.shouldBeUnique = shouldBeUnique;
        this.isUnique = isUnique;
    }

    public boolean validate(
            List<FieldValidationFunction> validationFunctions,
            DBTransaction transaction)
    {
        return validate(validationFunctions, transaction, false);
    }

    public boolean validate(
            List<FieldValidationFunction> validationFunctions,
            DBTransaction transaction,
            boolean shortCircuit)
    {
        var emptinessEvaluation = new EmptinessEvaluation(shortCircuit);
        if (emptinessEvaluation.shouldReturn)
            return emptinessEvaluation.returnValue;

        return executeValidityChecks(validationFunctions, transaction, shortCircuit) && executeUnicityCheck(shortCircuit);
    }

    private class EmptinessEvaluation {
        boolean shouldReturn = false;
        boolean returnValue = true;

        EmptinessEvaluation(boolean shortCircuit) {
            if (empty) {
                shouldReturn = true;
                if (required) {
                    if (!shortCircuit)
                        dbBeanLocalization.addErrorMessage(id, fieldName, fieldLabel, dbBeanLocalization.getRequiredErrorMessage(fieldName));
                    returnValue = false;
                }
            }
        }
    }

    private boolean executeValidityChecks(
            List<FieldValidationFunction> validationFunctions,
            DBTransaction transaction,
            boolean shortCircuit)
    {
        boolean ok = true;
        for (var test: validationFunctions) {
            var result = test.validate(transaction);
            if (result.ok()) {
                if (result.isWarning() && !shortCircuit) {
                    dbBeanLocalization.addWarningMessage(
                            id,
                            fieldName,
                            fieldLabel,
                            dbBeanLocalization.formatMessage(result.getLabelName(), result.getLabelParameters())
                    );
                }
            } else {
                if (shortCircuit)
                    return false;

                dbBeanLocalization.addErrorMessage(
                        id,
                        fieldName,
                        fieldLabel,
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

    private boolean executeUnicityCheck(boolean shortCircuit) {
        if (shouldBeUnique && !isUnique) {
            if (!shortCircuit) {
                dbBeanLocalization.addErrorMessage(
                        id,
                        fieldName,
                        fieldLabel,
                        dbBeanLocalization.getNotUniqueErrorMessage(fieldName)
                );
            }
            return false;
        }
        return true;
    }

}
