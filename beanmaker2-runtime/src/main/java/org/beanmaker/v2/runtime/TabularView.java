package org.beanmaker.v2.runtime;

public class TabularView extends BaseView {

    protected boolean displayId = false;
    protected boolean displayAllLanguages = true;
    protected boolean languageInfoRequired = true;

    public TabularView(DbBeanLocalization dbBeanLocalization) {
        super(dbBeanLocalization);
    }

    protected String yesName() {
        return dbBeanLocalization.getLabel("yes");
    }
    protected String noName() {
        return dbBeanLocalization.getLabel("no");
    }

}
