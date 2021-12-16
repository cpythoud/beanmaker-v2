package org.beanmaker.v2.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DbBeanRequiredLanguages {

    private final Set<DbBeanLanguage> languages;

    public DbBeanRequiredLanguages() {
        languages = new HashSet<>();
    }

    public DbBeanRequiredLanguages(DbBeanLanguage... languages) {
        this(languages.length == 0 ? Collections.emptyList() : Arrays.asList(languages));
    }

    public DbBeanRequiredLanguages(Collection<DbBeanLanguage> languages) {
        this.languages = new HashSet<>(languages);
    }

    public DbBeanRequiredLanguages(DbBeanRequiredLanguages requiredLanguages) {
        languages = new HashSet<>(requiredLanguages.languages);
    }

    public boolean isRequired(DbBeanLanguage dbBeanLanguage) {
        return languages.contains(dbBeanLanguage);
    }

    public Set<DbBeanLanguage> getLanguageSet() {
        return Collections.unmodifiableSet(languages);
    }

}
