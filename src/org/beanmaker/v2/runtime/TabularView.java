package org.beanmaker.v2.runtime;

public class TabularView extends BaseView {

    protected DbBeanLanguage dbBeanLanguage = null;
    protected boolean languageInfoRequired = false;
    protected boolean displayAllLanguages = true;

    protected String yesName = "yes";
    protected String noName = "no";

    protected boolean displayId = false;

    public TabularView(String resourceBundleName) {
        super(resourceBundleName);
    }
}
