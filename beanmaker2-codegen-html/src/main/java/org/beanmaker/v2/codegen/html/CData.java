package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class CData extends Tag<CData> {

    public CData(final String text) {
        element = new XMLElement(XMLElement.CDATA, text);
    }

    @Override
    protected CData getThis() {
        throw new IllegalArgumentException("Text data cannot be combined with attributes or child elements");
    }
}
