package org.beanmaker.v2.runtime.util;

import org.beanmaker.v2.runtime.HtmlFormHelper;
import org.beanmaker.v2.util.Types;

import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ApplicationParameters {
    INSTANCE;

    private static final List<String> BOOLEAN_TRUE_VALUES = List.of("true", "yes", "on", "1");
    private static final List<String> BOOLEAN_FALSE_VALUES = List.of("false", "no", "off", "0");

    public static final String HTML_HELPER_CLASS_NAME_PARAMETER = "htmlHelperClassName";

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

    public boolean getAsBoolean(String key) {
        ensureExisting(key);
        String value = getAsString(key);

        if (BOOLEAN_TRUE_VALUES.contains(value))
            return true;
        if (BOOLEAN_FALSE_VALUES.contains(value))
            return false;

        throw new IllegalArgumentException("Invalid boolean value: " + value);
    }

    public int getAsInt(String key) {
        ensureExisting(key);
        return Integer.parseInt(getAsString(key));
    }

    public long getAsLong(String key) {
        ensureExisting(key);
        return Long.parseLong(getAsString(key));
    }

    public long getId(String key) {
        return getAsLong(key);
    }

    private void ensureInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("ApplicationParameters not initialized");
        }
    }

    private void ensureExisting(String key) {
        ensureInitialized();
        if (!parameters.containsKey(key))
            throw new IllegalStateException("No value specified for key: " + key);
    }

    public HtmlFormHelper getHtmlFormHelper() {
        ensureInitialized();
        String className = getAsString(HTML_HELPER_CLASS_NAME_PARAMETER);
        if (className == null)
            throw new IllegalStateException("No HTML helper class name specified");

        return Types.createInstanceOf(className, HtmlFormHelper.class);
    }

}
