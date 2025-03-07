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
        checkBean();
        return bean;
    }

    public DbBeanLanguage getLanguage() {
        checkLanguage();
        return language;
    }

    public long getId() {
        if (bean == null)
            return 0;

        return bean.getId();
    }

    protected boolean noBeanYet() {
        return bean == null;
    }

    protected void checkBean() {
        if (bean == null)
            throw new IllegalArgumentException("No bean set");
    }

    protected void checkLanguage() {
        if (language == null)
            throw new IllegalArgumentException("No language set");
    }

    protected void checkParameters() {
        checkBean();
        checkLanguage();
    }

    protected abstract DbBeanHTMLViewInterface getHtmlView();

    public String getForm() {
        return getHtmlView().getHtmlForm();
    }

    public String getDisplayName() {
        checkParameters();
        return getBean().getNameForIdNamePairsAndTitles(language);
    }

    public void resetBean() {
        bean = null;
    }

}
