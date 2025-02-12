package org.beanmaker.v2.runtime;

import java.util.List;
import java.util.Map;

public interface DbBeanLabel {

    long getId();
    String getName();

    String get(DbBeanLanguage dbBeanLanguage);
    String get(DbBeanLanguage dbBeanLanguage, Object... parameters);
    String get(DbBeanLanguage dbBeanLanguage, List<Object> parameters);
    String get(DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters);

    boolean hasDataFor(DbBeanLanguage dbBeanLanguage);

    String getSafeValue(DbBeanLanguage dbBeanLanguage);
    String getSafeValue(DbBeanLanguage dbBeanLanguage, Object... parameters);
    String getSafeValue(DbBeanLanguage dbBeanLanguage, List<Object> parameters);
    String getSafeValue(DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters);

}
