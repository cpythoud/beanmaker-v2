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

        InputType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public static InputType getType(String s) {
            String val = s.toLowerCase();
            return switch (val) {
                case "hidden" -> HIDDEN;
                case "text" -> TEXT;
                case "search" -> SEARCH;
                case "tel" -> TEL;
                case "url" -> URL;
                case "email" -> EMAIL;
                case "password" -> PASSWORD;
                case "datetime" -> DATETIME;
                case "date" -> DATE;
                case "month" -> MONTH;
                case "week" -> WEEK;
                case "time" -> TIME;
                case "datetime-local" -> DATETIME_LOCAL;
                case "number" -> NUMBER;
                case "range" -> RANGE;
                case "color" -> COLOR;
                case "checkbox" -> CHECKBOX;
                case "radio" -> RADIO;
                case "file" -> FILE;
                case "submit" -> SUBMIT;
                case "image" -> IMAGE;
                case "reset" -> RESET;
                case "button" -> BUTTON;
                default -> throw new IllegalArgumentException(val + " does not correspond to an input type");
            };
        }
    }

    public InputTag(InputType type) {
        element = new XMLElement("input");
        type(type);
    }

    @Override
    protected InputTag getThis() {
        return this;
    }

    public InputTag type(InputType value) {
        return attribute("type", value.getVal());
    }

    public InputTag checked() {
        return attribute("checked");
    }

    public InputTag readonly() {
        return attribute("readonly");
    }

    public InputTag size(int value) {
        return attribute("size", Integer.toString(value));
    }

    public InputTag maxlength(int value) {
        return attribute("maxlength", Integer.toString(value));
    }

    public InputTag src(String value) {
        return attribute("src", value);
    }

    public InputTag alt(String value) {
        return attribute("alt", value);
    }

    public InputTag usemap(String value) {
        return attribute("usemap", value);
    }

    public InputTag ismap() {
        return attribute("ismap");
    }

    public InputTag accept(String value) {
        return attribute("accept", value);
    }

    public InputTag name(String value) {
        return attribute("name", value);
    }

    public InputTag value(String value) {
        return attribute("value", value);
    }

    public InputTag placeholder(String value) {
        return attribute("placeholder", value);
    }
}
