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

    public abstract void setId(long id);

    public void setSid(String sid) {
        throw new UnsupportedOperationException("No SID field in bean");
    }

    public void setIdOrSid(String idOrSid) {
        setId(Long.parseLong(idOrSid));
    }

    public void setCode(String code) {
        throw new UnsupportedOperationException("Bean identification through code is unsupported");
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

    public String getSid() {
        throw new UnsupportedOperationException("No SID field in bean");
    }

    public String getIdOrSid() {
        return Long.toString(getId());
    }

    protected String getIdOrSid(DbBeanParameters parameters) {
        if (parameters.useSids())
            return getSid();

        return Long.toString(getId());
    }

    protected boolean noBeanYet() {
        return bean == null;
    }

    protected void checkBean() {
        if (bean == null)
            throw new IllegalStateException("No bean set");
    }

    protected void checkLanguage() {
        if (language == null)
            throw new IllegalStateException("No language set");
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
