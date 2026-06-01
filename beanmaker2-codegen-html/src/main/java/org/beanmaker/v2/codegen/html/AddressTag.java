package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class AddressTag extends Tag<AddressTag> {

    public AddressTag() {
        element = new XMLElement("address", false);
    }

    public AddressTag(String body) {
        element = new XMLElement("address", body, false);
    }

    @Override
    protected AddressTag getThis() {
        return this;
    }

}
