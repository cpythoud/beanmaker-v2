package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class OptgroupTag extends Tag<OptgroupTag> {

    public OptgroupTag() {
        element = new XMLElement("optgroup", false);
    }

    public OptgroupTag(final String label) {
        this();
        label(label);
    }

    @Override
    protected OptgroupTag getThis() {
        return this;
    }

    public OptgroupTag label(final String value) {
        return attribute("label", value);
    }

    public OptgroupTag child(final OptionTag tag) {
        return super.child(tag);
    }
}
