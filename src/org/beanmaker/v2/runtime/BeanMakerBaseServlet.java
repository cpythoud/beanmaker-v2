package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.MimeTypes;
import org.beanmaker.v2.util.Strings;

import org.javatuples.Pair;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BeanMakerBaseServlet extends HttpServlet {

    private static final List<String> BOOLEAN_TRUE_VALUES = List.of("true", "yes", "on");
    private static final List<String> BOOLEAN_FALSE_VALUES = List.of("false", "no", "off");

    public enum Operation {
        GET_FORM(1, "get"),
        SUBMIT_FORM(2, "submit"),
        DELETE_BEAN(3, "delete"),
        CHANGE_ORDER(4, "order");

        private final int index;
        private final String paramValue;

        Operation(int index, String paramValue) {
            this.index = index;
            this.paramValue = paramValue;
        }

        public int getIndex() {
            return index;
        }

        private static final Map<String, Operation> OPERATION_MAP = initOperationMap();

        private static Map<String, Operation> initOperationMap() {
            Map<String, Operation> map = new LinkedHashMap<String, Operation>();
            for (Operation operation: values())
                map.put(operation.paramValue, operation);
            return map;
        }

        public static Operation from(HttpServletRequest request) throws ServletException {
            String paramValue = request.getParameter("beanmaker_operation");
            if (paramValue == null)
                throw new ServletException("Missing beanmaker_operation parameter");

            Operation operation = OPERATION_MAP.get(paramValue);
            if (operation == null)
                throw new ServletException("Unknown operation: " + paramValue);

            return operation;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    abstract protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected String getRootPath() {
        return getServletContext().getRealPath("/");
    }

    protected String getErrorsInJson(List<ErrorMessage> errorMessages) {
        StringBuilder buf = new StringBuilder();

        buf.append("{ \"ok\": false, \"errors\": [ ");

        for (ErrorMessage errorMessage: errorMessages)
            buf.append(errorMessage.toJson()).append(", ");

        buf.delete(buf.length() - 2, buf.length());
        buf.append(" ] }");

        return buf.toString();
    }

    protected long getBeanId(HttpServletRequest request, String parameterName) {
        return Strings.getLongVal(request.getParameter(parameterName));
    }

    protected String getJsonSimpleStatus(String status) {
        return "{ \"status\": \"" + status + "\" }";
    }

    protected String getJsonOk() {
        return getJsonSimpleStatus("ok");
    }

    protected String getJsonNoSession() {
        return getJsonSimpleStatus("no session");
    }

    protected String getStartJsonErrors() {
        return "{ \"status\": \"errors\", ";
    }

    protected Pair<String, Long> getSubmittedFormAndId(HttpServletRequest request) throws ServletException {
        String form = null;
        long id = 0;

        int count = 0;
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            if (param.startsWith("submitted")) {
                ++count;
                form = param.substring(9, param.length());
                id = Strings.getLongVal(request.getParameter(param));
            }
        }

        if (count > 1)
            throw new ServletException("More than one submittedXXX parameter.");

        return new Pair<String, Long>(form, id);
    }

    protected String processBean(HttpServletRequest request, DbBeanHTMLViewInterface htmlView) {
        htmlView.setAllFields(request);

        if (htmlView.isDataOK()) {
            htmlView.updateDB();
            return getJsonOk();
        }

        return getStartJsonErrors() + ErrorMessage.toJson(htmlView.getErrorMessages()) + " }";
    }

    protected Pair<String, Long> getBeanAndId(HttpServletRequest request) throws ServletException {
        String beanName = getBeanName(request);

        long id = getBeanId(request, "id");
        if (id == 0)
            throw new ServletException("Missing id parameter or id == 0");

        return new Pair<String, Long>(beanName, id);
    }

    protected Pair<String, String> getBeanAndCode(HttpServletRequest request) throws ServletException {
        String beanName = getBeanName(request);

        String code = request.getParameter("id");
        if (code == null)
            throw new ServletException("Missing id parameter");

        return new Pair<String, String>(beanName, code);
    }

    private String getBeanName(HttpServletRequest request) throws ServletException {
        String beanName = request.getParameter("bean");
        if (beanName == null)
            throw new ServletException("Missing bean parameter.");

        return beanName;
    }

    protected String deleteBean(DbBeanEditor bean) {
        bean.delete();
        return getJsonOk();
    }

    protected void disableCaching(HttpServletResponse response) {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    protected int getItemOrderDirectionChangeParameter(HttpServletRequest request) throws ServletException {
        String direction = request.getParameter("direction");

        if (direction == null)
            throw new ServletException("Missing direction parameter");

        return Strings.getIntVal(direction);
    }

    protected String singleStepItemOrderChangeFor(DbBeanEditorWithItemOrder beanEditor, int direction) {
        if (direction == 0)
            throw new IllegalArgumentException(
                    "Illegal direction parameter value: must be a non zero positive or negative integer.");

        if (direction > 0)
            beanEditor.itemOrderMoveUp();
        else
            beanEditor.itemOrderMoveDown();

        return getJsonOk();
    }

    protected String getErrorMessageContainerHtml(String idContainer) {
        return getErrorMessageContainerHtml(idContainer, "error-message-container");
    }

    protected String getErrorMessageContainerHtml(String idContainer, String cssClass) {
        return "<div id='" + idContainer + "' class='" + cssClass + "'></div>";
    }

    protected ChangeOrderDirection getChangeOrderDirection(HttpServletRequest request) throws ServletException {
        return getChangeOrderDirection(request, "direction");
    }

    protected ChangeOrderDirection getChangeOrderDirection(
            HttpServletRequest request,
            String parameterName
    ) throws ServletException
    {
        String direction = request.getParameter(parameterName);
        if (direction == null)
            throw new ServletException("Missing direction parameter");

        return ChangeOrderDirection.valueOf(direction.toUpperCase());
    }

    protected String changeOrder(
            DbBeanEditorWithItemOrder bean,
            ChangeOrderDirection direction,
            BasicItemOrderOperations companion)
    {
        switch (direction) {
            case UP:
                bean.itemOrderMoveUp();
                break;
            case DOWN:
                bean.itemOrderMoveDown();
                break;
            case AFTER:
                bean.itemOrderMoveAfter(companion);
                break;
            case BEFORE:
                bean.itemOrderMoveBefore(companion);
                break;
            default:
                throw new AssertionError("New/unchecked Direction ?");
        }

        return getJsonOk();
    }

    /*protected <B extends DbBeanEditorWithItemOrder<B>> String changeOrder(
            B bean,
            ChangeOrderDirection direction,
            B companion)
    {
        switch (direction) {
            case UP:
                bean.itemOrderMoveUp();
                break;
            case DOWN:
                bean.itemOrderMoveDown();
                break;
            case AFTER:
                bean.itemOrderMoveAfter(companion);
                break;
            case BEFORE:
                bean.itemOrderMoveBefore(companion);
                break;
            default:
                throw new AssertionError("New/unchecked Direction ?");
        }

        return getJsonOk();
    }*/

    protected void changeLocalOrder(
            long itemOrder,
            ChangeOrderDirection direction,
            long companionItemOrder,
            TableLocalOrderContext context,
            String orderingTable)
    {
        switch (direction) {
            case UP:
                TableLocalOrderUtil.itemOrderMoveUp(itemOrder, context, orderingTable);
                break;
            case DOWN:
                TableLocalOrderUtil.itemOrderMoveDown(itemOrder, context, orderingTable);
                break;
            case AFTER:
                TableLocalOrderUtil.itemOrderMoveAfter(itemOrder, companionItemOrder, context, orderingTable);
                break;
            case BEFORE:
                TableLocalOrderUtil.itemOrderMoveBefore(itemOrder, companionItemOrder, context, orderingTable);
                break;
            default:
                throw new AssertionError("New/unchecked Direction ?");
        }
    }

    protected void writeFileToServletOutputStream(
            DbBeanFile dbBeanFile,
            HttpServletResponse response
    ) throws IOException
    {
        writeFileToServletOutputStream(dbBeanFile.getFile(), dbBeanFile.getInternalFilename(), response);
    }

    protected void writeFileToServletOutputStream(File file, HttpServletResponse response) throws IOException {
        writeFileToServletOutputStream(file, file.getName(), response);
    }

    protected void writeFileToServletOutputStream(
            File file,
            String filename,
            HttpServletResponse response
    ) throws IOException
    {
        response.setContentType(MimeTypes.getType(filename));
        response.setContentLength((int) file.length());
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", filename));

        FileInputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, bytesRead);

        inputStream.close();
        outputStream.close();
    }

    protected <B extends DbBeanEditor> B getBean(
            B newBean,
            HttpServletRequest request,
            String idParameterName
    ) throws ServletException
    {
        String idStr = request.getParameter(idParameterName);
        if (idStr == null)
            throw new ServletException("Missing parameter: " + idParameterName);
        long id = Strings.getLongVal(idStr);
        if (id == 0)
            throw new ServletException("Invalid parameter: " + idParameterName + " = " + idStr);

        newBean.setId(id);
        return newBean;
    }

    protected Operation getOperation(HttpServletRequest request) throws ServletException {
        return Operation.from(request);
    }

    protected String getExpectedStringParameter(HttpServletRequest request, String parameterName) throws ServletException {
        String value = request.getParameter(parameterName);
        if (value == null)
            throw new ServletException("Missing request parameter: " + parameterName);

        return value;
    }

    protected int getExpectedIntegerParameter(HttpServletRequest request, String parameterName) throws ServletException {
        try {
            return Integer.parseInt(getExpectedStringParameter(request, parameterName));
        } catch (NumberFormatException nex) {
            throw new ServletException(nex);
        }
    }

    protected long getExpectedLongParameter(HttpServletRequest request, String parameterName) throws ServletException {
        try {
            return Long.parseLong(getExpectedStringParameter(request, parameterName));
        } catch (NumberFormatException nex) {
            throw new ServletException(nex);
        }
    }

    protected boolean getExpectedBooleanParameter(HttpServletRequest request, String parameterName) throws ServletException {
        String value = getExpectedStringParameter(request, parameterName);
        if (BOOLEAN_TRUE_VALUES.contains(value))
            return true;
        if (BOOLEAN_FALSE_VALUES.contains(value))
            return false;
        throw new ServletException("Value cannot be interpreted as boolean: " + value);
    }

}
