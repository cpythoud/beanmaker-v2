package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.HTMLText;

import org.jcodegen.html.FormTag;
import org.jcodegen.html.Tag;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public abstract class BaseHTMLView extends BaseEditableView implements DbBeanHTMLViewInterface {

    protected HtmlFormHelper htmlFormHelper = new Bootstrap3HTMLFormHelper();
    protected ServletContext servletContext = null;

    private final DbBeanEditor editor;

    protected String formName;
    protected boolean horizontal = false;
    protected boolean readonly = false;

    protected int uploadedFileSizeThreshold = HttpRequestParameters.DEFAULT_UPLOADED_FILE_SIZE_THRESHOLD;

    public BaseHTMLView(DbBeanEditor editor, DbBeanLocalization dbBeanLocalization) {
        super(dbBeanLocalization);
        this.editor = editor;
        formName = dbBeanLocalization.getBeanClassName();
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected String getParameter(ServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null)
            return null;

        return HTMLText.escapeEssentialHTMLtext(value);
    }

    @Override
    public void resetId() {
        editor.resetId();
    }

    @Override
    public void setId(long id) {
        editor.setId(id);
    }

    @Override
    public long getId() {
        return editor.getId();
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public String getHtmlForm() {
        return getHtmlFormTag().toString();
    }

    protected FormTag getFormStart() {
        if (horizontal)
            return htmlFormHelper.getHorizontalForm(formName, editor.getId());

        return htmlFormHelper.getForm(formName, editor.getId());
    }

    protected Tag getFormElementsContainer(Tag form) {
        return htmlFormHelper.getFormElementsContainer(form);
    }

    protected void composeErrorContainer(Tag form) {
        htmlFormHelper.addErrorMessagesContainer(form, editor.getId());
    }

    protected void composeHiddenSubmitField(Tag form) {
        form.child(htmlFormHelper.getHiddenSubmitInput(formName, editor.getId()));
    }

    protected void composeAdditionalHtmlFormFields(Tag form) { }

    protected Tag getFormButtonsContainer(Tag form) {
        return htmlFormHelper.getFormButtonsContainer(form);
    }

    protected void composeButtons(Tag form) {
        composeSubmitButton(form);
        composeResetButton(form);
    }

    protected HFHParameters getSubmitButtonParameters() {
        HFHParameters params = new HFHParameters();
        params.setBeanName(formName);
        params.setIdBean(editor.getId());
        params.setButtonLabel(dbBeanLocalization.getLabel("submit_button"));
        return params;
    }

    protected void composeSubmitButton(Tag form) {
        form.child(htmlFormHelper.getSubmitButton(getSubmitButtonParameters()));
    }

    protected void composeResetButton(Tag form) { }

    protected FormTag finalizeForm(FormTag form, Tag formElementsContainer) {
        if (form != formElementsContainer)
            form.child(formElementsContainer);

        return form;
    }

    protected FormTag finalizeForm(FormTag form, Tag formElementsContainer, Tag formButtonsContainer) {
        if (form != formElementsContainer)
            form.child(formElementsContainer);
        if (form != formButtonsContainer)
            form.child(formButtonsContainer);

        return form;
    }

    @Override
    public void setAllFields(ServletRequest request) {
        setAllFields((HttpServletRequest) request);
    }

    @Override
    public void setAllFields(HttpServletRequest request) {
        setAllFields(new HttpRequestParameters(request, uploadedFileSizeThreshold));
    }

    @Override
    public boolean isDataOK() {
        boolean ok = editor.isDataOK();
        errorMessages.clear();
        errorMessages.addAll(editor.getErrorMessages());
        warningMessages.clear();
        warningMessages.addAll(editor.getWarningMessages());

        return ok;
    }

    @Override
    public void updateDB() {
        editor.updateDB();
    }

    @Override
    public void reset() {
        editor.reset();
    }

    @Override
    public void fullReset() {
        editor.fullReset();
    }

    @Override
    public void delete() {
        editor.delete();
    }

}
