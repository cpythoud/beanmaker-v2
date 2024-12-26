package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public abstract class OperationsBaseServlet extends BeanMakerBaseServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        var requestParameters = new HttpRequestParameters(request, getUploadedFileSizeThreshold());
        switch (getOperation(requestParameters)) {
            case GET_FORM -> {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(getForm(requestParameters));
            }
            case SUBMIT_FORM -> {
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(submitForm(requestParameters));
            }
            case DELETE_BEAN -> {
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(deleteBean(requestParameters));
            }
            case CHANGE_ORDER -> {
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(changeOrder(requestParameters));
            }
            default ->
                    throw new AssertionError("Unidentified operation: " + getOperation(requestParameters));
        }
    }

    protected String getForm(HttpRequestParameters requestParameters) throws ServletException {
        return getFormPrefix(requestParameters) +
                getHTMLView(getBeanId(requestParameters, "id"), requestParameters).getHtmlForm() +
                getFormSuffix(requestParameters);
    }

    protected String getFormPrefix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected String getFormSuffix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected abstract DbBeanHTMLViewInterface getHTMLView(long id, HttpRequestParameters requestParameters)
            throws ServletException;

    protected abstract long getSubmitBeanId(HttpRequestParameters requestParameters);

    protected abstract DbBeanLanguage getLanguage(HttpSession session);

    protected String submitForm(HttpRequestParameters requestParameters) throws ServletException {
        return processBean(requestParameters, getHTMLView(getSubmitBeanId(requestParameters), requestParameters));
    }

    protected String processBean(HttpRequestParameters parameters, DbBeanHTMLViewInterface htmlView) {
        htmlView.setAllFields(parameters);

        if (htmlView.isDataOK()) {
            htmlView.updateDB();
            return getJsonOk(htmlView);
        }

        return getStartJsonErrors() + ErrorMessage.toJson(htmlView.getErrorMessages()) + " }";
    }

    protected String getJsonOk(DbBeanHTMLViewInterface htmlView) {
        return getJsonOk();
    }

    protected String deleteBean(HttpRequestParameters requestParameters) {
        return deleteBean(getInstance(getBeanId(requestParameters, "id")));
    }

    protected abstract DbBeanEditor getInstance(long id);

    protected String changeOrder(HttpRequestParameters requestParameters) throws ServletException {
        long id = getBeanId(requestParameters, "id");

        ChangeOrderDirection direction = getChangeOrderDirection(requestParameters);
        long companionId = Strings.getLongVal(requestParameters.getValue("companionId"));

        return changeOrder(id, direction, companionId, requestParameters);
    }

    protected abstract String changeOrder(
            long id,
            ChangeOrderDirection direction,
            long companionId,
            HttpRequestParameters requestParameters
    );

}
