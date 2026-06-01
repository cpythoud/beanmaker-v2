package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ATag extends Tag<ATag> {

    public ATag() {
        element = new XMLElement("a", false);
    }

    public ATag(String body) {
        element = new XMLElement("a", body, false);
    }

    public ATag(String body, String link) {
        this(body);
        href(link);
    }

    @Override
    protected ATag getThis() {
        return this;
    }

    public ATag name(String value) {
        return attribute("name", value);
    }

    public ATag href(String value) {
        return attribute("href", value);
    }

    public ATag hreflang(String value) {
        return attribute("hreflang", value);
    }

    public ATag type(String value) {
        return attribute("type", value);
    }

    public ATag rel(String value) {
        return attribute("rel", value);
    }

    public ATag rev(String value) {
        return attribute("rev", value);
    }

    public ATag charset(String value) {
        return attribute("charset", value);
    }
}
