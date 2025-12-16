package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.util.DecimalValue;
import org.beanmaker.v2.util.DecimalValueParser;
import org.beanmaker.v2.util.Strings;

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

        if (Strings.isEmpty(result))
            return null;

        return result;
    }

    public long getBeanId(String header) {
        String value = data.get(header);
        if (value == null)
            return 0L;

        return Long.parseLong(value);
    }

    public Long getLongValue(String header) {
        String value = data.get(header);
        if (value == null)
            return null;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert value [" + value + "] to Long @line #" + lineNumber, e);
        }
    }

    public Boolean getBooleanValue(String header, Map<String, Boolean> booleanMappings, boolean lenientParsing) {
        String value = getStringValue(header);
        if (value == null)
            return null;

        Boolean result = booleanMappings.get(value);
        if (result == null && !lenientParsing)
            throw new IllegalArgumentException("Cannot convert value [" + value + "] to Boolean @line #" + lineNumber);

        return result != null ? result : false;
    }

    public Integer getIntegerValue(String header) {
        String value = getStringValue(header);
        if (value == null)
            return null;

        try {
            return Integer.parseInt(getStringValue(header));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert value [" + value + "] to Integer @line #" + lineNumber, e);
        }
    }

    public DecimalValue getDecimalValue(String header, DecimalValueParser parser) {
        String value = getStringValue(header);
        if (value == null)
            return null;

        try {
            return parser.parse(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot convert value [" + value + "] to DecimalValue @line #" + lineNumber, e);
        }
    }

    // TODO: code other data types (numeric, bean reference, etc.)


    @Override
    public String toString() {
        return "DataEntry{" +
                "lineNumber = " + lineNumber +
                ", data = " + dataToString() +
                '}';
    }

    private String dataToString() {
        if (data.isEmpty())
            return "[ ]";

        var content = new StringBuilder();
        content.append("[ ");
        for (var entry: data.entrySet())
            content.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
        content.delete(content.length() - 2, content.length());
        content.append(" ]");

        return content.toString();
    }

}
