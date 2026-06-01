package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class CodeTag extends Tag<CodeTag> {

    public CodeTag() {
        element = new XMLElement("code", false);
    }

    public CodeTag(String body) {
        element = new XMLElement("code", body, false);
    }

    @Override
    protected CodeTag getThis() {
        return this;
    }

}
