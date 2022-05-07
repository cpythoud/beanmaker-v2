package org.beanmaker.v2.runtime;

import org.jcodegen.html.ButtonTag;
import org.jcodegen.html.FormTag;
import org.jcodegen.html.Tag;

public interface HtmlFormHelper {

    FormTag getForm(String beanName, long id);
    FormTag getHorizontalForm(String beanName, long id);
    FormTag getInlineForm(String beanName, long id);

    void setReadonly(boolean readonly);

    Tag getHiddenSubmitInput(String beanName, long id);

    Tag getCheckboxField(HFHParameters params);
    Tag getSelectField(HFHParameters params);
    Tag getTextAreaField(HFHParameters params);
    Tag getTextField(HFHParameters params);
    Tag getLabelFormField(String value, DbBeanLanguage dbBeanLanguage, boolean required, HFHParameters params);
    Tag getFileField(HFHParameters params);

    Tag getSubmitButton(HFHParameters params);

    ButtonTag getSubmitButtonTag(HFHParameters params);

    Tag getHiddenInfo(String field, long idBean, String value);

}
