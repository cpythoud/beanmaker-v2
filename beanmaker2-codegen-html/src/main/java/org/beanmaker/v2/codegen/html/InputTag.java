package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class InputTag extends FormElement<InputTag> {

    public enum InputType {
        HIDDEN("hidden"),
        TEXT("text"),
        SEARCH("search"),
        TEL("tel"),
        URL("url"),
        EMAIL("email"),
        PASSWORD("password"),
        DATETIME("datetime"),
        DATE("date"),
        MONTH("month"),
        WEEK("week"),
        TIME("time"),
        DATETIME_LOCAL("datetime-local"),
        NUMBER("number"),
        RANGE("range"),
        COLOR("color"),
        CHECKBOX("checkbox"),
        RADIO("radio"),
        FILE("file"),
        SUBMIT("submit"),
        IMAGE("image"),
        RESET("reset"),
        BUTTON("button");

        private final String val;

        private InputType(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public static InputType getType(final String s) {
            final String val = s.toLowerCase();
            if (val.equals("hidden"))
                return HIDDEN;
            if (val.equals("text"))
                return TEXT;
            if (val.equals("search"))
                return SEARCH;
            if (val.equals("tel"))
                return TEL;
            if (val.equals("url"))
                return URL;
            if (val.equals("email"))
                return EMAIL;
            if (val.equals("password"))
                return PASSWORD;
            if (val.equals("datetime"))
                return DATETIME;
            if (val.equals("date"))
                return DATE;
            if (val.equals("month"))
                return MONTH;
            if (val.equals("week"))
                return WEEK;
            if (val.equals("time"))
                return TIME;
            if (val.equals("datetime-local"))
                return DATETIME_LOCAL;
            if (val.equals("number"))
                return NUMBER;
            if (val.equals("range"))
                return RANGE;
            if (val.equals("color"))
                return COLOR;
            if (val.equals("checkbox"))
                return CHECKBOX;
            if (val.equals("radio"))
                return RADIO;
            if (val.equals("file"))
                return FILE;
            if (val.equals("submit"))
                return SUBMIT;
            if (val.equals("image"))
                return IMAGE;
            if (val.equals("reset"))
                return RESET;
            if (val.equals("button"))
                return BUTTON;

            throw new IllegalArgumentException(val + " does not correspond to an input type");
        }
    }

    public InputTag(final InputType type) {
        element = new XMLElement("input");
        type(type);
    }

    @Override
    protected InputTag getThis() {
        return this;
    }

    public InputTag type(final InputType value) {
        return attribute("type", value.getVal());
    }

    public InputTag checked() {
        return attribute("checked");
    }

    public InputTag readonly() {
        return attribute("readonly");
    }

    public InputTag size(final int value) {
        return attribute("size", Integer.toString(value));
    }

    public InputTag maxlength(final int value) {
        return attribute("maxlength", Integer.toString(value));
    }

    public InputTag src(final String value) {
        return attribute("src", value);
    }

    public InputTag alt(final String value) {
        return attribute("alt", value);
    }

    public InputTag usemap(final String value) {
        return attribute("usemap", value);
    }

    public InputTag ismap() {
        return attribute("ismap");
    }

    public InputTag accept(final String value) {
        return attribute("accept", value);
    }

    public InputTag name(final String value) {
        return attribute("name", value);
    }

    public InputTag value(final String value) {
        return attribute("value", value);
    }

    public InputTag placeholder(final String value) {
        return attribute("placeholder", value);
    }
}
