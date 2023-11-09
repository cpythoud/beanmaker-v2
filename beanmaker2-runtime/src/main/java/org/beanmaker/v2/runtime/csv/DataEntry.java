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

    // TODO: code other data types (numeric, bean reference, etc.)

}
