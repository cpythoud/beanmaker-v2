package org.beanmaker.v2.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DbBeanRequiredLanguages {

    private final Set<Long> languageIds;

    public DbBeanRequiredLanguages() {
        languageIds = new HashSet<Long>();
    }

    public DbBeanRequiredLanguages(DbBeanLanguage... languages) {
        this(languages.length == 0 ? Collections.<DbBeanLanguage>emptyList() : Arrays.asList(languages));
    }

    public DbBeanRequiredLanguages(Collection<DbBeanLanguage> languages) {
        languageIds = new HashSet<Long>(Ids.getIdSet(languages));
    }

    public DbBeanRequiredLanguages(DbBeanRequiredLanguages requiredLanguages) {
        languageIds = new HashSet<Long>(requiredLanguages.languageIds);
    }

    public boolean isRequired(DbBeanLanguage dbBeanLanguage) {
        return languageIds.contains(dbBeanLanguage.getId());
    }

    public void setStatus(DbBeanLanguage dbBeanLanguage, boolean required) {
        if (required)
            languageIds.add(dbBeanLanguage.getId());
        else
            languageIds.remove(dbBeanLanguage.getId());
    }
}
