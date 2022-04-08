package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Money;
import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.ATag;
import org.jcodegen.html.CData;
import org.jcodegen.html.HtmlCodeFragment;
import org.jcodegen.html.InputTag;
import org.jcodegen.html.OptionTag;
import org.jcodegen.html.PTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.SpanTag;
import org.jcodegen.html.TableTag;
import org.jcodegen.html.Tag;
import org.jcodegen.html.TbodyTag;
import org.jcodegen.html.TdTag;
import org.jcodegen.html.ThTag;
import org.jcodegen.html.TheadTag;
import org.jcodegen.html.TrTag;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseMasterTableView extends TabularView {

    protected String tableId;

    protected String iconLibrary = "glyphicons glyphicons-";
    protected String filetypeIconLibrary = "filetypes filetypes-";

    protected String tableCssClass = "cctable";
    protected String thResetCssClass = null;
    protected String tdResetCssClass = null;
    protected String thFilterCssClass = null;
    protected String tdFilterCssClass = null;
    protected String thTitleCssClass = "tb-sort";
    protected String thSuperTitleCssClass = null;

    protected String trFilterCssClass = null;
    protected String trTitleCssClass = null;
    protected String trSuperTitleCssClass = null;

    protected String removeFilteringIcon = "remove-circle";
    protected String formElementFilterCssClass = "tb-filter";
    protected String removeFilteringLinkCssClass = "tb-nofilter";
    protected Tag removeFilteringHtmlTags() {
        return new SpanTag()
                .cssClass(iconLibrary + removeFilteringIcon)
                .title(dbBeanLocalization.getLabel("cct_remove_filtering"));
    }

    protected String yesValue = "A";
    protected String noValue = "Z";
    protected String yesDisplay() {
        return "✔";
    }
    protected String noDisplay() {
        return "";
    }

    protected DateFormat dateFormat = null;
    protected DateFormat timeFormat = null;
    protected DateFormat datetimeFormat = null;

    protected String booleanCenterValueCssClass = "center";

    protected int zeroFilledMaxDigits = 18;

    protected int columnCount = 0;

    protected String noDataMessage() {
        return dbBeanLocalization.getLabel("cct_no_data");
    }

    protected String summaryTotalLabel() {
        return dbBeanLocalization.getLabel("cct_total");
    }
    protected String summaryShownLabel() {
        return dbBeanLocalization.getLabel("cct_shown");
    }
    protected String summaryFilteredOutLabel() {
        return dbBeanLocalization.getLabel("cct_filtered");
    }

    protected boolean showBeanIdInRowId = true;

    protected String editIcon = "edit";
    protected String deleteIcon = "bin";
    protected boolean showEditLinks = false;

    protected String moveUpLabel() {
        return dbBeanLocalization.getLabel("cct_move_up");
    }
    protected String moveDownLabel() {
        return dbBeanLocalization.getLabel("cct_move_down");
    }
    protected String moveUpIcon = "chevron-up";
    protected String moveDownIcon = "chevron-down";
    protected boolean showOrderingLinks = false;

    protected boolean doDataToggle = false;
    protected boolean showAllData = false;
    protected String showMoreLabel() {
        return dbBeanLocalization.getLabel("cct_show_more");
    }
    protected String showLessLabel() {
        return dbBeanLocalization.getLabel("cct_show_less");
    }
    protected String showMoreIcon = "eye-plus";
    protected String showLessIcon = "eye-minus";
    protected String showMoreCssClass = "tb-show-more";
    protected String showLessCssClass = "tb-show-less";
    protected String maskableCssClass = "tb-maskable";
    protected String maskingLinkCssClass = "tb-masking-link";
    protected String maskedCssClass = "tb-masked";
    protected String thShowDataToogleCssClass = null;

    protected boolean enableDragNDrop = false;
    protected String sortableCssClass = "tb-sortable";
    protected String dragNDropActiveIcon = "resize-vertical";
    protected String dragNDropDragElementCssClass = "tb-reorder";

    protected TableLocalOrderContext localOrderContext = null;
    protected String localOrderingTable = null;

    protected String sumLineCssClass = "tb-summation-line";
    protected String sumCellCssClass = "tb-summation-data";

    // ! Infrastructure exports Excel reste provisoirement en place, mais pas d'implémentation dans la première version
    protected boolean excelExportAvailable = false;
    protected String excelExportIdSuffix = "excel";
    protected String excelExportCssClass = "excel_export";
    protected String excelExportIcon = "xlsx";
    protected Tag excelExportHtmlTags() {
        return new SpanTag()
                .cssClass(filetypeIconLibrary + excelExportIcon)
                .title(dbBeanLocalization.getLabel("cct_excel_export"));
    }

    protected boolean excelExportDownloadLinkAlreadyShown = false;
    protected Map<String, String> excelExportExtraParameters = new HashMap<>();

    public BaseMasterTableView(String tableId, DbBeanLocalization dbBeanLocalization) {
        super(dbBeanLocalization);
        this.tableId = tableId;
    }

    public String getMasterTable() {
        columnCount = 0;

        excelExportDownloadLinkAlreadyShown = false;
        initExcelExportExtraParameters();

        return getMasterTableTag().toString();
    }

    public void setShowAllData(boolean showAllData) {
        this.showAllData = showAllData;
    }

    public void setLocalOrderContext(TableLocalOrderContext localOrderContext) {
        this.localOrderContext = localOrderContext;
    }

    public void setLocalOrderingTable(String localOrderingTable) {
        this.localOrderingTable = localOrderingTable;
    }

    public TableTag getMasterTableTag() {
        return getTable().child(getHead()).child(getBody());
    }

    protected TableTag getTable() {
        String dataToggleCssClass;
        if (doDataToggle) {
            if (showAllData)
                dataToggleCssClass = " " + showMoreCssClass;
            else
                dataToggleCssClass = " " + showLessCssClass;
        } else {
            dataToggleCssClass = "";
        }

        return new TableTag()
                .cssClass(tableCssClass + dataToggleCssClass)
                .id(tableId);
    }

    protected TheadTag getHead() {
        TheadTag head = new TheadTag();

        head.child(getFilterRow());
        head.child(getTitleRow());

        return head;
    }

    protected TbodyTag getBody() {
        TbodyTag body = new TbodyTag();

        if (enableDragNDrop)
            body.cssClass(sortableCssClass);

        int count = 0;
        for (TrTag tr: getData()) {
            body.child(tr);
            ++count;
        }
        if (count == 0)
            body.child(getNoDataAvailableLine());

        return body;
    }

    protected abstract List<TrTag> getData();

    protected abstract long getLineCount();

    protected TrTag getNoDataAvailableLine() {
        return getNoDataAvailableLine(noDataMessage());
    }

    protected TrTag getNoDataAvailableLine(String message) {
        return getNoDataAvailableLine(message, columnCount);
    }

    protected TrTag getNoDataAvailableLine(String message, int columnCount) {
        return new TrTag().child(
                new TdTag().cssClass(tdResetCssClass)
        ).child(
                new TdTag(message).colspan(columnCount)
        );
    }

    protected TrTag getFilterRow() {
        return getDefaultStartOfFilterRow();
    }

    protected TrTag getTitleRow() {
        return getDefaultStartOfTitleRow();
    }

    protected TrTag getDefaultStartOfFilterRow() {
        TrTag filterRow = new TrTag().child(getRemoveFilteringCellWithLink());

        if (trFilterCssClass != null)
            filterRow.cssClass(trFilterCssClass);

        return filterRow;
    }

    protected TrTag getDefaultStartOfTitleRow() {
        TrTag titleRow = new TrTag();

        if (excelExportDownloadLinkAlreadyShown || !excelExportAvailable)
            titleRow.child(getRemoveFilteringCell());
        else {
            titleRow.child(getDownloadExcelFileCellWithLink());
            excelExportDownloadLinkAlreadyShown = true;
        }

        if (trTitleCssClass != null)
            titleRow.cssClass(trTitleCssClass);

        return titleRow;
    }

    protected TrTag getDefaultStartOfSummationRow() {
        TrTag summationRow = new TrTag().cssClass(sumLineCssClass);

        summationRow.child(getTableCellForRemoveFilteringPlaceholder());

        return summationRow;
    }

    protected ThTag getRemoveFilteringCellWithLink() {
        return getRemoveFilteringCell().child(
                new ATag().href("#")
                        .cssClass(removeFilteringLinkCssClass)
                        .child(removeFilteringHtmlTags())
        );
    }

    protected ThTag getRemoveFilteringCell() {
        ThTag cell = new ThTag();

        if (thResetCssClass != null)
            cell.cssClass(thResetCssClass);

        return cell;
    }

    protected ThTag getDownloadExcelFileCellWithLink() {
        return getRemoveFilteringCell().child(getDownloadExcelFileLink());
    }

    protected ATag getDownloadExcelFileLink() {
        ATag link = new ATag()
                .href("#")
                .id(tableId + "_" + excelExportIdSuffix)
                .cssClass(removeFilteringLinkCssClass + " " + excelExportCssClass)
                .child(excelExportHtmlTags());

        for (Map.Entry<String, String> parameter: excelExportExtraParameters.entrySet())
            link.data(parameter.getKey(), parameter.getValue());

        return link;
    }

    protected ThTag getTableFilterCell() {
        ThTag cell = new ThTag();

        if (thFilterCssClass != null)
            cell.cssClass(thFilterCssClass);

        return cell;
    }

    protected ThTag getStringFilterCell(String name) {
        return getTableFilterCell().child(
                new InputTag(InputTag.InputType.TEXT)
                        .name("tb-" + name)
                        .cssClass(formElementFilterCssClass)
                        .attribute("autocomplete", "off")
        );
    }

    protected ThTag getBooleanFilterCell(String name) {
        return getTableFilterCell().child(
                new SelectTag().name("tb-" + name).cssClass(formElementFilterCssClass).child(
                        new OptionTag("", "").selected()
                ).child(
                        new OptionTag(yesName(), yesValue)
                ).child(
                        new OptionTag(noName(), noValue)
                )
        );
    }

    protected ThTag getBasicSelectFilterCell(String name, List<String> values) {
        SelectTag select = new SelectTag().name("tb-" + name).cssClass(formElementFilterCssClass);

        select.child(new OptionTag("", "").selected());
        for (String value: values)
            select.child(new OptionTag(value));

        return getTableFilterCell().child(select);
    }

    protected static record FilterNameValuePair(String name, String value) { }

    protected ThTag getPairBasedSelectFilterCell(String name, List<FilterNameValuePair> nameValuePairs) {
        SelectTag select = new SelectTag().name("tb-" + name).cssClass(formElementFilterCssClass);

        select.child(new OptionTag("", "").selected());
        for (var pair: nameValuePairs)
            select.child(new OptionTag(pair.name(), pair.value()));

        return getTableFilterCell().child(select);
    }

    protected ThTag getAdvancedSelectFilterCell(String name, List<IdNamePair> pairs) {
        SelectTag select = new SelectTag().name("tb-" + name).cssClass(formElementFilterCssClass);

        select.child(new OptionTag("", "").selected());
        for (IdNamePair pair: pairs)
            select.child(new OptionTag(
                    pair.getName(),
                    Strings.zeroFill(Long.parseLong(pair.getId()), zeroFilledMaxDigits)));

        return getTableFilterCell().child(select);
    }

    protected ThTag getTitleCell(String name) {
        return getTitleCell(name, getTitle(name));
    }

    protected ThTag getStyledTitleCell(String name, String extraCssClasses) {
        return getStyledTitleCell(name, getTitle(name), extraCssClasses);
    }

    protected String getTitle(String name) {
        return dbBeanLocalization.getLabel(name);
    }

    protected ThTag getTitleCell(String name, String adhocTitle) {
        return getStyledTitleCell(name, adhocTitle, null);
    }

    protected ThTag getTitleCell(String name, Tag adhocTitle) {
        return getStyledTitleCell(name, adhocTitle, null);
    }

    protected ThTag getStyledTitleCell(String name, String adhocTitle, String extraCssClasses) {
        ++columnCount;
        return new ThTag(adhocTitle)
                .cssClass(getTitleCellCssClasses(name, extraCssClasses))
                .attribute("data-sort-class", "tb-" + name);
    }

    protected ThTag getStyledTitleCell(String name, Tag adhocTitle, String extraCssClasses) {
        ++columnCount;
        return new ThTag()
                .cssClass(getTitleCellCssClasses(name, extraCssClasses))
                .attribute("data-sort-class", "tb-" + name)
                .child(adhocTitle);
    }

    protected String getTitleCellCssClasses(String name, String extraCssClasses) {
        return thTitleCssClass + " th-" + name + (Strings.isEmpty(extraCssClasses) ? "" : " " + extraCssClasses);
    }

    public <B extends DbBeanInterface> TrTag getTableLine(B bean) {
        TrTag line;
        if (showEditLinks)
            line = getTrTag(bean.getId());
        else
            line = getTableLine(bean.getId());

        if (showEditLinks)
            line.child(getEditCell(bean));
        if (displayId)
            line.child(getIdTableCell(bean));
        addDataToLine(line, bean);
        if (showEditLinks && okToDelete(bean))
            line.child(getDeleteCell(bean));

        return line;
    }

    public <B extends DbBeanWithItemOrder> TrTag getItemOrderTableLine(B bean) {
        TrTag line;
        if (showEditLinks || showOrderingLinks || enableDragNDrop)
            line = getTrTag(bean.getId());
        else
            line = getTableLine(bean.getId());

        if (showEditLinks || showOrderingLinks || enableDragNDrop)
            line.child(getOperationCell(bean));
        if (displayId)
            line.child(getIdTableCell(bean));
        addDataToLine(line, bean);
        if (showEditLinks && okToDelete(bean))
            line.child(getDeleteCell(bean));

        return line;
    }

    protected TdTag getEditCell(DbBeanInterface bean) {
        return getEditCell(bean, dbBeanLocalization.getBeanVarName(), dbBeanLocalization.getLabel("tooltip_edit"));
    }

    private TdTag getOperationCell(DbBeanWithItemOrder bean) {
        return getOperationCell(bean, dbBeanLocalization.getBeanVarName(), dbBeanLocalization.getLabel("tooltip_edit"));
    }

    protected TdTag getIdTableCell(DbBeanInterface bean) {
        return getTableCell("id", bean.getId());
    }

    protected abstract <B extends DbBeanInterface> void addDataToLine(TrTag line, B bean);

    protected  <B extends DbBeanInterface> boolean okToDelete(B bean) {
        return true;
    }

    protected TdTag getDeleteCell(DbBeanInterface bean) {
        return getDeleteCell(bean, dbBeanLocalization.getBeanVarName(), dbBeanLocalization.getLabel("tooltip_delete"));
    }

    protected TrTag getTableLine(long id) {
        TrTag line = getTrTag(id);

        line.child(getTableCellForRemoveFilteringPlaceholder());

        return line;
    }

    protected TrTag getTrTag(long id) {
        TrTag line = new TrTag();

        if (showBeanIdInRowId)
            line.id(tableId + "_row_" + id);

        return line;
    }

    protected TrTag getTableLine(String code) {
        TrTag line = getTrTag(code);

        line.child(getTableCellForRemoveFilteringPlaceholder());

        return line;
    }

    protected TrTag getTrTag(String code) {
        return new TrTag().id(tableId + "_row_" + code);
    }

    protected TdTag getTableCellForRemoveFilteringPlaceholder() {
        TdTag cell = new TdTag();

        if (tdResetCssClass != null)
            cell.cssClass(tdResetCssClass);

        return cell;
    }

    @Deprecated
    protected TdTag getTableCell(String name, Tag content) {
        return getTableCell(name, content, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Tag content, String extraCssClasses) {
        return new TdTag().child(content).cssClass(getTableCellCssClasses(name, extraCssClasses));
    }

    @Deprecated
    protected TdTag getTableCell(String name, String value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Date value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Date value, String extraCssClasses) {
        if (value == null)
            return getTableCell(name, "");

        if (dateFormat == null)
            dateFormat = DateFormat.getDateInstance();

        return getTableCell(name, dateFormat.format(value), extraCssClasses)
                .attribute("data-sort-value", value.toString());
    }

    @Deprecated
    protected TdTag getTableCell(String name, Time value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Time value, String extraCssClasses) {
        if (value == null)
            return getTableCell(name, "");

        if (timeFormat == null)
            timeFormat = DateFormat.getTimeInstance();

        return getTableCell(name, timeFormat.format(value), extraCssClasses)
                .attribute("data-sort-value", value.toString());
    }

    @Deprecated
    protected TdTag getTableCell(String name, Timestamp value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Timestamp value, String extraCssClasses) {
        if (value == null)
            return getTableCell(name, "");

        if (datetimeFormat == null)
            datetimeFormat = DateFormat.getDateTimeInstance();

        return getTableCell(name, datetimeFormat.format(value), extraCssClasses)
                .attribute("data-sort-value", value.toString());
    }

    @Deprecated
    protected TdTag getTableCell(String name, boolean value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, boolean value, String extraCssClasses) {
        if (value)
            return getTableBooleanCell(name, yesDisplay(), yesValue, extraCssClasses);

        return getTableBooleanCell(name, noDisplay(), noValue, extraCssClasses);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(String name, String value, String sortnfilter) {
        return getTableBooleanCell(name, value, sortnfilter, null);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(
            String name,
            String value,
            String sortnfilter,
            String extraCssClasses)
    {
        return decorateBooleanCell(new TdTag(value), name, sortnfilter, extraCssClasses);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(String name, Tag value, String sortnfilter) {
        return getTableBooleanCell(name, value, sortnfilter, null);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(
            String name,
            Tag value,
            String sortnfilter,
            String extraCssClasses)
    {
        return decorateBooleanCell(new TdTag().child(value), name, sortnfilter, extraCssClasses);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(String name, HtmlCodeFragment value, String sortnfilter) {
        return getTableBooleanCell(name, value, sortnfilter, null);
    }

    @Deprecated
    protected TdTag getTableBooleanCell(
            String name,
            HtmlCodeFragment value,
            String sortnfilter,
            String extraCssClasses) {
        return decorateBooleanCell(new TdTag().addCodeFragment(value), name, sortnfilter, extraCssClasses);
    }

    private TdTag decorateBooleanCell(
            TdTag cell,
            String name,
            String sortnfilter,
            String extraCssClasses)
    {
        return cell
                .cssClass(booleanCenterValueCssClass + " " + getTableCellCssClasses(name, extraCssClasses))
                .attribute("data-filter-value", sortnfilter).attribute("data-sort-value", sortnfilter);
    }

    @Deprecated
    protected TdTag getTableCell(String name, long value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, long value, String extraCssClasses) {
        return getTableCell(name, Long.toString(value), extraCssClasses)
                .attribute("data-sort-value", Strings.zeroFill(value, zeroFilledMaxDigits));
    }

    @Deprecated
    protected TdTag getTableCell(String name, Money value) {
        return getTableCell(name, value, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, Money value, String extraCssClasses) {
        return getTableCell(name, value.toString(), extraCssClasses)
                .attribute("data-sort-value", Strings.zeroFill(value.getVal(), zeroFilledMaxDigits));
    }

    @Deprecated
    protected TdTag getTableCell(String name, HtmlCodeFragment content) {
        return getTableCell(name, content, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, HtmlCodeFragment content, String extraCssClasses) {
        return new TdTag().addCodeFragment(content).cssClass(getTableCellCssClasses(name, extraCssClasses));
    }

    @Deprecated
    protected TdTag getTableCell(String name, IdNamePair pair) {
        return getTableCell(name, pair, null);
    }

    @Deprecated
    protected TdTag getTableCell(String name, IdNamePair pair, String extraCssClasses) {
        return getTableCell(name, pair.getName(), extraCssClasses)
                .attribute("data-filter-value", Strings.zeroFill(Long.valueOf(pair.getId()), zeroFilledMaxDigits));
    }

    @Deprecated
    protected TdTag getTableCell(
            String name,
            List<IdNamePair> pairs,
            long idRow,
            long idSelected,
            boolean emptyChoice)
    {
        return getTableCell(name, pairs, idRow, idSelected, emptyChoice, null);
    }

    @Deprecated
    protected TdTag getTableCell(
            String name,
            List<IdNamePair> pairs,
            long idRow,
            long idSelected,
            boolean emptyChoice,
            String extraCssClasses)
    {
        SelectTag select =
                new SelectTag().id("tb-cell-select-" + name + "_" + idRow).cssClass("tb-cell-select-" + name);

        if (emptyChoice) {
            OptionTag emptyOption = new OptionTag("", "");
            if (idSelected == 0)
                emptyOption.selected();
            select.child(emptyOption);
        }

        String sortValue = null;
        for (IdNamePair pair: pairs) {
            OptionTag option =
                    new OptionTag(pair.getName(), Strings.zeroFill(Long.parseLong(pair.getId()), zeroFilledMaxDigits));
            if (pair.getId().equals(Long.toString(idSelected))) {
                option.selected();
                sortValue = pair.getName();
            }
            select.child(option);
        }

        return getTableCell(name, select, extraCssClasses)
                .attribute("data-filter-value", Strings.zeroFill(idSelected, zeroFilledMaxDigits))
                .attribute("data-sort-value", sortValue == null ? "" : sortValue);
    }

    // ! Start reimplementation of getTableCell()

    protected TdTag getTableCell(MasterTableCellDefinition definition) {
        TdTag cell = getTableCell(definition.fieldName(), definition.content(), definition.extraCssClasses());
        if (definition.orderingDefined())
            cell.attribute("data-sort-value", definition.orderingValue());
        if (definition.filteringDefined())
            cell.attribute("data-filter-value", definition.filteringValue());
        if (definition.sumDefined())
            cell.attribute("data-sum-value", definition.sumValue());
        return cell;
    }

    protected TdTag getTableCell(String name, String value, String extraCssClasses) {
        return new TdTag(value).cssClass(getTableCellCssClasses(name, extraCssClasses));
    }

    protected String getTableCellCssClasses(String name, String extraCssClasses) {
        return "tb-" + name + (extraCssClasses == null ? "" : " " + extraCssClasses);
    }

    protected TdTag getEmptySummationCell(String name) {
        return getTableCell(MasterTableCellDefinition.createTextCellDefinition(name, "").extraCssClasses(sumCellCssClass));
    }

    protected TdTag getEmptyTableCell(String name) {
        return getTableCell(MasterTableCellDefinition.createTextCellDefinition(name, ""));
    }

    protected TdTag getEmptyTableCell(String name, String extraCssClasses) {
        return getTableCell(MasterTableCellDefinition.createTextCellDefinition(name, "").extraCssClasses(extraCssClasses));
    }

    // ! End reimplementation of getTableCell()

    protected TheadTag getThreeLineHead() {
        TheadTag head = new TheadTag();

        head.child(getFilterRow());
        head.child(getSuperTitleRow());
        head.child(getTitleRow());

        return head;
    }

    protected TrTag getSuperTitleRow() {
        return getDefautStartSuperTitleRow();
    }

    protected TrTag getDefautStartSuperTitleRow() {
        TrTag row = new TrTag().child(new ThTag().cssClass(thResetCssClass));

        if (trSuperTitleCssClass != null)
            row.cssClass(trSuperTitleCssClass);

        if (displayId)
            row.child(new ThTag());

        return row;
    }

    protected ThTag getMultiColTitle(String text, int colspan) {
        ThTag multiColTitle = new ThTag(text).colspan(colspan);

        if (thSuperTitleCssClass != null)
            multiColTitle.cssClass(thSuperTitleCssClass);

        return multiColTitle;
    }

    public String getSummaryInfo() {
        return getSummaryInfoCode().toString();
    }

    public Tag getSummaryInfoCode() {
        long count = getLineCount();

        return new PTag().cssClass("cctable-summary")
                .child(getSummarySpan(count, "_total"))
                .child(new CData(summaryTotalLabel()))
                .child(getSummarySpan(count, "_shown"))
                .child(new CData(summaryShownLabel()))
                .child(getSummarySpan(0, "_filtered_out"))
                .child(new CData(summaryFilteredOutLabel()));
    }

    protected SpanTag getSummarySpan(long count, String idPostfix) {
        return new SpanTag(Long.toString(count))
                .id(tableId + idPostfix);
    }

    protected ATag getEditLineLink(long id, String idPrefix, String cssClass, String tooltip) {
        return getOperationLink(id, idPrefix, cssClass, editIcon, tooltip);
    }

    protected ATag getDeleteLineLink(long id, String idPrefix, String cssClass, String tooltip) {
        return getOperationLink(id, idPrefix, cssClass, deleteIcon, tooltip);
    }

    protected ATag getMoveUpLink(long id, String idPrefix, String cssClass) {
        return getOperationLink(id, idPrefix, cssClass, moveUpIcon, moveUpLabel());
    }

    protected ATag getMoveDownLink(long id, String idPrefix, String cssClass) {
        return getOperationLink(id, idPrefix, cssClass, moveDownIcon, moveDownLabel());
    }

    protected ATag getOperationLink(
            long id,
            String idPrefix,
            String cssClass,
            String icon,
            String tooltip)
    {
        return getOperFileLink(id, idPrefix, cssClass, iconLibrary + icon, tooltip);
    }

    private ATag getOperFileLink(
            long id,
            String idPrefix,
            String cssClass,
            String icon,
            String tooltip)
    {
        return new ATag()
                .id(idPrefix + "_" + id)
                .cssClass("tb-operation " + cssClass)
                .child(
                        new SpanTag()
                                .cssClass(icon)
                                .title(tooltip));
    }

    protected TdTag getOperationCell(
            DbBeanWithItemOrder bean,
            String beanName,
            String editTooltip)
    {
        TdTag cell = new TdTag().cssClass(tdResetCssClass);

        if (enableDragNDrop)
            cell.child(new SpanTag().cssClass(iconLibrary + dragNDropActiveIcon + " " + dragNDropDragElementCssClass));

        if (showEditLinks)
            cell.child(getEditLineLink(
                    bean.getId(),
                    beanName,
                    "edit_" + beanName,
                    editTooltip));

        if (showOrderingLinks) {
            if (!bean.isFirstInItemOrder())
                cell.child(getMoveUpLink(
                        bean.getId(),
                        beanName + "Up",
                        "move_up_" + beanName));

            if (!bean.isLastInItemOrder())
                cell.child(getMoveDownLink(
                        bean.getId(),
                        beanName + "Down",
                        "move_down_" + beanName));
        }

        return cell;
    }

    protected TdTag getEditCell(
            DbBeanInterface bean,
            String beanName,
            String tooltip)
    {
        return new TdTag()
                .cssClass(tdResetCssClass)
                .child(getEditLineLink(
                        bean.getId(),
                        beanName,
                        "edit_" + beanName,
                        tooltip));
    }

    protected TdTag getDeleteCell(
            DbBeanInterface bean,
            String beanName,
            String tooltip)
    {
        return new TdTag()
                .cssClass(tdResetCssClass)
                .child(getDeleteLineLink(
                        bean.getId(),
                        beanName + "Del",
                        "delete_" + beanName,
                        tooltip));
    }

    protected ThTag showMoreLessCell() {
        ThTag cell = new ThTag().child(showMoreLink()).child(showLessLink());
        if (thShowDataToogleCssClass != null)
            cell.cssClass(thShowDataToogleCssClass);

        return cell;
    }

    protected ATag showMoreLink() {
        SpanTag icon =
                new SpanTag()
                        .cssClass(iconLibrary + showMoreIcon)
                        .title(showMoreLabel());

        ATag link = new ATag().href("#").id(tableId + "-masking-link-show").child(icon);
        if (showAllData)
            link.cssClass(maskingLinkCssClass + " " + maskedCssClass);
        else
            link.cssClass(maskingLinkCssClass);

        return link;
    }

    protected ATag showLessLink() {
        SpanTag icon =
                new SpanTag()
                        .cssClass(iconLibrary + showLessIcon)
                        .title(showLessLabel());

        ATag link = new ATag().href("#").id(tableId + "-masking-link-hide").child(icon);
        if (showAllData)
            link.cssClass(maskingLinkCssClass);
        else
            link.cssClass(maskingLinkCssClass + " " + maskedCssClass);

        return link;
    }

    protected ThTag getMaskableHeader(ThTag header) {
        return header.changeCssClasses(addShowMoreOrLessCssClasses(header));
    }

    protected TdTag getMaskableCell(TdTag cell) {
        return cell.changeCssClasses(addShowMoreOrLessCssClasses(cell));
    }

    private String addShowMoreOrLessCssClasses(Tag cell) {
        StringBuilder cssClasses = new StringBuilder();

        cssClasses.append(cell.getCssClasses());
        if (cssClasses.length() > 0)
            cssClasses.append(" ");

        cssClasses.append(maskableCssClass);

        return cssClasses.toString();
    }

    protected <B extends DbBeanInterface> List<B> getBeansInLocalOrder(List<B> beans) {
        if (localOrderContext == null)
            return beans;

        if (localOrderingTable == null)
            throw new IllegalStateException("Ordering table not specified");

        return TableLocalOrderUtil.getBeansInOrder(beans, localOrderContext, localOrderingTable);
    }

    protected void initExcelExportExtraParameters() { }

    protected TdTag adjustSorting(TdTag cell, int value) {
        return cell.attribute("data-sort-value", Strings.zeroFill(value, zeroFilledMaxDigits));
    }

    protected TdTag adjustSorting(TdTag cell, long value) {
        return cell.attribute("data-sort-value", Strings.zeroFill(value, zeroFilledMaxDigits));
    }

    protected TdTag adjustSorting(TdTag cell, String value) {
        if (Strings.isEmpty(value))
            return adjustSorting(cell, 0);

        return adjustSorting(cell, Strings.getLongVal(value));
    }
}
