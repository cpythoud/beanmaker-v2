package org.beanmaker.v2.runtime;

public abstract class DbBeanHTMLWrapperBase {

    private DbBeanInterface bean;
    private DbBeanLanguage language;

    protected void setBean(DbBeanInterface bean) {
        this.bean = bean;
    }

    public void setLanguage(DbBeanLanguage language) {
        this.language = language;
    }

    public DbBeanInterface getBean() {
        return bean;
    }

    public DbBeanLanguage getLanguage() {
        return language;
    }

    protected void checkParameters() {
        if (bean == null)
            throw new IllegalArgumentException("No bean set");
        if (language == null)
            throw new IllegalArgumentException("No language set");
    }

    public abstract String getForm();

    public String getDisplayName() {
        checkParameters();
        return getBean().getNameForIdNamePairsAndTitles(language);
    }

}
