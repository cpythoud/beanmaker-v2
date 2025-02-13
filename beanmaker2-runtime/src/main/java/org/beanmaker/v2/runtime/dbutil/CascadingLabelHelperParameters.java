package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;
import org.beanmaker.v2.runtime.DbBeanWithUniqueCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CascadingLabelHelperParameters {

    private final List<Object> parameterList;
    private final Map<String, Object> parameterMap;
    private final DbBeanLanguage defaultLanguage;
    private final String defaultValue;

    private CascadingLabelHelperParameters(
            List<Object> parameterList,
            Map<String, Object> parameterMap,
            DbBeanLanguage defaultLanguage,
            String defaultValue)
    {
        this.parameterList = parameterList;
        this.parameterMap = parameterMap;
        this.defaultLanguage = defaultLanguage;
        this.defaultValue = defaultValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Object> getParameterList() {
        return parameterList;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public boolean hasMapBasedParameters() {
        return parameterMap != null;
    }

    public DbBeanLanguage getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static class Builder {
        private List<Object> parameterList;
        private Map<String, Object> parameterMap;
        private DbBeanLanguage defaultLanguage;
        private String defaultValue;

        public Builder parameters(Object... parameterList) {
            return parameters(Arrays.asList(parameterList));
        }

        public Builder parameters(List<Object> parameterList) {
            this.parameterList = parameterList;
            return this;
        }

        public Builder parameters(Map<String, Object> parameterMap) {
            this.parameterMap = parameterMap;
            return this;
        }

        public Builder defaultLanguage(DbBeanLanguage defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder defaultValue(DbBeanWithUniqueCode bean) {
            defaultValue = "[" + bean.getCode() + "]";
            return this;
        }

        public CascadingLabelHelperParameters build() {
            if (parameterMap != null) {
                if (parameterList != null)
                    throw new IllegalStateException(
                            "You can only have one type of parameters (either the list or the map)");

                return new CascadingLabelHelperParameters(
                        null,
                        Collections.unmodifiableMap(parameterMap),
                        defaultLanguage,
                        defaultValue);
            }

            if (parameterList == null)
                return new CascadingLabelHelperParameters(
                        Collections.emptyList(),
                        null,
                        defaultLanguage,
                        defaultValue);

            return new CascadingLabelHelperParameters(
                    Collections.unmodifiableList(parameterList),
                    null,
                    defaultLanguage,
                    defaultValue);
        }
    }

}
