package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public abstract class OperationsBaseServlet extends BeanMakerBaseServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (getOperation(request)) {
            case GET_FORM:
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(getForm(request));
                break;
            case SUBMIT_FORM:
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(submitForm(request));
                break;
            case DELETE_BEAN:
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(deleteBean(request));
                break;
            case CHANGE_ORDER:
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(changeOrder(request));
                break;
            default:
                throw new AssertionError("Unidentified operation: " + getOperation(request));
        }
    }

    protected String getForm(HttpServletRequest request) throws ServletException {
        return getFormPrefix(request) +
                getHTMLView(getBeanId(request, "id"), request).getHtmlForm() +
                getFormSuffix(request);
    }

    protected String getFormPrefix(HttpServletRequest request) {
        return "";
    }

    private String getFormSuffix(HttpServletRequest request) {
        return "";
    }

    protected abstract DbBeanHTMLViewInterface getHTMLView(long id, HttpServletRequest request) throws ServletException;

    protected abstract long getSubmitBeanId(HttpServletRequest request);

    protected abstract DbBeanLanguage getLanguage(HttpSession session);

    protected String submitForm(HttpServletRequest request) throws ServletException {
        return processBean(new HttpRequestParameters(request), getHTMLView(getSubmitBeanId(request), request));
    }

    protected String processBean(HttpRequestParameters parameters, DbBeanHTMLViewInterface htmlView) {
        htmlView.setAllFields(parameters);

        if (htmlView.isDataOK()) {
            htmlView.updateDB();
            return getJsonOk();
        }

        return getStartJsonErrors() + ErrorMessage.toJson(htmlView.getErrorMessages()) + " }";
    }

    protected String deleteBean(HttpServletRequest request) {
        return deleteBean(getInstance(getBeanId(request, "id")));
    }

    protected abstract DbBeanEditor getInstance(long id);

    protected String changeOrder(HttpServletRequest request) throws ServletException {
        long id = getBeanId(request, "id");

        ChangeOrderDirection direction = getChangeOrderDirection(request);
        long companionId = Strings.getLongVal(request.getParameter("companionId"));

        return changeOrder(id, direction, companionId);
    }

    protected abstract String changeOrder(long id, ChangeOrderDirection direction, long companionId);

}
