package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.ButtonTag;
import org.jcodegen.html.CData;
import org.jcodegen.html.DivTag;
import org.jcodegen.html.FormElement;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.HtmlCodeFragment;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.LabelTag;
import org.jcodegen.html.OptgroupTag;
import org.jcodegen.html.OptionTag;
import org.jcodegen.html.PTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.SpanTag;
import org.jcodegen.html.Tag;
import org.jcodegen.html.TextareaTag;
import org.jcodegen.html.util.CssClasses;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Bootstrap3HTMLFormHelper implements HtmlFormHelper {

    public static final String SELECT_OFF_GROUP_MAP_KEY = "__ROOT__";

    private String notRequiredExtension = "";
    private String requiredExtension = " *";
    private boolean useRequiredInHtml = true;

    private String htmlFormAction = null;
    private boolean htmlFormMultipart = false;

    private String defaultEncoding = null;


    private boolean inline = false;
    private boolean inlineWithoutLabels = false;
    private boolean horizontal = false;
    private boolean readonly = false;
    private String readonlyExtension = "-readonly";

    private String horizontalSizeShift = "sm";
    private int horizontalLabelWidth = 4;
    private int horizontalFieldWidth = 8;


    private InputTag.InputType inputTypeForDateFields = null;
    private String cssClassForDateFields = null;
    private InputTag.InputType inputTypeForTimeFields = null;
    private String cssClassForTimeFields = null;
    private InputTag.InputType inputTypeForNumberFields = null;
    private String cssClassForNumberFields = null;

    private String cssClassForFileFields = "file";
    private String uploadButtonLabel = "Upload";
    private String uploadButtonCssClasses = "btn btn-default btn-sm";
    private String uploadNoFileLabel = "(no file)";
    private String uploadFilenameDisplayCssClasses = "file-display";
    private String uploadRemoveFileLabel = "";
    private String uploadRemoveFileCssClasses = "remove-file glyphicons glyphicons-remove text-danger";
    private String uploadRemoveFileTitle = "Remove file";

    private String extraFormCssClasses = null;

    private String readonlyPostfix = "-ro";

    private boolean displayROFilesAsLinks = false;

    private String readonlyFormCssClass = null;


    public String getNotRequiredExtension() {
        return notRequiredExtension;
    }

    public void setNotRequiredExtension(String notRequiredExtension) {
        this.notRequiredExtension = notRequiredExtension;
    }

    public String getRequiredExtension() {
        return requiredExtension;
    }

    public void setRequiredExtension(String requiredExtension) {
        this.requiredExtension = requiredExtension;
    }

    public boolean useRequiredInHtml() {
        return useRequiredInHtml;
    }

    public void useRequiredInHtml(boolean useRequiresInHtml) {
        this.useRequiredInHtml = useRequiresInHtml;
    }

    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

    public boolean htmlFormMultipart() {
        return htmlFormMultipart;
    }

    public void htmlFormMultipart(boolean htmlFormMultipart) {
        this.htmlFormMultipart = htmlFormMultipart;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }


    public void setInline(boolean inline) {
        this.inline = inline;
        if (inline)
            horizontal = false;
        else
            inlineWithoutLabels = false;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInlineWithoutLabels(boolean inlineWithoutLabels) {
        this.inlineWithoutLabels = inlineWithoutLabels;
        if (inlineWithoutLabels) {
            horizontal = false;
            inline = true;
        }
    }

    public boolean isInlineWithoutLabels() {
        return inlineWithoutLabels;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
        if (horizontal) {
            inline = false;
            inlineWithoutLabels = false;
        }
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonlyExtension(String readonlyExtension) {
        this.readonlyExtension = readonlyExtension;
    }

    public String getReadonlyExtension() {
        return readonlyExtension;
    }

    private static List<String> BOOTSTRAP_SIZES = Arrays.asList("xs", "sm", "md", "lg");

    public void setHorizontalFormParameters(
            String horizontalSizeShift,
            int horizontalLabelWidth,
            int horizontalFieldWidth)
    {
        if (!BOOTSTRAP_SIZES.contains(horizontalSizeShift))
            throw new IllegalArgumentException("Unknown Boostrap size: " + horizontalSizeShift);
        if (horizontalLabelWidth < 0 || horizontalLabelWidth > 12)
            throw new IllegalArgumentException("Illegal column index for label width: " + horizontalLabelWidth);
        if (horizontalFieldWidth < 0 || horizontalFieldWidth > 12)
            throw new IllegalArgumentException("Illegal column index for field width:" + horizontalFieldWidth);
        if (horizontalLabelWidth + horizontalFieldWidth > 12)
            throw new IllegalArgumentException(
                    "Column count for label + field is incorrect (> 12): " + horizontalLabelWidth
                            + " + " + horizontalFieldWidth
                            + " = " + (horizontalLabelWidth + horizontalFieldWidth));

        this.horizontalSizeShift = horizontalSizeShift;
        this.horizontalLabelWidth = horizontalLabelWidth;
        this.horizontalFieldWidth = horizontalFieldWidth;

        setHorizontal(true);
    }

    public String getHorizontalSizeShift() {
        return horizontalSizeShift;
    }

    public int getHorizontalLabelWidth() {
        return horizontalLabelWidth;
    }

    public int getHorizontalFieldWidth() {
        return horizontalFieldWidth;
    }

    public void setInputTypeForDateFields(InputTag.InputType inputTypeForDateFields) {
        this.inputTypeForDateFields = inputTypeForDateFields;
    }

    public InputTag.InputType getInputTypeForDateFields() {
        return inputTypeForDateFields;
    }

    public void setCssClassForDateFields(String cssClassForDateFields) {
        this.cssClassForDateFields = cssClassForDateFields;
    }

    public String getCssClassForDateFields() {
        return cssClassForDateFields;
    }

    public void setInputTypeForTimeFields(InputTag.InputType inputTypeForTimeFields) {
        this.inputTypeForTimeFields = inputTypeForTimeFields;
    }

    public InputTag.InputType getInputTypeForTimeFields() {
        return inputTypeForTimeFields;
    }

    public void setCssClassForTimeFields(String cssClassForTimeFields) {
        this.cssClassForTimeFields = cssClassForTimeFields;
    }

    public String getCssClassForTimeFields() {
        return cssClassForTimeFields;
    }

    public void setInputTypeForNumberFields(InputTag.InputType inputTypeForNumberFields) {
        this.inputTypeForNumberFields = inputTypeForNumberFields;
    }

    public InputTag.InputType getInputTypeForNumberFields() {
        return inputTypeForNumberFields;
    }

    public void setCssClassForNumberFields(String cssClassForNumberFields) {
        this.cssClassForNumberFields = cssClassForNumberFields;
    }

    public String getCssClassForNumberFields() {
        return cssClassForNumberFields;
    }

    public void setCssClassForFileFields(String cssClassForFileFields) {
        this.cssClassForFileFields = cssClassForFileFields;
    }

    public void setUploadButtonLabel(String uploadButtonLabel) {
        this.uploadButtonLabel = uploadButtonLabel;
    }

    public void setUploadButtonCssClasses(String uploadButtonCssClasses) {
        this.uploadButtonCssClasses = uploadButtonCssClasses;
    }

    public void setUseRequiredInHtml(boolean useRequiredInHtml) {
        this.useRequiredInHtml = useRequiredInHtml;
    }

    public void setUploadNoFileLabel(String uploadNoFileLabel) {
        this.uploadNoFileLabel = uploadNoFileLabel;
    }

    public void setUploadFilenameDisplayCssClasses(String uploadFilenameDisplayCssClasses) {
        this.uploadFilenameDisplayCssClasses = uploadFilenameDisplayCssClasses;
    }

    public void setUploadRemoveFileLabel(String uploadRemoveFileLabel) {
        this.uploadRemoveFileLabel = uploadRemoveFileLabel;
    }

    public void setUploadRemoveFileCssClasses(String uploadRemoveFileCssClasses) {
        this.uploadRemoveFileCssClasses = uploadRemoveFileCssClasses;
    }

    public void setUploadRemoveFileTitle(String uploadRemoveFileTitle) {
        this.uploadRemoveFileTitle = uploadRemoveFileTitle;
    }

    public String getCssClassForFileFields() {
        return cssClassForFileFields;
    }

    public String getUploadButtonLabel() {
        return uploadButtonLabel;
    }

    public String getUploadButtonCssClasses() {
        return uploadButtonCssClasses;
    }

    public String getUploadNoFileLabel() {
        return uploadNoFileLabel;
    }

    public String getUploadFilenameDisplayCssClasses() {
        return uploadFilenameDisplayCssClasses;
    }

    public String getUploadRemoveFileLabel() {
        return uploadRemoveFileLabel;
    }

    public String getUploadRemoveFileCssClasses() {
        return uploadRemoveFileCssClasses;
    }

    public String getUploadRemoveFileTitle() {
        return uploadRemoveFileTitle;
    }

    public void setExtraFormCssClasses(String extraFormCssClasses) {
        this.extraFormCssClasses = extraFormCssClasses;
    }

    public String getExtraFormCssClasses() {
        return extraFormCssClasses;
    }

    public void setReadonlyPostfix(String readonlyPostfix) {
        this.readonlyPostfix = readonlyPostfix;
    }

    public String getReadonlyPostfix() {
        return readonlyPostfix;
    }

    public void setDisplayROFilesAsLinks(boolean displayROFilesAsLinks) {
        this.displayROFilesAsLinks = displayROFilesAsLinks;
    }

    public boolean isDisplayROFilesAsLinks() {
        return displayROFilesAsLinks;
    }

    public void setReadonlyFormCssClass(String readonlyFormCssClass) {
        this.readonlyFormCssClass = readonlyFormCssClass;
    }

    public String getReadonlyFormCssClass() {
        return readonlyFormCssClass;
    }

    protected String getFormCssClasses(String beanName) {
        StringBuilder formCssClasses = new StringBuilder();

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

    protected void resetFormTypeFlags() {
        inline = false;
        inlineWithoutLabels = false;
        horizontal = false;
    }

    @Override
    public FormTag getForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public FormTag getInlineForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();
        inline = true;

        return form.cssClass(getFormCssClasses(beanName));
    }

    public FormTag getInlineFormWithoutLabels(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();
        inline = true;
        inlineWithoutLabels = true;

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public FormTag getHorizontalForm(String beanName, long id) {
        FormTag form = getFormTag(beanName, id);

        resetFormTypeFlags();
        horizontal = true;

        return form.cssClass(getFormCssClasses(beanName));
    }

    @Override
    public InputTag getHiddenSubmitInput(String beanName, long id) {
        return new InputTag(InputTag.InputType.HIDDEN).name("submitted" + beanName).value(Long.toString(id));
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

        if (params.isRequired() && useRequiredInHtml)
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

    protected String getFieldId(String field, long idBean, boolean readonly) {
        return getFieldId(field, idBean, null, readonly);
    }

    protected String getFieldId(String field, long idBean, String idNamePostfix, boolean readonly) {
        String readonlyIndication = readonly ? readonlyPostfix : "";

        if (Strings.isEmpty(idNamePostfix))
            return field + "_" + idBean + readonlyIndication;

        return field + "_" + idNamePostfix + "_" + idBean + readonlyIndication;
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
        if (type == InputTag.InputType.DATE)
            return new InputTag(inputTypeForDateFields == null ? type : inputTypeForDateFields)
                    .cssClass(CssClasses.start("form-control").add(cssClassForDateFields, !readonly).add(extraCssClasses).get())
                    .id(id).name(name).value(value);

        if (type == InputTag.InputType.TIME)
            return new InputTag(readonly || inputTypeForTimeFields == null ? type : inputTypeForTimeFields)
                    .cssClass(CssClasses.start("form-control").add(cssClassForTimeFields).add(extraCssClasses).get())
                    .id(id).name(name).value(value);

        if (type == InputTag.InputType.NUMBER)
            return new InputTag(inputTypeForNumberFields == null ? type : inputTypeForNumberFields)
                    .cssClass(CssClasses.start("form-control").add(cssClassForNumberFields).add(extraCssClasses).get())
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
                .cssClass(CssClasses.start("form-control").add(extraCssClasses).get())
                .id(id)
                .name(name)
                .value(value);
    }

    public DivTag getFormGroup() {
        return new DivTag().cssClass("form-group");
    }

    public DivTag getFormGroup(String extraCssClasses) {
        return new DivTag().cssClass(CssClasses.start("form-group").add(extraCssClasses).get());
    }

    public DivTag getFormGroup(LabelTag label, Tag field) {
        return getFormGroup(label, field, null);
    }

    public DivTag getFormGroup(LabelTag label, Tag field, String helpText) {
        return getFormGroup(label, field, helpText, null);
    }

    public DivTag getFormGroup(LabelTag label, Tag field, String helpText, String extraCssClasses) {
        DivTag formGroup =
                new DivTag().cssClass(CssClasses.start("form-group").add(extraCssClasses).get())
                        .child(label);

        if (horizontal) {
            DivTag formElements =
                    new DivTag().cssClass(getHorizontalFieldClass())
                            .child(field);
            if (helpText != null)
                formElements.child(getHelperBlock(helpText));
            formGroup.child(formElements);
        } else {
            formGroup.child(field);
            if (helpText != null)
                formGroup.child(getHelperBlock(helpText));
        }

        return formGroup;
    }

    protected Tag getHelperBlock(String helpText) {
        return new PTag(helpText).cssClass("helpBlock");
    }

    protected LabelTag getLabel(String fieldLabel, boolean required) {
        return getLabel(fieldLabel, null, required);
    }

    protected LabelTag getLabel(String fieldLabel, String fieldId, boolean required) {
        return getLabel(fieldLabel, fieldId, required, null);
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

        StringBuilder cssClasses = new StringBuilder();
        if (inlineWithoutLabels)
            cssClasses.append("sr-only");
        if (horizontal)
            cssClasses.append(getHorizontalLabelClasses());
        if (!Strings.isEmpty(extraCssClasses)) {
            if (cssClasses.length() > 0)
                cssClasses.append(" ");
            cssClasses.append(extraCssClasses);
        }
        if (cssClasses.length() > 0)
            label.cssClass(cssClasses.toString());

        return label;
    }

    protected String getLabelText(String fieldLabel, boolean required) {
        if (required && !readonly)
            return fieldLabel + requiredExtension;

        return fieldLabel + notRequiredExtension;
    }

    protected String getHorizontalLabelClasses() {
        return getHorizontalLabelClass() + " control-label";
    }

    public String getHorizontalLabelClass() {
        return "col-" + horizontalSizeShift + "-" + horizontalLabelWidth;
    }

    public String getHorizontalFieldClass() {
        return "col-" + horizontalSizeShift + "-" + horizontalFieldWidth;
    }

    @Override
    public ButtonTag getSubmitButtonTag(HFHParameters params) {
        return getSubmitButtonTag(params, null);
    }

    public ButtonTag getSubmitButtonTag(HFHParameters params, String extraCssClasses) {
        return getButtonTag(
                new HFHParameters(params)
                        .setButtonType(ButtonTag.ButtonType.SUBMIT)
                        .setFunctionName("submit")
                        .setCssClasses("btn btn-default" + (extraCssClasses == null ? "" : " " + extraCssClasses)));
    }

    public ButtonTag getButtonTag(HFHParameters params) {
        var button = new ButtonTag(params.getButtonType())
                .id(getHtmlId(params.getBeanName() + "_" + params.getFunctionName(), params.getIdBean()))
                .cssClass(params.getCssClasses());

        params.getButtonActionSpan().ifPresent(button::child);
        button.child(new CData(params.getButtonLabel()));

        return button;
    }

    @Override
    public Tag getSubmitButton(HFHParameters params) {
        ButtonTag submit = getSubmitButtonTag(params);

        if (params.isDisabled())
            submit.disabled();

        if (horizontal)
            return getFormGroup().child(new DivTag().cssClass(getHorizontalFieldClassesWithOffset()).child(submit));

        return submit;
    }

    public String getHorizontalFieldClassesWithOffset() {
        return "col-" + horizontalSizeShift
                + "-offset-" + horizontalLabelWidth
                + " col-" + horizontalSizeShift
                + "-" + horizontalFieldWidth;
    }

    @Override
    public DivTag getSelectField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        FormElement formElement;
        if (params.isReadonly())
            formElement = getReadOnlyFormElement(params, fieldId);
        else
            formElement = getReadWriteFormElement(params, fieldId);

        if (params.isRequired() && useRequiredInHtml)
            formElement.required();
        if (params.isDisabled())
            formElement.disabled();

        return getFormGroup(label, formElement, params.getHelpText(), params.getGroupExtraCssClasses());
    }

    private FormElement getReadOnlyFormElement(HFHParameters params, String fieldId) {
        String value = "";
        if (!params.getSelected().equals("0"))
            for (IdNamePair pair: params.getSelectPairs())
                if (pair.getId().equals(params.getSelected()))
                    value = pair.getName();

        return getInputTag(InputTag.InputType.TEXT, fieldId, params.getField(), value, true, params.getTagExtraCssClasses())
                .readonly();
    }

    private FormElement getReadWriteFormElement(HFHParameters params, String fieldId) {
        SelectTag select = getSelectTag(params.getField(), fieldId, params.getTagExtraCssClasses());

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

    private void addPairs(Tag selectOrGroup, List<IdNamePair> pairs, String selected) {
        for (IdNamePair pair: pairs) {
            OptionTag optionTag = new OptionTag(pair.getName(), pair.getId());
            if (pair.isDisabled())
                optionTag.disabled();
            if (pair.getId().equals(selected))
                optionTag.selected();
            selectOrGroup.child(optionTag);
        }
    }

    protected SelectTag getSelectTag(String name, String id, String extraCssClasses) {
        return new SelectTag(name)
                .cssClass(CssClasses.start("form-control").add(extraCssClasses).get())
                .id(id);
    }

    @Override
    public DivTag getTextAreaField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), fieldId, params.isRequired(), params.getLabelExtraCssClasses());

        TextareaTag textarea = getTextAreaTag(fieldId, params.getField(), params.getValue(), params.getTagExtraCssClasses());
        if (params.isRequired() && useRequiredInHtml)
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
        return new TextareaTag(value)
                .cssClass(CssClasses.start("form-control").add(extraCssClasses).get())
                .id(id)
                .name(name);
    }

    @Override
    public DivTag getCheckboxField(HFHParameters params) {
        DivTag innerPart = getCheckbox(params);

        if (horizontal)
            return getFormGroup(params.getGroupExtraCssClasses())
                    .child(new DivTag().cssClass(getHorizontalFieldClassesWithOffset()).child(innerPart));

        if (params.hasGroupExtraCssClasses())
            innerPart.changeCssClasses(CssClasses.start("checkbox").add(params.getGroupExtraCssClasses()).get());

        return innerPart;
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

        return checkbox;
    }

    protected String getHtmlId(String beanName, long id) {
        return beanName + (readonly ? readonlyExtension : "") + "_" + id;
    }

    @Override
    public DivTag getFileField(HFHParameters params) {
        if (params.isReadonly())
            return getReadOnlyFileField(params);

        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());
        LabelTag label = getLabel(params.getFieldLabel(), null, params.isRequired(), params.getLabelExtraCssClasses());

        LabelTag uploadButton = new LabelTag(uploadButtonLabel, fieldId).cssClass(uploadButtonCssClasses);

        InputTag input =
                getInputTag(
                        InputTag.InputType.FILE,
                        fieldId,
                        params.getField(),
                        params.getCurrentFile(),
                        params.isReadonly(),
                        params.getTagExtraCssClasses());

        SpanTag filenameDisplay =
                new SpanTag(params.hasCurrentFile() ? params.getCurrentFile() : uploadNoFileLabel)
                        .cssClass(uploadFilenameDisplayCssClasses)
                        .id("display_" + fieldId);

        SpanTag removeFileButton =
                new SpanTag(uploadRemoveFileLabel)
                        .cssClass(uploadRemoveFileCssClasses + (params.hasCurrentFile() ? "" : " hidden"))
                        .id("remove_" + fieldId)
                        .data("fileinput", fieldId)
                        .title(uploadRemoveFileTitle);

        if (params.isRequired() && useRequiredInHtml && !params.hasCurrentFile())
            input.required();
        if (params.isDisabled())
            input.disabled();
        if (params.isReadonly())
            input.readonly();

        return getFileFormGroup(label, uploadButton, input, filenameDisplay, removeFileButton, params.getGroupExtraCssClasses());
    }

    protected DivTag getReadOnlyFileField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), true);
        LabelTag label = getLabel(params.getFieldLabel(), null, params.isRequired(), params.getLabelExtraCssClasses());

        Tag input;
        if (displayROFilesAsLinks && params.hasCurrentFileLink())
            input = params.getCurrentFileLink();
        else {
            input = getInputTag(
                    InputTag.InputType.TEXT,
                    fieldId,
                    params.getField(),
                    params.getCurrentFile(),
                    true,
                    params.getTagExtraCssClasses());
        }

        if (params.isDisabled())
            input.disabled();

        return getFormGroup(label, input, null, params.getGroupExtraCssClasses());
    }

    private DivTag getFileFormGroup(
            LabelTag label,
            LabelTag uploadButton,
            InputTag input,
            SpanTag filenameDisplay,
            SpanTag removeFileButton,
            String extraCssClasses)
    {
        DivTag formGroup =
                new DivTag()
                        .cssClass(CssClasses.start("form-group").add(extraCssClasses).get())
                        .child(label);

        if (horizontal) {
            DivTag formElements =
                    new DivTag().cssClass(getHorizontalFieldClass())
                            .child(uploadButton)
                            .child(input)
                            .child(filenameDisplay)
                            .child(removeFileButton);
            formGroup.child(formElements);
        } else {
            formGroup.child(uploadButton)
                    .child(input)
                    .child(filenameDisplay)
                    .child(removeFileButton);
        }

        return formGroup;
    }

    @Override
    public Tag getHiddenInfo(String field, long idBean, String value) {
        return new InputTag(InputTag.InputType.HIDDEN)
                .name(field)
                .id(getFieldId(field, idBean, false))
                .value(value);
    }

    public DivTag getBooleanRadiosField(HFHParameters params) {
        LabelTag label = getLabel(params.getFieldLabel(), null, params.isRequired(), params.getLabelExtraCssClasses());

        DivTag wrapper = new DivTag();
        wrapper.child(getBooleanRadioButton(params, true));
        wrapper.child(getBooleanRadioButton(params, false));

        return getFormGroup(label, wrapper, null, params.getGroupExtraCssClasses());
    }

    // TODO: split into 2 or more functions for clarity and separation of concerns
    private LabelTag getBooleanRadioButton(HFHParameters params, boolean positiveValue) {
        String fieldLabel;
        String fieldValue;
        if (positiveValue) {
            fieldLabel = params.getYesLabel();
            fieldValue = params.getYesValue();
        } else {
            fieldLabel = params.getNoLabel();
            fieldValue = params.getNoValue();
        }

        HtmlCodeFragment buttonInside = new HtmlCodeFragment();
        InputTag radioButton =
                new InputTag(InputTag.InputType.RADIO)
                        .name(params.getField())
                        .value(fieldValue);
        if (params.hasTagExtraCssClasses())
            radioButton.cssClass(params.getTagExtraCssClasses());
        if (params.isChecked() && positiveValue)
            radioButton.checked();
        if (!params.isChecked() && !positiveValue && !params.areRadioButtonsUnchecked())
            radioButton.checked();
        if (params.isDisabled())
            radioButton.disabled();
        if (params.isReadonly())
            radioButton.readonly()
                    .attribute("onclick", "return false;")
                    .attribute("onkeydown", "return false;");
        if (!params.isAutocomplete())
            radioButton.attribute("autocomplete", "off");
        buttonInside.addTag(radioButton);
        buttonInside.addTag(new CData("&nbsp;" + fieldLabel));

        return new LabelTag().cssClass("radio-inline").addCodeFragment(buttonInside);
    }

    @Override
    public DivTag getLabelFormField(
            String value,
            DbBeanLanguage dbBeanLanguage,
            boolean required,
            HFHParameters params)
    {
        HFHParameters actualParameters = new HFHParameters(params);
        String iso = dbBeanLanguage.getCapIso();

        actualParameters.setField(params.getField() + iso);
        actualParameters.setValue(value);
        actualParameters.setFieldLabel(params.getFieldLabel() + " " + iso);
        actualParameters.setRequired(required);

        if (params.isAsTextArea())
            return getTextAreaField(actualParameters);

        actualParameters.setInputType(InputTag.InputType.TEXT);
        return getTextField(actualParameters);
    }

    public DivTag getTextLabelField(HFHParameters params) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());

        return getFormGroup(
                getLabel(params.getFieldLabel(), fieldId, params.isRequired()),
                getTextLabelTag(fieldId, params.getValue()));
    }

    public DivTag getTextLabelField(HFHParameters params, Tag adHocRepresentation) {
        String fieldId = getFieldId(params.getField(), params.getIdBean(), params.isReadonly());

        return getFormGroup(
                getLabel(params.getFieldLabel(), fieldId, params.isRequired()),
                adHocRepresentation.id(fieldId));
    }

    protected Tag getTextLabelTag(String id, String value) {
        return new SpanTag(value).cssClass("form-control").id(id);
    }

    public DivTag getFractionField(HFHParameters params) {
        LabelTag label = getLabel(params.getFieldLabel(), null, params.isRequired(), params.getLabelExtraCssClasses());

        String altCSSClasses = params.getTagExtraCssClasses() == null ? "" : params.getTagExtraCssClasses();
        HFHParameters numeratorParams = params.getNumeratorParameters();
        HFHParameters denominatorParams = params.getDenominatorParameters();
        DivTag wrapper = new DivTag();
        wrapper.child(getInputTag(
                InputTag.InputType.NUMBER,
                getFieldId(numeratorParams.getField(), numeratorParams.getIdBean(), numeratorParams.isReadonly()),
                numeratorParams.getField(),
                numeratorParams.getValue(),
                numeratorParams.isReadonly())
                .style("width: " + numeratorParams.getFractionFieldSize() + "ex")
                .changeCssClasses(altCSSClasses));
        wrapper.child(new CData("&nbsp;/&nbsp;"));
        wrapper.child(getInputTag(
                InputTag.InputType.NUMBER,
                getFieldId(denominatorParams.getField(), denominatorParams.getIdBean(), denominatorParams.isReadonly()),
                denominatorParams.getField(),
                denominatorParams.getValue(),
                denominatorParams.isReadonly())
                .style("width: " + denominatorParams.getFractionFieldSize() + "ex")
                .changeCssClasses(altCSSClasses));

        return getFormGroup(label, wrapper, null, params.getGroupExtraCssClasses());
    }
}
