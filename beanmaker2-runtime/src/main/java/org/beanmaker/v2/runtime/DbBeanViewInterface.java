package org.beanmaker.v2.runtime;

public interface DbBeanViewInterface {

    void resetId();

    void setId(long id);

    void setIdOrSid(String idOrSid);

    long getId();

    String getSid();

    String getIdOrSid();

}
