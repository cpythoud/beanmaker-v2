package org.beanmaker.v2.runtime;

public class BaseView {

    protected final DbBeanLocalization dbBeanLocalization;

    public BaseView(DbBeanLocalization dbBeanLocalization) {
        this.dbBeanLocalization = dbBeanLocalization;
    }

    public void setLanguage(DbBeanLanguage language) {
        dbBeanLocalization.setLanguage(language);
    }

}
