package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ATag extends Tag<ATag> {

    public ATag() {
        element = new XMLElement("a", false);
    }

    public ATag(final String body) {
        element = new XMLElement("a", body, false);
    }

    public ATag(final String body, final String link) {
        this(body);
        href(link);
    }

    @Override
    protected ATag getThis() {
        return this;
    }

    /*@Override
    public ATag child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public ATag addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public ATag name(final String value) {
        return attribute("name", value);
    }

    public ATag href(final String value) {
        return attribute("href", value);
    }

    public ATag hreflang(final String value) {
        return attribute("hreflang", value);
    }

    public ATag type(final String value) {
        return attribute("type", value);
    }

    public ATag rel(final String value) {
        return attribute("rel", value);
    }

    public ATag rev(final String value) {
        return attribute("rev", value);
    }

    public ATag charset(final String value) {
        return attribute("charset", value);
    }
}
