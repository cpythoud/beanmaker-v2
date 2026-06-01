package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SelectTag extends FormElement<SelectTag> {

    public SelectTag() {
        element = new XMLElement("select", false);
    }

    public SelectTag(String name) {
        this();
        name(name);
    }

    @Override
    protected SelectTag getThis() {
        return this;
    }

    public SelectTag name(String value) {
        return attribute("name", value);
    }

    public SelectTag multiple() {
        return attribute("multiple");
    }

    public SelectTag size(int value) {
        return attribute("size", Integer.toString(value));
    }

    public SelectTag child(OptgroupTag tag) {
        return super.child(tag);
    }

    public SelectTag child(OptionTag tag) {
        return super.child(tag);
    }

}
