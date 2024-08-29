package org.beanmaker.v2.runtime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SafeLabels {

    private static final String DEFAULT_ALTERNATIVE_VALUE = "[MISSING LABEL]";

    private final String defaultAlternativeValue;

    public SafeLabels() {
        this(DEFAULT_ALTERNATIVE_VALUE);
    }

    public SafeLabels(String defaultAlternativeValue) {
        this.defaultAlternativeValue = defaultAlternativeValue;
    }

    public String get(DbBeanMultilingual bean, DbBeanLanguage language, Object... parameters) {
        return get(bean, language, defaultAlternativeValue, parameters);
    }

    public String get(DbBeanMultilingual bean, DbBeanLanguage language, List<Object> parameters) {
        return get(bean, language, defaultAlternativeValue, parameters);
    }

    public String get(DbBeanMultilingual bean, DbBeanLanguage language, String alternativeValue, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return get(bean, language, alternativeValue, Collections.emptyList());

        return get(bean, language, alternativeValue, Arrays.asList(parameters));
    }

    public String get(
            DbBeanMultilingual bean,
            DbBeanLanguage language,
            String alternativeValue,
            List<Object> parameters)
    {
        if (bean == null || language == null)
            return getAlternativeValue(bean, language, alternativeValue, parameters);
        if (bean.getIdLabel() == 0)
            return getAlternativeValue(bean, language, alternativeValue, parameters);

        var label = bean.getLabel();
        if (label.hasDataFor(language))
            return label.get(language, parameters);
        return getAlternativeValue(bean, language, alternativeValue, parameters);
    }

    protected String getAlternativeValue(
            DbBeanMultilingual bean,
            DbBeanLanguage language,
            String alternativeValue,
            List<Object> parameters)
    {
        return alternativeValue;
    }

    public String get(DbBeanMultilingualWithUniqueCode bean, DbBeanLanguage language, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return get(bean, language, Collections.emptyList());

        return get(bean, language, Arrays.asList(parameters));
    }

    public String get(DbBeanMultilingualWithUniqueCode bean, DbBeanLanguage language, List<Object> parameters) {
        if (bean == null || language == null)
            return formatCode(bean, language, parameters);
        if (bean.getIdLabel() == 0)
            return formatCode(bean, language, parameters);

        var label = bean.getLabel();
        if (label.hasDataFor(language))
            return label.get(language, parameters);
        return formatCode(bean, language, parameters);
    }

    protected String formatCode(
            DbBeanMultilingualWithUniqueCode bean,
            DbBeanLanguage language,
            List<Object> parameters)
    {
        if (bean == null)
            return getAlternativeValue(null, language, defaultAlternativeValue, parameters);

        return "[" + bean.getCode() + "]";
    }

}
