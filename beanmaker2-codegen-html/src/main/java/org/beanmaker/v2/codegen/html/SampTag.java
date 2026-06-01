package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SampTag extends Tag<SampTag> {

    public SampTag() {
        element = new XMLElement("samp", false);
    }

    public SampTag(final String body) {
        element = new XMLElement("samp", body, false);
    }

    @Override
    protected SampTag getThis() {
        return this;
    }
}
