package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class VarTag extends Tag<VarTag> {

    public VarTag() {
        element = new XMLElement("var", false);
    }

    public VarTag(String body) {
        element = new XMLElement("var", body, false);
    }

    @Override
    protected VarTag getThis() {
        return this;
    }

}
