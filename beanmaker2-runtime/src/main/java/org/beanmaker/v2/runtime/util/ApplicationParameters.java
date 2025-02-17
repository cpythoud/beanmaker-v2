package org.beanmaker.v2.runtime.util;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ApplicationParameters {
    INSTANCE;

    private final ConcurrentHashMap<String, Object> parameters = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public void initialize(Map<String, Object> initialParameters) {
        if (!initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("ApplicationParameters already initialized");
        }
        parameters.putAll(initialParameters);
    }

    public Object get(String key) {
        ensureInitialized();
        return parameters.get(key);
    }

    public boolean hasParameter(String key) {
        ensureInitialized();
        return parameters.containsKey(key);
    }

    public String getAsString(String key) {
        ensureInitialized();
        Object value = parameters.get(key);
        return value == null ? null : value.toString();
    }

    private void ensureInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("ApplicationParameters not initialized");
        }
    }

}
