package org.beanmaker.v2.runtime.util;

import org.beanmaker.v2.runtime.HtmlFormHelper;
import org.beanmaker.v2.util.Types;

import javax.ws.rs.PUT;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ApplicationParameters {
    INSTANCE;

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

    private void ensureInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("ApplicationParameters not initialized");
        }
    }

    public HtmlFormHelper getHtmlFormHelper() {
        ensureInitialized();
        String className = getAsString(HTML_HELPER_CLASS_NAME_PARAMETER);
        if (className == null)
            throw new IllegalStateException("No HTML helper class name specified");

        return Types.createInstanceOf(className, HtmlFormHelper.class);
    }

}
