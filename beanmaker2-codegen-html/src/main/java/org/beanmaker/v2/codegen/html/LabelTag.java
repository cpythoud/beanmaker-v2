package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class LabelTag extends Tag<LabelTag> {

    public LabelTag() {
        element = new XMLElement("label", false);
    }

    public LabelTag(final String body) {
        element = new XMLElement("label", body, false);
    }

    public LabelTag(final String body, final String formElementId) {
        element = new XMLElement("label", body, false);
        forAttr(formElementId);
    }

    @Override
    protected LabelTag getThis() {
        return this;
    }

    /*@Override
    public LabelTag child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public LabelTag addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public LabelTag forAttr(final String val) {
        return attribute("for", val);
    }
}
