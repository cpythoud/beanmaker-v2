package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.ButtonTag;
import org.jcodegen.html.CData;
import org.jcodegen.html.DivTag;
import org.jcodegen.html.FormElement;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.LabelTag;
import org.jcodegen.html.OptgroupTag;
import org.jcodegen.html.OptionTag;
import org.jcodegen.html.PTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.Tag;
import org.jcodegen.html.TextareaTag;
import org.jcodegen.html.util.CssClasses;

import java.util.List;
import java.util.Map;

public abstract class AbstractHtmlFormHelper implements HtmlFormHelper {

    private boolean inline = false;
    private boolean horizontal = false;

    private boolean readonly = false;
    private String readonlyExtension = "-readonly";
    private String readonlyFormCssClass = null;
    private String readonlyPostfix = "-ro";

    private String extraFormCssClasses = null;

    private boolean htmlFormMultipart = false;

    private String notRequiredExtension = "";
    private String requiredExtension = " *";
    private boolean useRequiredInHtml = true;

    private InputTag.InputType inputTypeForDateFields = null;
    private String cssClassForDateFields = null;
    private InputTag.InputType inputTypeForTimeFields = null;
    private String cssClassForTimeFields = null;
    private InputTag.InputType inputTypeForNumberFields = null;
    private String cssClassForNumberFields = null;
    private String cssClassForFileFields = "file";

    protected void resetFormTypeFlags() {
        inline = false;
        horizontal = false;
    }

    protected String getFormCssClasses(String beanName, String... otherCssClasses) {
        var formCssClasses = new StringBuilder();

        formCssClasses.append(beanName).append("-form");
        if (readonly) {
            formCssClasses.append(readonlyExtension);
            if (readonlyFormCssClass != null)
                formCssClasses.append(" ").append(readonlyFormCssClass);
        }

        if (inline)
            formCssClasses.append(" form-inline");
        if (horizontal)
            formCssClasses.append(" form-horizontal");

        if (extraFormCssClasses != null)
            formCssClasses.append(" ").append(extraFormCssClasses);

        for (String otherCssClass : otherCssClasses)
            formCssClasses.append(" ").append(otherCssClass);

        return formCssClasses.toString();
    }

    protected FormTag getFormTag(String beanName, long id) {
        FormTag form =
                new FormTag()
                        .role("form")
                        .id(getHtmlId(beanName, id))
                        .name(beanName + (readonly ? readonlyExtension : ""))
                        .method(FormTag.Method.POST);

        if (htmlFormMultipart)
            return form.enctype(FormTag.EncodingType.MULTIPART);

        return form;
    }

    protected String getHtmlId(String beanName, long id) {
        return beanName + (readonly ? readonlyExtension : "") + "_" + id;
    }

    @Override
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @Override
    public boolean isInline() {
        return inline;
    }

    @Override
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public boolean isHorizontal() {
        return horizontal;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonlyExtension(String readonlyExtension) {
        this.readonlyExtension = readonlyExtension;
    }

    @Override
    public String getReadonlyExtension() {
        return readonlyExtension;
    }

    @Override
    public void setReadonlyFormCssClass(String readonlyFormCssClass) {
        this.readonlyFormCssClass = readonlyFormCssClass;
    }

    @Override
    public String getReadonlyFormCssClass() {
        return readonlyFormCssClass;
    }

    @Override
    public void setReadonlyPostfix(String readonlyPostfix) {
        this.readonlyPostfix = readonlyPostfix;
    }

    @Override
    public String getReadonlyPostfix() {
        return readonlyPostfix;
    }

    @Override
    public void setExtraFormCssClasses(String extraFormCssClasses) {
        this.extraFormCssClasses = extraFormCssClasses;
    }

    @Override
    public String getExtraFormCssClasses() {
        return extraFormCssClasses;
    }

    @Override
    public boolean htmlFormMultipart() {
        return htmlFormMultipart;
    }

    @Override
    public void htmlFormMultipart(boolean htmlFormMultipart) {
        this.htmlFormMultipart = htmlFormMultipart;
    }

    @Override
    public Tag getHiddenSubmitInput(String beanName, long id) {
        return new InputTag(InputTag.InputType.HIDDEN).name("submitted" + beanName).value(Long.toString(id));
    }

    @Override
    public String getNotRequiredExtension() {
        return notRequiredExtension;
    }

    @Override
    public void setNotRequiredExtension(String notRequiredExtension) {
        this.notRequiredExtension = notRequiredExtension;
    }

    @Override
    public String getRequiredExtension() {
        return requiredExtension;
    }

    @Override
    public void setRequiredExtension(String requiredExtension) {
        this.requiredExtension = requiredExtension;
    }

    @Override
    public boolean useRequiredInHtml() {
        return useRequiredInHtml;
    }

    @Override
    public void useRequiredInHtml(boolean useRequiresInHtml) {
        this.useRequiredInHtml = useRequiresInHtml;
    }

    @Override
    public void setInputTypeForDateFields(InputTag.InputType inputTypeForDateFields) {
        this.inputTypeForDateFields = inputTypeForDateFields;
    }

    @Override
    public InputTag.InputType getInputTypeForDateFields() {
        return inputTypeForDateFields;
    }

    @Override
    public void setCssClassForDateFields(String cssClassForDateFields) {
        this.cssClassForDateFields = cssClassForDateFields;
    }

    @Override
    public String getCssClassForDateFields() {
        return cssClassForDateFields;
    }

    @Override
    public void setInputTypeForTimeFields(InputTag.InputType inputTypeForTimeFields) {
        this.inputTypeForTimeFields = inputTypeForTimeFields;
    }

    @Override
    public InputTag.InputType getInputTypeForTimeFields() {
        return inputTypeForTimeFields;
    }

    @Override
    public void setCssClassForTimeFields(String cssClassForTimeFields) {
        this.cssClassForTimeFields = cssClassForTimeFields;
    }

    @Override
    public String getCssClassForTimeFields() {
        return cssClassForTimeFields;
    }

    @Override
    public void setInputTypeForNumberFields(InputTag.InputType inputTypeForNumberFields) {
        this.inputTypeForNumberFields = inputTypeForNumberFields;
    }

    @Override
    public InputTag.InputType getInputTypeForNumberFields() {
        return inputTypeForNumberFields;
    }

    @Override
    public void setCssClassForNumberFields(String cssClassForNumberFields) {
        this.cssClassForNumberFields = cssClassForNumberFields;
    }

    @Override
    public String getCssClassForNumberFields() {
        return cssClassForNumberFields;
    }

    @Override
    public void setCssClassForFileFields(String cssClassForFileFields) {
        this.cssClassForFileFields = cssClassForFileFields;
    }

    @Override
    public String getCssClassForFileFields() {
        return cssClassForFileFields;
    }

    protected DivTag getCheckbox(HFHParameters params) {
        LabelTag labelTag = new LabelTag()
                .child(getCheckboxTag(params))
                .child(new CData(" " + params.getFieldLabel()));
        if (params.hasLabelExtraCssClasses())
            labelTag.cssClass(params.getLabelExtraCssClasses());

        return new DivTag()
                .cssClass("checkbox")
                .child(labelTag);
    }

    protected InputTag getCheckboxTag(HFHParameters params) {
        InputTag checkbox =
                new InputTag(InputTag.InputType.CHECKBOX)
                        .name(params.getField())
                        .id(getFieldId(params.getField(), params.getIdBean(), params.getIdNameSuffix(), params.isReadonly()));

        setCheckboxParameters(checkbox, params);

        return checkbox;
    }

    protected void setCheckboxParameters(InputTag checkbox, HFHParameters params) {
        if (params.hasTagExtraCssClasses())
            checkbox.cssClass(params.getTagExtraCssClasses());
        if (params.isChecked())
            checkbox.checked();
        if (params.isDisabled())
            checkbox.disabled();
        if (params.isReadonly())
            checkbox.readonly()
                    .attribute("onclick", "return false;")
                    .attribute("onkeydown", "return false;");
        if (!params.isAutocomplete())
            checkbox.attribute("autocomplete", "off");
        if (params.getCheckboxValue() != null)
            checkbox.value(params.getCheckboxValue());
    }

    protected String getFieldId(String field, long idBean, boolean readonly) {
        return getFieldId(field, idBean, null, readonly);
    }

    protected String getFieldId(String field, long idBean, String idNamePostfix, boolean readonly) {
        String readonlyIndication = readonly ? readonlyPostfix : "";

        if (Strings.isEmpty(idNamePostfix))
            return field + "_" + idBean + readonlyIndication;

        return field + "_" + idNamePostfix + "_" + idBean + readonlyIndication;
    }

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

        var cssClasses = new StringBuilder();
        if (!Strings.isEmpty(extraCssClasses)) {
            if (!cssClasses.isEmpty())
                cssClasses.append(" ");
            cssClasses.append(extraCssClasses);
        }
        if (!cssClasses.isEmpty())
            label.cssClass(cssClasses.toString());

        return label;
    }

    protected String getLabelText(String fieldLabel, boolean required) {
        if (required && !isReadonly())
            return fieldLabel + requiredExtension;

        return fieldLabel + notRequiredExtension;
    }

    protected FormElement getSelectReadOnlyFormElement(HFHParameters params, String fieldId) {
        String value = "";
        if (!params.getSelected().equals("0"))
            for (IdNamePair pair: params.getSelectPairs())
                if (pair.getId().equals(params.getSelected()))
                    value = pair.getName();

        return getInputTag(InputTag.InputType.TEXT, fieldId, params.getField(), value, true, params.getTagExtraCssClasses())
                .readonly();
    }

    protected FormElement getSelectReadWriteFormElement(HFHParameters params, String fieldId) {
        var select = getSelectTag(params.getField(), fieldId, params.getTagExtraCssClasses());

        if (params.hasOptionGroupSelectData()) {
            Map<String, List<IdNamePair>> pairMap = params.getOptionGroupSelectPairs();
            for (String groupName: pairMap.keySet()) {
                if (groupName.equals(SELECT_OFF_GROUP_MAP_KEY))
                    addPairs(select, pairMap.get(groupName), params.getSelected());
                else {
                    OptgroupTag group = new OptgroupTag(groupName);
                    addPairs(group, pairMap.get(groupName), params.getSelected());
                    select.child(group);
                }
            }
        } else
            addPairs(select, params.getSelectPairs(), params.getSelected());

        return select;
    }

    protected void addPairs(Tag selectOrGroup, List<IdNamePair> pairs, String selected) {
        for (IdNamePair pair: pairs) {
            OptionTag optionTag = new OptionTag(pair.getName(), pair.getId());
            if (pair.isDisabled())
                optionTag.disabled();
            if (pair.getId().equals(selected))
                optionTag.selected();
            selectOrGroup.child(optionTag);
        }
    }

    protected InputTag getInputTag(
            InputTag.InputType type,
            String id,
            String name,
            String value,
            boolean readonly)
    {
        return getInputTag(type, id, name, value, readonly, null);
    }

    protected InputTag getInputTag(
            InputTag.InputType type,
            String id,
            String name,
            String value,
            boolean readonly,
            String extraCssClasses)
    {
        return getInputTag(type, id, name, value, readonly, "", extraCssClasses);
    }

    protected InputTag getInputTag(
            InputTag.InputType type,
            String id,
            String name,
            String value,
            boolean readonly,
            String baseInputCssClass,
            String extraCssClasses)
    {
        if (type == InputTag.InputType.DATE)
            return new InputTag(inputTypeForDateFields == null ? type : inputTypeForDateFields)
                    .cssClass(CssClasses.start(baseInputCssClass).add(cssClassForDateFields, !readonly).add(extraCssClasses).get())
                    .id(id).name(name).value(value);

        if (type == InputTag.InputType.TIME)
            return new InputTag(readonly || inputTypeForTimeFields == null ? type : inputTypeForTimeFields)
                    .cssClass(CssClasses.start(baseInputCssClass).add(cssClassForTimeFields).add(extraCssClasses).get())
                    .id(id).name(name).value(value);

        if (type == InputTag.InputType.NUMBER)
            return new InputTag(inputTypeForNumberFields == null ? type : inputTypeForNumberFields)
                    .cssClass(CssClasses.start(baseInputCssClass).add(cssClassForNumberFields).add(extraCssClasses).get())
                    .id(id).name(name).value(value);

        if (type == InputTag.InputType.FILE) {
            InputTag fileInput = new InputTag(InputTag.InputType.FILE);
            if (!Strings.isEmpty(cssClassForFileFields))
                fileInput.cssClass(CssClasses.start(cssClassForFileFields).add(extraCssClasses).get());
            if (!Strings.isEmpty(value))
                fileInput.placeholder(value);
            return fileInput.id(id).name(name);
        }

        return new InputTag(type)
                .cssClass(CssClasses.start(baseInputCssClass).add(extraCssClasses).get())
                .id(id)
                .name(name)
                .value(value);
    }

    protected SelectTag getSelectTag(String name, String id, String extraCssClasses) {
        return new SelectTag(name)
                .cssClass(extraCssClasses)
                .id(id);
    }

    public DivTag getFormGroup() {
        return new DivTag();
    }

    public DivTag getFormGroup(String extraCssClasses) {
        return new DivTag().cssClass(extraCssClasses);
    }

    public DivTag getFormGroup(LabelTag label, Tag field) {
        return getFormGroup(label, field, null);
    }

    public DivTag getFormGroup(LabelTag label, Tag field, String helpText) {
        return getFormGroup(label, field, helpText, null);
    }

    public DivTag getFormGroup(LabelTag label, Tag field, String helpText, String extraCssClasses) {
        var formGroup = extraCssClasses == null ? new DivTag() : new DivTag().cssClass(extraCssClasses);
        formGroup.child(label);

        formGroup.child(field);
        if (helpText != null)
            formGroup.child(getHelperBlock(helpText));

        return formGroup;
    }

    protected Tag getHelperBlock(String helpText) {
        return new PTag(helpText).cssClass("helpBlock");
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
        if (params.isDisabled())
            textarea.disabled();
        if (params.isReadonly())
            textarea.readonly();
        if (params.getMaxLength() > 0)
            textarea.maxlength(params.getMaxLength());

        return getFormGroup(label, textarea, params.getHelpText(), params.getGroupExtraCssClasses());
    }

    protected TextareaTag getTextAreaTag(String id, String name, String value, String extraCssClasses) {
        var textarea = new  TextareaTag(value)
                .id(id)
                .name(name);

        if (!Strings.isEmpty(extraCssClasses))
            textarea.cssClass(extraCssClasses);

        return textarea;
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
        if (params.isDisabled())
            input.disabled();
        if (params.isReadonly())
            input.readonly();
        if (!params.isAutocomplete())
            input.attribute("autocomplete", "off");
        if (params.getMaxLength() > 0)
            input.maxlength(params.getMaxLength());

        return getFormGroup(label, input, params.getHelpText(), params.getGroupExtraCssClasses());
    }

    @Override
    public DivTag getLabelFormField(
            String value,
            DbBeanLanguage dbBeanLanguage,
            boolean required,
            HFHParameters params)
    {
        HFHParameters actualParameters = new HFHParameters(params);
        String tag = dbBeanLanguage.getTag();

        actualParameters.setField(params.getField() + tag);
        actualParameters.setValue(value);
        actualParameters.setFieldLabel(params.getFieldLabel() + " " + tag);
        actualParameters.setRequired(required);

        if (params.isAsTextArea())
            return getTextAreaField(actualParameters);

        actualParameters.setInputType(InputTag.InputType.TEXT);
        return getTextField(actualParameters);
    }

    @Override
    public Tag getSubmitButton(HFHParameters params) {
        ButtonTag submit = getSubmitButtonTag(params);

        if (params.isDisabled())
            submit.disabled();

        return submit;
    }

    @Override
    public ButtonTag getSubmitButtonTag(HFHParameters params) {
        return getSubmitButtonTag(params, null);
    }

    @Override
    public ButtonTag getSubmitButtonTag(HFHParameters params, String extraCssClasses) {
        var actualParams = new HFHParameters(params)
                .setButtonType(ButtonTag.ButtonType.SUBMIT)
                .setFunctionName("submit");

        if (!Strings.isEmpty(extraCssClasses))
            actualParams.setCssClasses(extraCssClasses);

        return getButtonTag(actualParams);
    }

    @Override
    public ButtonTag getButtonTag(HFHParameters params) {
        var button = new ButtonTag(params.getButtonType())
                .id(getHtmlId(params.getBeanName() + "_" + params.getFunctionName(), params.getIdBean()))
                .cssClass(params.getCssClasses());

        params.getButtonActionSpan().ifPresent(button::child);
        button.child(new CData(params.getButtonLabel()));

        return button;
    }

    @Override
    public Tag getHiddenInfo(String field, long idBean, String value) {
        return new InputTag(InputTag.InputType.HIDDEN)
                .name(field)
                .id(getFieldId(field, idBean, false))
                .value(value);
    }

}
