package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class LiTag extends Tag<LiTag> {

    public LiTag() {
        element = new XMLElement("li", false);
    }

    public LiTag(final String body) {
        element = new XMLElement("li", body, false);
    }

    @Override
    protected LiTag getThis() {
        return this;
    }

    /*@Override
    public LiTag child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public LiTag addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public LiTag value(final int value) {  // only if li is child of ol
        return attribute("value", Integer.toString(value));
    }
}
