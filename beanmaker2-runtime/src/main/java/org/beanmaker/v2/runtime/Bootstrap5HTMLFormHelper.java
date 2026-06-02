package org.beanmaker.v2.runtime;

import org.beanmaker.v2.codegen.html.ButtonTag;
import org.beanmaker.v2.codegen.html.CData;
import org.beanmaker.v2.codegen.html.DivTag;
import org.beanmaker.v2.codegen.html.FormElement;
import org.beanmaker.v2.codegen.html.FormTag;
import org.beanmaker.v2.codegen.html.InputTag;
import org.beanmaker.v2.codegen.html.LabelTag;
import org.beanmaker.v2.codegen.html.SelectTag;
import org.beanmaker.v2.codegen.html.Tag;
import org.beanmaker.v2.codegen.html.TextareaTag;
import org.beanmaker.v2.codegen.html.util.CssClasses;

import org.beanmaker.v2.util.Strings;

public class Bootstrap5HTMLFormHelper extends AbstractHtmlFormHelper {

    private String verticalFormElementSpacing = "mb-3";
    private boolean formInModale = true;
    private boolean floatingLabels = false;
    private boolean buttonsSeparated = false;

    public String getVerticalFormElementSpacing() {
        return verticalFormElementSpacing;
    }

    public void setVerticalFormElementSpacing(String verticalFormElementSpacing) {
        this.verticalFormElementSpacing = verticalFormElementSpacing;
    }

    public boolean isFormInModale() {
        return formInModale;
    }

    public void setFormInModale(boolean formInModale) {
        this.formInModale = formInModale;
    }

    public boolean isFloatingLabels() {
        return floatingLabels;
    }

    public void setFloatingLabels(boolean floatingLabels) {
        this.floatingLabels = floatingLabels;
    }

    public boolean areButtonsSeparated() {
        return buttonsSeparated;
    }

    public void setButtonsSeparated(boolean buttonsSeparated) {
        this.buttonsSeparated = buttonsSeparated;
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
    public Tag<?> getCheckboxField(HFHParameters params) {
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
                getFieldId(params.getField(), params.getIdBean(), params.getIdNameSuffix(), params.isReadonly())
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
    public Tag<?> getSelectField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        FormElement<?> formElement;
        if (params.isReadonly())
            formElement = getSelectReadOnlyFormElement(params, fieldId);
        else
            formElement = getSelectReadWriteFormElement(params, fieldId);

        if (params.isRequired() && useRequiredInHtml())
            formElement.required();
        if (params.isDisabled())
            formElement.disabled();

        var formGroup = getFormGroup(label, formElement, params.getHelpText(), params.getGroupExtraCssClasses());
        if (isFloatingLabels())
            formGroup.appendCssClasses("form-floating");
        return formGroup;
    }

    @Override
    protected SelectTag getSelectTag(String name, String id, String extraCssClasses) {
        return new SelectTag(name)
                .cssClass("form-select " + extraCssClasses)
                .id(id);
    }

    @Override
    public Tag<?> getFileField(HFHParameters params) {
        throw new UnsupportedOperationException("getFileField(HFHParameters)"); // TODO: IMPLEMENT
    }

    @Override
    public DivTag getFormGroup(LabelTag label, Tag<?> field, String helpText, String extraCssClasses) {
        String cssClasses = CssClasses.start(getVerticalFormElementSpacing()).add(extraCssClasses).get();
        var formGroup = new DivTag().cssClass(cssClasses);
        if (isFloatingLabels()) {
            formGroup.child(field);
            formGroup.child(label);
        } else {
            formGroup.child(label);
            formGroup.child(field);
        }

        if (helpText != null)
            formGroup.child(getHelperBlock(helpText));

        return formGroup;
    }

    @Override
    protected Tag<?> getHelperBlock(String helpText) {
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
    public DivTag getTextAreaField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        TextareaTag textarea = getTextAreaTag(fieldId, params.getField(), params.getValue(), params.getTagExtraCssClasses());
        if (params.isRequired() && useRequiredInHtml())
            textarea.required();
        if (params.getPlaceholder() != null)
            textarea.placeholder(params.getPlaceholder());
        else if (isFloatingLabels())
            textarea.placeholder(params.getFieldLabel()); // ! placeholder required for floating labels
        if (params.isDisabled())
            textarea.disabled();
        if (params.isReadonly())
            textarea.readonly();
        if (params.getMaxLength() > 0)
            textarea.maxlength(params.getMaxLength());

        return getFormGroup(
                label,
                textarea,
                params.getHelpText(),
                isFloatingLabels() ?
                        params.mergeGroupExtraCssClasses("form-floating") :
                        params.getGroupExtraCssClasses());
    }

    @Override
    protected TextareaTag getTextAreaTag(String id, String name, String value, String extraCssClasses) {
        return new  TextareaTag(value)
                .id(id)
                .name(name)
                .style("height: 100px;")
                .cssClass(CssClasses.start("form-control").add(extraCssClasses).get());
    }

    @Override
    public DivTag getTextField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label =
                getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        InputTag input =
                getInputTag(
                        params.getInputType(),
                        fieldId,
                        params.getField(),
                        params.getValue(),
                        params.isReadonly(),
                        params.getTagExtraCssClasses());

        if (params.isRequired() && useRequiredInHtml())
            input.required();
        if (params.getPlaceholder() != null)
            input.placeholder(params.getPlaceholder());
        else if (isFloatingLabels())
            input.placeholder(params.getFieldLabel()); // ! placeholder required for floating labels
        if (params.isDisabled())
            input.disabled();
        if (params.isReadonly())
            input.readonly();
        if (!params.isAutocomplete())
            input.attribute("autocomplete", "off");
        if (params.getMaxLength() > 0)
            input.maxlength(params.getMaxLength());

        return getFormGroup(
                label,
                input,
                params.getHelpText(),
                isFloatingLabels() ?
                        params.mergeGroupExtraCssClasses("form-floating") :
                        params.getGroupExtraCssClasses());
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

    @Override
    public Tag<?> getFormElementsContainer(Tag<?> form) {
        if (isFormInModale() && !areButtonsSeparated())
            return new DivTag().cssClass("modal-body");

        return super.getFormElementsContainer(form);
    }

    @Override
    public Tag<?> getFormButtonsContainer(Tag<?> form) {
        if (isFormInModale())
            return new DivTag().cssClass("modal-footer");

        return super.getFormButtonsContainer(form);
    }

    @Override
    public void addErrorMessagesContainer(Tag<?> form, String formName, long idBean) {
        form.child(HtmlFormHelper.getDefaultErrorMessageContainer(formName, idBean));
    }

    @Override
    public ButtonTag getButtonTag(HFHParameters params) {
        var button = super.getButtonTag(params);
        button.appendCssClasses("btn btn-primary");
        if (buttonsSeparated)
            button.form(getHtmlId(params.getBeanName(), params.getIdBean()));
        return button;
    }

    public static ButtonTag getDefaultCancelButton(String label) {
        return new ButtonTag(ButtonTag.ButtonType.BUTTON, label)
                .cssClass("btn btn-secondary")
                .data("bs-dismiss", "modal");
    }

}
