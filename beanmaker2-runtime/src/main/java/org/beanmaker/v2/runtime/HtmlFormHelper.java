package org.beanmaker.v2.runtime;

import org.jcodegen.html.ButtonTag;
import org.jcodegen.html.DivTag;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.Tag;

public interface HtmlFormHelper {

    String SELECT_OFF_GROUP_MAP_KEY = "__ROOT__";

    FormTag getForm(String beanName, long id);
    FormTag getHorizontalForm(String beanName, long id);
    FormTag getInlineForm(String beanName, long id);

    void setInline(boolean inline);
    boolean isInline();

    void setHorizontal(boolean horizontal);
    boolean isHorizontal();

    void setReadonly(boolean readonly);
    boolean isReadonly();
    void setReadonlyExtension(String readonlyExtension);
    String getReadonlyExtension();
    void setReadonlyFormCssClass(String readonlyFormCssClass);
    String getReadonlyFormCssClass();
    void setReadonlyPostfix(String readonlyPostfix);
    String getReadonlyPostfix();

    void setExtraFormCssClasses(String extraFormCssClasses);
    String getExtraFormCssClasses();

    boolean htmlFormMultipart();
    void htmlFormMultipart(boolean htmlFormMultipart);

    String getNotRequiredExtension();
    void setNotRequiredExtension(String notRequiredExtension);
    String getRequiredExtension();
    void setRequiredExtension(String requiredExtension);
    boolean useRequiredInHtml();
    void useRequiredInHtml(boolean useRequiresInHtml);

    void setInputTypeForDateFields(InputTag.InputType inputTypeForDateFields);
    InputTag.InputType getInputTypeForDateFields();
    void setCssClassForDateFields(String cssClassForDateFields);
    String getCssClassForDateFields();
    void setInputTypeForTimeFields(InputTag.InputType inputTypeForTimeFields);
    InputTag.InputType getInputTypeForTimeFields();
    void setCssClassForTimeFields(String cssClassForTimeFields);
    String getCssClassForTimeFields();
    void setInputTypeForNumberFields(InputTag.InputType inputTypeForNumberFields);
    InputTag.InputType getInputTypeForNumberFields();
    void setCssClassForNumberFields(String cssClassForNumberFields);
    String getCssClassForNumberFields();
    void setCssClassForFileFields(String cssClassForFileFields);
    String getCssClassForFileFields();

    Tag getHiddenSubmitInput(String beanName, long id);

    Tag getCheckboxField(HFHParameters params);
    Tag getSelectField(HFHParameters params);
    Tag getTextAreaField(HFHParameters params);
    Tag getTextField(HFHParameters params);
    Tag getLabelFormField(String value, DbBeanLanguage dbBeanLanguage, boolean required, HFHParameters params);
    Tag getFileField(HFHParameters params);

    Tag getSubmitButton(HFHParameters params);

    ButtonTag getSubmitButtonTag(HFHParameters params);
    ButtonTag getSubmitButtonTag(HFHParameters params, String extraCssClasses);
    ButtonTag getButtonTag(HFHParameters params);

    Tag getHiddenInfo(String field, long idBean, String value);

    default Tag getFormElementsContainer(Tag form) {
        return form;
    }

    default Tag getFormButtonsContainer(Tag form) {
        return form;
    }

    default void addErrorMessagesContainer(Tag form, long idBean) { }

    static Tag getDefaultErrorMessageContainer(long idBean) {
        return new DivTag().id("error_messages_" + idBean);
    }

}
