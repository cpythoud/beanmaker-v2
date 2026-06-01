package org.beanmaker.v2.codegen.html.util;

import org.beanmaker.v2.codegen.html.FormTag;
import org.beanmaker.v2.codegen.html.Tag;

/**
 * ...
 */
public class FormHelper {

    private final FormTag form = new FormTag();

    private final Tag formItemsWrapper;
    private final Tag itemWrapper;
    private final Tag labelWrapper;
    private final Tag fieldWrapper;

    public FormHelper(final Tag formItemsWrapper, final Tag itemWrapper, final Tag labelWrapper, final Tag fieldWrapper) {
        this.formItemsWrapper = formItemsWrapper;
        this.itemWrapper = itemWrapper;
        this.labelWrapper = labelWrapper;
        this.fieldWrapper = fieldWrapper;
    }

    public FormTag getForm() {
        return form;
    }

    @Override
    public String toString() {
        return form.toString();
    }
}
