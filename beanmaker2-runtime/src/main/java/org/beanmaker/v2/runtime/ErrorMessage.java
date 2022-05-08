package org.beanmaker.v2.runtime;

import java.util.List;

public class ErrorMessage {

    public static final String GLOBAL_MESSAGE_MARKER = "__GLOBAL__";

    private final long beanId;
    private final String fieldName;
    private final String fieldLabel;
    private final String message;

	public ErrorMessage(long beanId, String fieldName, String fieldLabel, String message) {
		this.beanId = beanId;
		this.fieldName = fieldName;
		this.fieldLabel = fieldLabel;
		this.message = message;
	}

    public ErrorMessage(long beanId, String message) {
        this.beanId = beanId;
        fieldName = GLOBAL_MESSAGE_MARKER;
        fieldLabel = GLOBAL_MESSAGE_MARKER;
        this.message = message;
    }
	
	public long getBeanId() {
		return beanId;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getFieldLabel() {
		return fieldLabel;
	}
	
	public String getMessage() {
		return message;
	}

    public String toJson() {
        return "{ \"idBean\": " + beanId + ", " + "\"fieldName\": \"" + fieldName + "\", " + "\"fieldLabel\": \"" + fieldLabel + "\", " + "\"message\": \"" + message + "\" }";
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "beanId=" + beanId +
                ", fieldName='" + fieldName + '\'' +
                ", fieldLabel='" + fieldLabel + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static String toStrings(List<ErrorMessage> errorMessages) {
        StringBuilder buf = new StringBuilder();

        for (ErrorMessage errorMessage: errorMessages)
            buf.append(errorMessage).append("\n");

        return buf.toString();
    }

    public static String toJson(List<ErrorMessage> errorMessages) {
        if (errorMessages.isEmpty())
            throw new IllegalArgumentException("List of error messages is empty.");

        StringBuilder buf = new StringBuilder();

        buf.append("\"errors\": [ ");

        for (ErrorMessage errorMessage: errorMessages)
            buf.append(errorMessage.toJson()).append(", ");

        buf.delete(buf.length() - 2, buf.length());
        buf.append(" ]");

        return buf.toString();
    }

}
