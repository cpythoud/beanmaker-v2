package org.beanmaker.v2.runtime;

import org.beanmaker.v2.codegen.html.FormTag;
import org.beanmaker.v2.codegen.html.Tag;

import org.beanmaker.v2.util.HTMLText;

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
    protected boolean displayButtonsSeparately = false;
    protected boolean useSid = false;

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
    public void setIdOrSid(String idOrSid) {
        editor.setIdOrSid(idOrSid);
    }

    @Override
    public long getId() {
        return editor.getId();
    }

    @Override
    public String getSid() {
        return editor.getSid();
    }

    @Override
    public String getIdOrSid() {
        return editor.getIdOrSid();
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

    public void setDisplayButtonsSeparately(boolean displayButtonsSeparately) {
        this.displayButtonsSeparately = displayButtonsSeparately;
    }

    public void useSid(boolean useSid) {
        this.useSid = useSid;
    }

    @Override
    public String getHtmlForm() {
        return getHtmlFormTag().toString();
    }

    protected FormTag getFormStart() {
        String idOrSid = SidManager.zeroOrSid(useSid ? editor.getSid() : editor.getId() + "");
        if (horizontal)
            return htmlFormHelper.getHorizontalForm(formName, idOrSid);

        return htmlFormHelper.getForm(formName, idOrSid);
    }

    protected Tag<?> getFormElementsContainer(Tag<?> form) {
        return htmlFormHelper.getFormElementsContainer(form);
    }

    protected void composeErrorContainer(Tag<?> form) {
        String idOrSid = useSid ? editor.getSid() : editor.getId() + "";
        htmlFormHelper.addErrorMessagesContainer(form, formName, SidManager.zeroOrSid(idOrSid));
    }

    protected void composeHiddenSubmitField(Tag<?> form) {
        String idOrSid = SidManager.zeroOrSid(useSid ? editor.getSid() : editor.getId() + "");
        form.child(htmlFormHelper.getHiddenSubmitInput(formName, idOrSid));
    }

    protected void composeAdditionalHtmlFormFields(Tag<?> form) { }

    protected Tag<?> getFormButtonsContainer(Tag<?> form) {
        return htmlFormHelper.getFormButtonsContainer(form);
    }

    protected void composeButtons(Tag<?> form) {
        composeSubmitButton(form);
        composeResetButton(form);
    }

    protected HFHParameters getSubmitButtonParameters() {
        HFHParameters params = new HFHParameters();
        params.setBeanName(formName);
        params.setBeanIdOrSid(useSid ? editor.getSid() : editor.getId() + "");
        params.setButtonLabel(dbBeanLocalization.getLabel("submit_button"));
        return params;
    }

    protected void composeSubmitButton(Tag<?> form) {
        form.child(htmlFormHelper.getSubmitButton(getSubmitButtonParameters()));
    }

    protected void composeResetButton(Tag<?> form) { }

    protected FormTag finalizeForm(FormTag form, Tag<?> formElementsContainer) {
        if (form != formElementsContainer)
            form.child(formElementsContainer);

        return form;
    }

    protected FormTag finalizeForm(FormTag form, Tag<?> formElementsContainer, Tag<?> formButtonsContainer) {
        if (form != formElementsContainer)
            form.child(formElementsContainer);
        if (form != formButtonsContainer)
            form.child(formButtonsContainer);

        return form;
    }

    @Override
    public String getStandaloneFormButtons() {
        return getStandaloneFormButtonsTag().toString();
    }

    @Override
    public Tag<?> getStandaloneFormButtonsTag() {
        var buttons = getStandaloneFormButtonsContainer();
        composeButtons(buttons);
        return buttons;
    }

    protected Tag<?> getStandaloneFormButtonsContainer() {
        return htmlFormHelper.getStandaloneFormButtonsContainer();
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
