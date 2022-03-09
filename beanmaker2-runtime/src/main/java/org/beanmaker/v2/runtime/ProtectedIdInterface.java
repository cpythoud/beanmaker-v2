package org.beanmaker.v2.runtime;

public interface ProtectedIdInterface extends DbBeanInterface {

    ProtectedIdManager getProtectedIdManager();

    String getProtectionCode();

    void initFromProtectionCode(String code);

    boolean matchesProtectionCode(String code);

}
