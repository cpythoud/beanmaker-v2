package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Types;

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
            case DISPLAY_TABLE -> {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(displayTable(requestParameters));
            }
            case GET_FORM_BUTTONS -> {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(getFormButtons(requestParameters));
            }
            case NEW_VERSION -> {
                response.setContentType("text/json; charset=UTF-8");
                response.getWriter().println(newVersion(requestParameters));
            }
            default ->
                    throw new AssertionError("Unidentified operation: " + getOperation(requestParameters));
        }
    }

    protected String getForm(HttpRequestParameters requestParameters) throws ServletException {
        return getFormPrefix(requestParameters) +
                getHTMLView(getBeanIdOrSid(requestParameters, "id"), requestParameters).getHtmlForm() +
                getFormSuffix(requestParameters);
    }

    protected String getFormPrefix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected String getFormSuffix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected abstract DbBeanHTMLViewInterface getHTMLView(String idOrSid, HttpRequestParameters requestParameters)
            throws ServletException;

    protected abstract String getSubmitBeanIdOrSid(HttpRequestParameters requestParameters);

    protected abstract DbBeanLanguage getLanguage(HttpSession session);

    protected String submitForm(HttpRequestParameters requestParameters) throws ServletException {
        return processBean(requestParameters, getHTMLView(getSubmitBeanIdOrSid(requestParameters), requestParameters));
    }

    protected String processBean(HttpRequestParameters parameters, DbBeanHTMLViewInterface htmlView) {
        htmlView.setAllFields(parameters);

        if (htmlView.isDataOK()) {
            htmlView.updateDB();
            return getJsonStatusObject("ok").put("id", htmlView.getIdOrSid()).toString();
        }

        return getJsonStatusObject("errors")
                .put("errors", ErrorMessage.toJsonArray(htmlView.getErrorMessages()))
                .toString();
    }

    protected String getJsonOk(DbBeanHTMLViewInterface htmlView) {
        if (htmlView.hasWarnings())
            return getJsonStatusObject("ok")
                    .put("warnings", WarningMessage.toJsonArray(htmlView.getErrorMessages()))
                    .toString();

        return getJsonOk();
    }

    protected String deleteBean(HttpRequestParameters requestParameters) {
        return deleteBean(getInstance(getBeanIdOrSid(requestParameters, "id")));
    }

    protected abstract DbBeanEditor getInstance(long id);

    protected abstract DbBeanEditor getInstance(String idOrSid);

    protected String changeOrder(HttpRequestParameters requestParameters) throws ServletException {
        String idOrSid = getBeanIdOrSid(requestParameters, "id");

        ChangeOrderDirection direction = getChangeOrderDirection(requestParameters);
        String companionIdOrSid = requestParameters.getValue("companionId");

        return changeOrder(idOrSid, direction, companionIdOrSid, requestParameters);
    }

    protected abstract String changeOrder(
            String idOrSid,
            ChangeOrderDirection direction,
            String companionIdOrSid,
            HttpRequestParameters requestParameters
    );

    protected String displayTable(HttpRequestParameters requestParameters) {
        var table = retrieveTable(requestParameters);
        initTable(table, requestParameters);
        return getTableSummaryInfo(table) + getTable(table);
    }

    protected MasterTableView retrieveTable(HttpRequestParameters requestParameters) {
        var table = Types.createInstanceOf(getTableClass(), MasterTableView.class);
        table.setLanguage(getLanguage(requestParameters.getSession()));
        return table;
    }

    protected abstract String getTableClass();

    protected void initTable(MasterTableView table, HttpRequestParameters requestParameters) { }

    protected String getTableSummaryInfo(MasterTableView table) {
        return table.getSummaryInfo();
    }

    protected String getTable(MasterTableView table) {
        return table.getMasterTable();
    }

    protected String getFormButtons(HttpRequestParameters requestParameters) throws ServletException {
        return getFormButtonsPrefix(requestParameters) +
                getHTMLView(getBeanIdOrSid(requestParameters, "id"), requestParameters).getStandaloneFormButtons() +
                getFormButtonsSuffix(requestParameters);
    }

    protected String getFormButtonsPrefix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected String getFormButtonsSuffix(HttpRequestParameters requestParameters) {
        return "";
    }

    protected String newVersion(HttpRequestParameters requestParameters) throws ServletException {
        String idOrSid = getBeanIdOrSid(requestParameters, "id");

        var editor = getVersionedBeanEditor(idOrSid, requestParameters);
        System.out.println(editor);
        if (!editor.isLatestVersionedBean())
            throw new ServletException("New version can only be created from the latest version of the bean");
        if (!editor.needsNewBeanVersion())
            throw new ServletException("Creating a new version is not required at this time");

        var newVersion = editor.newVersionedEditor();
        System.out.println(newVersion);
        System.out.println("ID = " + newVersion.getId() + ", SID = " + newVersion.getSid() + ", idOrSid = " + newVersion.getIdOrSid());
        return getJsonStatusObject("ok").put("id", newVersion.getIdOrSid()).toString();
    }

    protected VersionedBeanEditor getVersionedBeanEditor(String idOrSid, HttpRequestParameters requestParameters)
            throws ServletException
    {
        throw new ServletException(
                "NEW_VERSION is not supported here: bean is not versioned or getVersionedBeanEditor() was not overridden.",
                new UnsupportedOperationException("getVersionedBeanEditor() not implemented")
        );
    }

}
