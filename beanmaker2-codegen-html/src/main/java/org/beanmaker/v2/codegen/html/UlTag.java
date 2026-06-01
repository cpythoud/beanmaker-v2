package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

import java.util.List;

/**
 * ...
 */
public class UlTag extends Tag<UlTag> {

    public UlTag() {
        element = new XMLElement("ul", false);
    }

    @Override
    protected UlTag getThis() {
        return this;
    }

    public static UlTag createList(final List<Object> items) {
        if (items == null)
            throw new NullPointerException("Item List is null");
        if (items.isEmpty())
            throw new IllegalArgumentException("Item list is empty");

        return createList(new UlTag(), items);
    }
}
