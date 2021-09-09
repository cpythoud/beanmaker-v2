package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.HTMLText;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

public class BaseHTMLView extends BaseEditableView {

    protected boolean captchaControl = false;
    protected String captchaValue = "";
    protected String captchaControlValue = "";

    protected HtmlFormHelper htmlFormHelper = new HtmlFormHelper();
    protected ServletContext servletContext = null;

    public BaseHTMLView(String resourceBundleName) {
        super(resourceBundleName);
    }

    public void setCaptchaControl(boolean captchaControl) {
        this.captchaControl = captchaControl;
    }

    public void setCaptchaValue(String captchaValue) {
        this.captchaValue = captchaValue;
    }

    public void setCaptchaControlValue(String captchaControlValue) {
        this.captchaControlValue = captchaControlValue;
    }

    public String getInvalidCaptchaErrorMessage() {
        return resourceBundle.getString("invalid_captcha");

    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected String getParameter(ServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null)
            return null;

        return HTMLText.escapeEssentialHTMLtext(value);
    }
}
