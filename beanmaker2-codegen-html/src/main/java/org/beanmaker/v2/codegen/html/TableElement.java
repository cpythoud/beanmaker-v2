package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public abstract class TableElement<T extends TableElement<T>> extends Tag<T> {

    public T align(final HAlign value) {
        return attribute("align", value.toString());
    }

    public T alignOnChar(final String value) {
        return attribute("char", value);
    }

    public T alignCharOff(final String value) {
        return attribute("charoff", value);
    }

    public T valign(final VAlign value) {
        return attribute("valign", value.toString());
    }
}
