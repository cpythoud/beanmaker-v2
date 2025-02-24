package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.ButtonTag;
import org.jcodegen.html.DivTag;
import org.jcodegen.html.FormElement;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.LabelTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.Tag;
import org.jcodegen.html.TextareaTag;

import org.jcodegen.html.util.CssClasses;

public class W3CssHtmlFormHelper extends AbstractHtmlFormHelper {

    @Override
    public FormTag getForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();

        return form.cssClass(getFormCssClasses(beanName, "w3-container"));
    }

    @Override
    public FormTag getHorizontalForm(String beanName, long id) {
        return getForm(beanName, id);
    }

    @Override
    public FormTag getInlineForm(String beanName, long id) {
        return getForm(beanName, id);
    }

    public DivTag getFormGroup(LabelTag label, Tag field, String helpText, String extraCssClasses) {
        var formGroup = new DivTag()
                .cssClass(CssClasses.start("w3-margin-top").add(extraCssClasses).get())
                .child(label)
                .child(field);

        if (helpText != null)
            formGroup.child(getHelperBlock(helpText));

        return formGroup;
    }

    @Override
    public DivTag getCheckboxField(HFHParameters params) {
        DivTag innerPart = getCheckbox(params);  // TODO: reconsider variable name

        if (params.hasGroupExtraCssClasses())
            innerPart.changeCssClasses(
                    CssClasses.start("checkbox w3-margin-top").add(params.getGroupExtraCssClasses()).get());

        return innerPart;
    }

    @Override
    protected InputTag getCheckboxTag(HFHParameters params) {
        var checkbox = super.getCheckboxTag(params);

        if (params.hasTagExtraCssClasses())
            checkbox.changeCssClasses(CssClasses.start("w3-check w3-margin-top").add(params.getTagExtraCssClasses()).get());
        else
            checkbox.cssClass("w3-check w3-margin-top");

        return checkbox;
    }

    @Override
    public DivTag getSelectField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        FormElement formElement;
        if (params.isReadonly())
            formElement = getSelectReadOnlyFormElement(params, fieldId);
        else
            formElement = getSelectReadWriteFormElement(params, fieldId);

        if (params.isRequired() && useRequiredInHtml())
            formElement.required();
        if (params.isDisabled())
            formElement.disabled();

        return getFormGroup(label, formElement, params.getHelpText(), params.getGroupExtraCssClasses());
    }

    @Override
    protected SelectTag getSelectTag(String name, String id, String extraCssClasses) {
        return new SelectTag(name)
                .cssClass(CssClasses.start("w3-select").add(extraCssClasses).get())
                .id(id);
    }

    @Override
    protected InputTag getInputTag(
            InputTag.InputType type,
            String id,
            String name,
            String value,
            boolean readonly,
            String extraCssClasses)
    {
        return getInputTag(type, id, name, value, readonly, "w3-input w3-border", extraCssClasses);
    }

    @Override
    protected TextareaTag getTextAreaTag(String id, String name, String value, String extraCssClasses) {
        return new TextareaTag(value)
                .cssClass(CssClasses.start("w3-input w3-border").add(extraCssClasses).get())
                .id(id)
                .name(name);
    }

    @Override
    public Tag getFileField(HFHParameters params) {
        throw new UnsupportedOperationException("getFileField(HFHParameters)"); // TODO: IMPLEMENT
    }

    @Override
    public ButtonTag getSubmitButtonTag(HFHParameters params, String extraCssClasses) {
        var actualParams = new HFHParameters(params)
                .setButtonType(ButtonTag.ButtonType.SUBMIT)
                .setFunctionName("submit")
                .setCssClasses(
                        CssClasses.start("w3-button w3-teal w3-margin-top w3-margin-bottom")
                                .add(extraCssClasses).get());

        if (!Strings.isEmpty(extraCssClasses))
            actualParams.setCssClasses(extraCssClasses);

        return getButtonTag(actualParams);
    }

}
