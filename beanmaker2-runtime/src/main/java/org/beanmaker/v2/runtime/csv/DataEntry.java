package org.beanmaker.v2.runtime.csv;

import java.util.Map;

public class DataEntry {

    private final long lineNumber;
    private final Map<String, String> data;

    DataEntry(long lineNumber, Map<String, String> data) {
        this.lineNumber = lineNumber;
        this.data = data;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getStringValue(String header) {
        String result = data.get(header);
        if (result == null)
            throw new IllegalArgumentException("No such column: " + header);
        return result;
    }

    public long getLongValue(String header) {
        return Long.parseLong(getStringValue(header));
    }

    public Boolean getBooleanValue(String header, Map<String, Boolean> booleanMappings, boolean lenientParsing) {
        String value = getStringValue(header);
        Boolean result = booleanMappings.get(value);
        if (result == null && !lenientParsing)
            throw new IllegalArgumentException("Cannot convert value [" + value + "] to boolean @line #" + lineNumber);
        return result != null ? result : false;
    }

    public Integer getIntegerValue(String header) {
        try {
            return Integer.parseInt(getStringValue(header));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert value [" + getStringValue(header) + "] to integer @line #" + lineNumber, e);
        }
    }

    // TODO: code other data types (numeric, bean reference, etc.)

}
