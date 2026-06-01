package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public abstract class FormElement<T extends FormElement<T>> extends Tag<T> {

    public T required() {
        return attribute("required");
    }

    public T form(final String val) {
        return attribute("form", val);
    }
}
