package org.beanmaker.v2.runtime;

import java.util.List;

class PlatformMessage {

    public static final String GLOBAL_MESSAGE_MARKER = "__GLOBAL__";

    private final long beanId;
    private final String fieldName;
    private final String fieldLabel;
    private final String message;

    PlatformMessage(long beanId, String fieldName, String fieldLabel, String message) {
        this.beanId = beanId;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.message = message;
    }

    PlatformMessage(long beanId, String message) {
        this(beanId, GLOBAL_MESSAGE_MARKER, GLOBAL_MESSAGE_MARKER, message);
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
        return "{ \"idBean\": %d, \"fieldName\": \"%s\", \"fieldLabel\": \"%s\", \"message\": \"%s\" }"
                .formatted(beanId, fieldName, fieldLabel, message);
    }

    @Override
    public String toString() {
        return "%s{beanId=%d, fieldName='%s', fieldLabel='%s', message='%s'}"
                .formatted(getClass().getName(), beanId, fieldName, fieldLabel, message);
    }

    public static <M extends PlatformMessage> String toStrings(List<M> messages) {
        var buf = new StringBuilder();

        for (M message : messages)
            buf.append(message).append("\n");

        return buf.toString();
    }

    public static <T extends PlatformMessage> String toJson(List<T> messages, String jsonKey) {
        if (messages.isEmpty())
            throw new IllegalArgumentException("List of messages is empty.");

        StringBuilder buf = new StringBuilder();

        buf.append("\"").append(jsonKey).append("\": [ ");

        for (T message : messages) {
            buf.append(message.toJson()).append(", ");
        }

        buf.delete(buf.length() - 2, buf.length()); // Remove the trailing comma and space
        buf.append(" ]");

        return buf.toString();
    }

}
