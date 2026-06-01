package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public abstract class TableElement<T extends TableElement<T>> extends Tag<T> {

    public T align(HAlign value) {
        return attribute("align", value.toString());
    }

    public T alignOnChar(String value) {
        return attribute("char", value);
    }

    public T alignCharOff(String value) {
        return attribute("charoff", value);
    }

    public T valign(VAlign value) {
        return attribute("valign", value.toString());
    }

}
