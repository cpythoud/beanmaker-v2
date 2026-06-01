package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class AbbrTag extends Tag<AbbrTag> {

    public AbbrTag() {
        element = new XMLElement("abbr", false);
    }

    public AbbrTag(final String body) {
        element = new XMLElement("abbr", body, false);
    }

    @Override
    protected AbbrTag getThis() {
        return this;
    }
}
