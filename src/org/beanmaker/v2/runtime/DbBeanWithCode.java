package org.beanmaker.v2.runtime;

public interface DbBeanWithCode extends DbBeanInterface {

    String getCode();
    void setCode(String code);

    boolean isCodeEmpty();
    boolean isCodeRequired();
    boolean isCodeOK();
    boolean isCodeToBeUnique();
    boolean isCodeUnique();

    String getCodeLabel();
    String getCodeEmptyErrorMessage();
    String getCodeBadFormatErrorMessage();
    String getCodeNotUniqueErrorMessage();

}
