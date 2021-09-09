package org.beanmaker.v2.runtime;

import org.jcodegen.html.FormTag;

import javax.servlet.ServletRequest;

import java.util.List;
import java.util.Locale;

public interface DbBeanHTMLViewInterface extends DbBeanViewInterface {

    void setLocale(Locale locale);

    List<ErrorMessage> getErrorMessages();

    String getHtmlForm();

    FormTag getHtmlFormTag();

    void setAllFields(ServletRequest request);

    void setAllFields(HttpRequestParameters parameters);

    boolean isDataOK();

    void updateDB();

    void setUpdateDB(String dummy);

    void reset();

    void fullReset();

    void setReset(String dummy);

    void setFullReset(String dummy);

    void delete();

    void setDelete(String dummy);
}
