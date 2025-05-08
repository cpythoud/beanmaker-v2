package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Types;

import org.jcodegen.html.OptionTag;
import org.jcodegen.html.SelectTag;
import org.jcodegen.html.Tag;

import java.util.List;

public class DbBeanHtmlSelector {

    private final List<DbBeanInterface> beans;
    private final DbBeanLanguage language;
    private final boolean sortByName;
    private final long selectedId;
    private final String noSelectionText;
    private final DbBeanLabel noSelectionLabel;
    private final String selectHtmlId;
    private final String selectCssClasses;

    private DbBeanHtmlSelector(
            List<DbBeanInterface> beans,
            DbBeanLanguage language,
            boolean sortByName,
            long selectedId,
            String noSelectionText,
            DbBeanLabel noSelectionLabel,
            String selectHtmlId,
            String selectCssClasses)
    {
        this.beans = beans;
        this.language = language;
        this.sortByName = sortByName;
        this.selectedId = selectedId;
        this.noSelectionText = noSelectionText;
        this.noSelectionLabel = noSelectionLabel;
        this.selectHtmlId = selectHtmlId;
        this.selectCssClasses = selectCssClasses;
    }

    public static <B extends DbBeanInterface> Builder builder(List<B> beans, DbBeanLanguage language) {
        return new Builder(Types.getSubtypeList(beans), language);
    }

    public static class Builder {
        private final List<DbBeanInterface> beans;
        private final DbBeanLanguage language;
        private boolean sortByName = false;
        private long selectedId = 0;
        private String noSelectionText;
        private DbBeanLabel noSelectionLabel;
        private String selectHtmlId;
        private String selectCssClasses;

        private Builder(List<DbBeanInterface> beans, DbBeanLanguage language) {
            this.beans = beans;
            this.language = language;
        }

        public Builder sortByName() {
            sortByName = true;
            return this;
        }

        public Builder selectedId(long selectedId) {
            this.selectedId = selectedId;
            return this;
        }

        public Builder noSelectionText(String noSelectionText) {
            this.noSelectionText = noSelectionText;
            return this;
        }

        public Builder noSelectionLabel(DbBeanLabel noSelectionLabel) {
            this.noSelectionLabel = noSelectionLabel;
            return this;
        }

        public Builder selectHtmlId(String selectHtmlId) {
            this.selectHtmlId = selectHtmlId;
            return this;
        }

        public Builder selectCssClasses(String selectCssClasses) {
            this.selectCssClasses = selectCssClasses;
            return this;
        }

        public DbBeanHtmlSelector build() {
            if (noSelectionText != null && noSelectionLabel != null)
                throw new IllegalStateException("Only one of noSelectionText or noSelectionLabel can be set");

            return new DbBeanHtmlSelector(beans, language, sortByName, selectedId, noSelectionText, noSelectionLabel,
                    selectHtmlId, selectCssClasses);
        }
    }

    public Tag getTag() {
        var tag = getSelectTag();
        addOptions(tag);
        return tag;
    }

    private SelectTag getSelectTag() {
        var tag = new SelectTag();
        if (selectHtmlId != null)
            tag.id(selectHtmlId);
        if (selectCssClasses != null)
            tag.cssClass(selectCssClasses);
        return tag;
    }

    private void addOptions(SelectTag tag) {
        String selectedOptionId = Long.toString(selectedId);
        for (var idNamePair : getIdNamePairs()) {
            var option = new OptionTag(idNamePair.getName(), idNamePair.getId());
            if (selectedOptionId.equals(idNamePair.getId()))
                option.selected();
            tag.child(option);
        }
    }

    private List<IdNamePair> getIdNamePairs() {
        return IdNamePair.getPairs(beans, language, getNoSelectionText(), sortByName);
    }

    private String getNoSelectionText() {
        if (noSelectionText != null)
            return noSelectionText;

        if (noSelectionLabel != null)
            return noSelectionLabel.get(language);

        return null;
    }

    @Override
    public String toString() {
        return getTag().toString();
    }

}
