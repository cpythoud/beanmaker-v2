package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class LabelTag extends Tag<LabelTag> {

    public LabelTag() {
        element = new XMLElement("label", false);
    }

    public LabelTag(String body) {
        element = new XMLElement("label", body, false);
    }

    public LabelTag(String body, String formElementId) {
        element = new XMLElement("label", body, false);
        forAttr(formElementId);
    }

    @Override
    protected LabelTag getThis() {
        return this;
    }

    public LabelTag forAttr(String val) {
        return attribute("for", val);
    }

}
