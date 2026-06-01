package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class DfnTag extends Tag<DfnTag> {

    public DfnTag() {
        element = new XMLElement("dfn", false);
    }

    public DfnTag(String body) {
        element = new XMLElement("dfn", body, false);
    }

    @Override
    protected DfnTag getThis() {
        return this;
    }

}
