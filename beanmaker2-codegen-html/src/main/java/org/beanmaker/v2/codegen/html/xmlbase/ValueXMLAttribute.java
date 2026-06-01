package org.beanmaker.v2.codegen.html.xmlbase;

/**
 * ...
 */
public class ValueXMLAttribute extends XMLAttribute {

    public final String value;
    public final boolean useSingleQuotes;

    public ValueXMLAttribute(String name, String value) {
        this(name, value, false);
    }

    public ValueXMLAttribute(String name, String value, boolean useSingleQuotes) {
        super(name);
        this.value = value;
        this.useSingleQuotes = useSingleQuotes;
    }

    @Override
    public String toString() {
        return " " + name + "=" + getQuotedValue();
    }

    private String getQuotedValue() {
        if (useSingleQuotes)
            return "'" + value + "'";

        return "\"" + value + "\"";
    }

    public XMLAttribute copy() {
        return new ValueXMLAttribute(name, value, useSingleQuotes);
    }

}
