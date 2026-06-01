package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public abstract class ColElement<T extends ColElement<T>> extends TableElement<T> {

    public T span(int value) {
        return attribute("span", Integer.toString(value));
    }

    public T width(String value) {
        return attribute("width", value);
    }

}
