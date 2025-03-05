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

    protected DbBeanInterface getBean() {
        return bean;
    }

    public DbBeanLanguage getLanguage() {
        return language;
    }

    public long getId() {
        return bean.getId();
    }

    protected void checkParameters() {
        if (bean == null)
            throw new IllegalArgumentException("No bean set");
        if (language == null)
            throw new IllegalArgumentException("No language set");
    }

    protected abstract DbBeanHTMLViewInterface getHtmlView();

    public String getForm() {
        return getHtmlView().getHtmlForm();
    }

    public String getDisplayName() {
        checkParameters();
        return getBean().getNameForIdNamePairsAndTitles(language);
    }

}
