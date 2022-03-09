package org.beanmaker.v2.runtime;

import org.jcodegen.html.FormTag;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

public interface DbBeanHTMLViewInterface extends DbBeanViewInterface {

    List<ErrorMessage> getErrorMessages();

    String getHtmlForm();

    FormTag getHtmlFormTag();

    void setAllFields(ServletRequest request);

    void setAllFields(HttpServletRequest request);

    void setAllFields(HttpRequestParameters parameters);

    boolean isDataOK();

    void updateDB();

    void reset();

    void fullReset();

    void delete();

}
