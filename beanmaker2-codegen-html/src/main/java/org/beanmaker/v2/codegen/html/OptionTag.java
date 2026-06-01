package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class OptionTag extends Tag<OptionTag> {

    public OptionTag(String body) {
        element = new XMLElement("option", body, false);
    }

    public OptionTag(String body, String value) {
        this(body);
        value(value);
    }

    @Override
    protected OptionTag getThis() {
        return this;
    }

    public OptionTag value(String value) {
        return attribute("value", value);
    }

    public OptionTag selected() {
        return attribute("selected");
    }

    public OptionTag label(String value) {
        return attribute("label", value);
    }

}
