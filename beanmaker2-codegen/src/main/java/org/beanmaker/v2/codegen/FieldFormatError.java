package org.beanmaker.v2.codegen;

public enum FieldFormatError {
    MISSING_ID(1001, "missing id field"),
    BAD_SQL_TYPE(1101, "contain bad SQL type association"),
    DUPLICATE_SPECIAL_FIELD(1201, "duplicate special field"),
    MISSING_VERSIONING_COUNTERPART(1301, "one of the versioning field is missing");

    private final int code;
    private final String defaultMessage;

    FieldFormatError(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public FieldAssociatedError associateField(String field) {
        return new FieldAssociatedError(this, field);
    }

    public static class FieldAssociatedError {
        private final FieldFormatError error;
        private final String fieldName;

        private FieldAssociatedError(FieldFormatError error, String fieldName) {
            this.error = error;
            this.fieldName = fieldName;
        }

        public FieldFormatError getError() {
            return error;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String formatMessage() {
            return "[" + fieldName + "] " + error.getCode() + " " + error.getDefaultMessage();
        }
    }

}
