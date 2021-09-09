package org.beanmaker.v2.runtime;

public class HFHParameterMissingException extends IllegalArgumentException {

    public HFHParameterMissingException(String parameter) {
        super("Parameter '" + parameter + "' cannot be retrieved without having been set first.");
    }
}
