package org.beanmaker.v2.runtime;

import org.jcodegen.html.StrongTag;
import org.jcodegen.html.TableTag;
import org.jcodegen.html.Tag;
import org.jcodegen.html.TbodyTag;
import org.jcodegen.html.TdTag;
import org.jcodegen.html.ThTag;
import org.jcodegen.html.TrTag;

import java.util.List;

public class HtmlTableHelper {

	protected String tableCssClass = "display-table";
	protected String beanTableCssClassPrefix = tableCssClass;

	protected String rowCssClass = "display-table-row";
	protected String beanRowCssClassPrefix = rowCssClass;

	protected boolean useStrongInsteadOfTh = false;

	public void setTableCssClass(String tableCssClass) {
		this.tableCssClass = tableCssClass;
	}

	public void setBeanTableCssClassPrefix(String beanTableCssClassPrefix) {
		this.beanTableCssClassPrefix = beanTableCssClassPrefix;
	}

	public void setRowCssClass(String rowCssClass) {
		this.rowCssClass = rowCssClass;
	}

	public void setBeanRowCssClassPrefix(String beanRowCssClassPrefix) {
		this.beanRowCssClassPrefix = beanRowCssClassPrefix;
	}

	public void setUseStrongInsteadOfTh(boolean useStrongInsteadOfTh) {
		this.useStrongInsteadOfTh = useStrongInsteadOfTh;
	}

	public String getTableCssClass() {
		return tableCssClass;
	}

	public String getBeanTableCssClassPrefix() {
		return beanTableCssClassPrefix;
	}

	public String getRowCssClass() {
		return rowCssClass;
	}

	public String getBeanRowCssClassPrefix() {
		return beanRowCssClassPrefix;
	}

	public boolean shouldUseStrongInsteadOfTh() {
		return useStrongInsteadOfTh;
	}

	public static class Row {

		private final String fieldName;
		private final String label;
		private final String value;
		private final boolean useStrongInsteadOfTh;

		public Row(String fieldName, String label, String value) {
			this(fieldName, label, value, false);
		}

		public Row(String fieldName, String label, String value, boolean useStrongInsteadOfTh) {
			this.fieldName = fieldName;
			this.label = label;
			this.value = value;
			this.useStrongInsteadOfTh = useStrongInsteadOfTh;
		}
		
		private TrTag getCode(String rowCssClass, String beanRowCssClassPrefix, int index) {
			return new TrTag()
					.cssClass(getCssClasses(rowCssClass, beanRowCssClassPrefix, index))
					.child(getRowTitle())
					.child(new TdTag(value));
		}

		private Tag getRowTitle() {
			if (useStrongInsteadOfTh)
				return new TdTag().child(new StrongTag(label));

			return new ThTag(label);
		}

		private String getCssClasses(String rowCssClass, String beanRowCssClassPrefix, int index) {
			return rowCssClass + " " + beanRowCssClassPrefix + "-" + fieldName
					+ (index % 2 == 0 ? " even" : " odd");
		}
	}

	public String getTable(String beanName, long id, List<Row> rows) {
		return getTableCode(beanName, id, rows).toString();
	}

	protected TableTag getTableCode(String beanName, long id, List<Row> rows) {
		TbodyTag body = new TbodyTag();

		int index = 0;
		for (Row row: rows)
			body.child(row.getCode(rowCssClass, beanRowCssClassPrefix, ++index));

		return new TableTag()
				.id(getTableId(beanName, id))
				.cssClass(getCssClasses(beanName))
				.child(body);
	}

	protected String getTableId(String beanName, long id) {
		return beanName + "_" + id;
	}

	protected String getCssClasses(String beanName) {
		return tableCssClass + " " + beanTableCssClassPrefix + "-" + beanName;
	}

	public static String getTextRow(String label, String value) {
		return getTextRow(label, value, " : ");
	}

	public static String getTextRow(String label, String value, String separator) {
		return label + separator + value + "\n";
	}
	
}
