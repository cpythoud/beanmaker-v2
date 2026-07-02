package org.beanmaker.v2.runtime;

public class SidRequiredException extends RuntimeException {

    public SidRequiredException() {
        super("SID required; numeric ID is not allowed");
    }

    public SidRequiredException(String message) {
        super(message);
    }

}
