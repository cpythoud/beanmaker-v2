package org.beanmaker.v2.runtime;

import org.beanmaker.v2.codegen.html.FormTag;
import org.beanmaker.v2.codegen.html.Tag;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

public interface DbBeanHTMLViewInterface extends DbBeanViewInterface {

    List<ErrorMessage> getErrorMessages();
    List<WarningMessage> getWarningMessages();

    default boolean hasWarnings() {
        return !getWarningMessages().isEmpty();
    }

    String getHtmlForm();

    FormTag getHtmlFormTag();

    String getStandaloneFormButtons();

    Tag getStandaloneFormButtonsTag();

    void setAllFields(ServletRequest request);

    void setAllFields(HttpServletRequest request);

    void setAllFields(HttpRequestParameters parameters);

    boolean isDataOK();

    void updateDB();

    void reset();

    void fullReset();

    void delete();

}
