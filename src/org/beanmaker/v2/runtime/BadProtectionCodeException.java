package org.beanmaker.v2.runtime;

public class BadProtectionCodeException extends RuntimeException {

    public BadProtectionCodeException(ProtectedIdInterface bean) {
        super("Missing protection code for bean " + bean.getClass().getName() + " #" + bean.getId());
    }

    public BadProtectionCodeException(ProtectedIdInterface bean, final String code) {
        super("Bad protection code passed for bean " + bean.getClass().getName()
                + " #" + bean.getId()
                + (code == null ? "" : " (code = " + code + ")"));
    }

    public BadProtectionCodeException(String message) {
        super(message);
    }
}
