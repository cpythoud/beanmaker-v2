package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.CData;
import org.jcodegen.html.DivTag;
import org.jcodegen.html.FormElement;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.LabelTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.Tag;

import org.jcodegen.html.util.CssClasses;

// !! This implementation for Bootstrap 5 should be considered alpha software !!
// !! It will be augmented and stabilized as work on XnLab continues !!

public class Bootstrap5HTMLFormHelper extends AbstractHtmlFormHelper {

    private String verticalFormElementSpacing = "mb-3";

    public String getVerticalFormElementSpacing() {
        return verticalFormElementSpacing;
    }

    public void setVerticalFormElementSpacing(String verticalFormElementSpacing) {
        this.verticalFormElementSpacing = verticalFormElementSpacing;
    }

    @Override
    public FormTag getForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public FormTag getHorizontalForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();
        setHorizontal(true);

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public FormTag getInlineForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();
        setInline(true);

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public Tag getCheckboxField(HFHParameters params) {
        var elementSpacing = getVerticalFormElementSpacing();
        var field = new DivTag();
        if (!Strings.isEmpty(elementSpacing))
            field.cssClass(elementSpacing);

        DivTag innerPart = getCheckbox(params);

        // TODO: implement horizontal forms
        /*if (isHorizontal())
            return getFormGroup(params.getGroupExtraCssClasses())
                    .child(new DivTag().cssClass(getHorizontalFieldClassesWithOffset()).child(innerPart));*/

        // ????
        if (params.hasGroupExtraCssClasses())
            innerPart.appendCssClasses(params.getGroupExtraCssClasses());

        return field.child(innerPart);
    }

    @Override
    protected DivTag getCheckbox(HFHParameters params) {
        var labelTag = new LabelTag(
                params.getFieldLabel(),
                getFieldId(params.getField(), params.getIdBean(), params.isReadonly())
        ).cssClass("form-check-label");

        if (params.hasLabelExtraCssClasses())
            labelTag.appendCssClasses(params.getLabelExtraCssClasses());

        return new DivTag()
                .cssClass("form-check")
                .child(getCheckboxTag(params))
                .child(labelTag);
    }

    @Override
    protected InputTag getCheckboxTag(HFHParameters params) {
        InputTag checkbox =
                new InputTag(InputTag.InputType.CHECKBOX)
                        .name(params.getField())
                        .cssClass("form-check-input")
                        .id(getFieldId(
                                params.getField(), params.getIdBean(), params.getIdNameSuffix(), params.isReadonly()));

        setCheckboxParameters(checkbox, params);

        return checkbox;
    }

    @Override
    public Tag getSelectField(HFHParameters params) {
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
                .cssClass("form-select " + extraCssClasses)
                .id(id);
    }

    @Override
    public Tag getFileField(HFHParameters params) {
        throw new UnsupportedOperationException("getFileField(HFHParameters)"); // TODO: IMPLEMENT
    }

    @Override
    public DivTag getFormGroup(LabelTag label, Tag field, String helpText, String extraCssClasses) {
        String cssClasses = CssClasses.start(getVerticalFormElementSpacing()).add(extraCssClasses).get();
        var formGroup = new DivTag().cssClass(cssClasses);
        formGroup.child(label);

        formGroup.child(field);
        if (helpText != null)
            formGroup.child(getHelperBlock(helpText));

        return formGroup;
    }

    @Override
    protected Tag getHelperBlock(String helpText) {
        return new DivTag().cssClass("form-text").child(new CData(helpText));
    }

    @Override
    protected LabelTag getLabel(
            String fieldLabel,
            String fieldId,
            boolean required,
            String extraCssClasses)
    {
        LabelTag label;
        if (fieldId == null)
            label = new LabelTag(getLabelText(fieldLabel, required));
        else
            label = new LabelTag(getLabelText(fieldLabel, required), fieldId);

        return label.cssClass(CssClasses.start("form-label").add(extraCssClasses).get());
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
        return getInputTag(type, id, name, value, readonly, "form-control", extraCssClasses);
    }

}
