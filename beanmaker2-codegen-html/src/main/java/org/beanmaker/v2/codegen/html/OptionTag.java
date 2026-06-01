package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class OptionTag extends Tag<OptionTag> {

    public OptionTag(final String body) {
        element = new XMLElement("option", body, false);
    }

    public OptionTag(final String body, final String value) {
        this(body);
        value(value);
    }

    @Override
    protected OptionTag getThis() {
        return this;
    }

    public OptionTag value(final String value) {
        return attribute("value", value);
    }

    public OptionTag selected() {
        return attribute("selected");
    }

    public OptionTag label(final String value) {
        return attribute("label", value);
    }
}
