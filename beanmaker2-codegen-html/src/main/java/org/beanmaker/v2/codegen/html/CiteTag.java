package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class CiteTag extends Tag<CiteTag> {

    public CiteTag() {
        element = new XMLElement("cite", false);
    }

    public CiteTag(final String body) {
        element = new XMLElement("cite", body, false);
    }

    @Override
    protected CiteTag getThis() {
        return this;
    }
}
