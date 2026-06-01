package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TextareaTag extends FormElement<TextareaTag> {

    public TextareaTag() {
        this("");
    }

    public TextareaTag(String content) {
        element = new XMLElement("textarea", content, false);
        element.setOnOneLine(true);
    }

    @Override
    protected TextareaTag getThis() {
        return this;
    }

    public TextareaTag cols(int val) {
        return cols(Integer.toString(val));
    }

    public TextareaTag rows(int val) {
        return rows(Integer.toString(val));
    }

    public TextareaTag cols(String val) {
        return attribute("cols", val);
    }

    public TextareaTag rows(String val) {
        return attribute("rows", val);
    }

    public TextareaTag maxlength(int val) {
        return maxlength(Integer.toString(val));
    }

    public TextareaTag maxlength(String val) {
        return attribute("maxlength", val);
    }

    public TextareaTag name(String val) {
        return attribute("name", val);
    }

    public TextareaTag readonly() {
        return attribute("readonly");
    }

    public TextareaTag placeholder(String value) {
        return attribute("placeholder", value);
    }
}
