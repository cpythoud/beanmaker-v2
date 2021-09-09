package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

import java.util.List;
import java.util.Map;

public interface LabelManager {

    DbBeanLabel get(long id);

    boolean isIdOK(long id);

    boolean isIdOK(long id, DBTransaction transaction);

    boolean isNameOK(String name);

    boolean isNameOK(String name, DBTransaction transaction);

    String get(long id, DbBeanLanguage dbBeanLanguage);

    String get(String name, DbBeanLanguage dbBeanLanguage);

    DbBeanLabel createInstance();

    List<DbBeanLanguage> getAllActiveLanguages();

    DbBeanLanguage getLanguage(long id);

    DbBeanLanguage getCopy(DbBeanLanguage dbBeanLanguage);

    DbBeanLabel replaceData(DbBeanLabel into, DbBeanLabel from);

    Map<String, String> getLabelMap(DbBeanLanguage dbBeanLanguage, String... labelNames);

    Map<String, String> getLabelMap(DbBeanLanguage dbBeanLanguage, List<String> labelNames);
}
