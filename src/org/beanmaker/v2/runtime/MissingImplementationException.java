package org.beanmaker.v2.runtime;

public class MissingImplementationException extends RuntimeException {

    public MissingImplementationException(String origin) {
        super("Missing implementation for: " + origin);
    }
}
