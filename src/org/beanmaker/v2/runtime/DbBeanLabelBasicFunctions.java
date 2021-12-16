package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import java.util.List;
import java.util.Optional;

public interface DbBeanLabelBasicFunctions {

    default boolean lenient() {
        return true;
    }

    default boolean lookForGlobalLabel() {
        return true;
    }

    default String globalPrefix() {
        return "global";
    }

    default String missingLabelErrorPrefix() {
        return "MISSING LABEL: ";
    }

    default String missingLanguageErrorPrefix() {
        return "MISSING LANGUAGE: ";
    }

    default DbBeanLanguage getDefaultLanguage() {
        return null;
    }

    Optional<DbBeanLabel> getPossibleLabel(String labelName);

    default String get(String labelName, DbBeanLanguage language) {
        return processContent(
                getPossibleLabel(labelName).map(label -> label.get(language)).orElse(null),
                labelName,
                language
        );
    }

    default String get(String labelName, DbBeanLanguage language, List<Object> parameters) {
        return processContent(
                getPossibleLabel(labelName).map(label -> label.get(language, parameters)).orElse(null),
                labelName,
                language
        );
    }

    private String processContent(String content, String labelName, DbBeanLanguage language) {
        if (content == null) {
            String errorMessage = missingLabelErrorPrefix() + labelName;
            if (lenient())
                return errorMessage;
            throw new IllegalArgumentException(errorMessage);
        }
        if (Strings.isEmpty(content)) {
            String errorMessage = missingLanguageErrorPrefix() + labelName + " (" + language.getCapIso() + ")";
            if (lenient())
                return errorMessage;
            throw new IllegalArgumentException(errorMessage);
        }
        return content;
    }

    boolean isNameOK(String labelName);

    default String get(String prefix, String labelName, DbBeanLanguage language) {
        String fullName = prefix + "_" + labelName;
        if (isNameOK(fullName) || !lookForGlobalLabel())
            return get(fullName, language);

        return get(getGlobalName(labelName), language);
    }

    default String get(String prefix, String labelName, DbBeanLanguage language, List<Object> parameters) {
        String fullName = prefix + "_" + labelName;
        if (isNameOK(fullName) || !lookForGlobalLabel())
            return get(fullName, language, parameters);

        return get(getGlobalName(labelName), language, parameters);
    }

    private String getGlobalName(String labelName) {
        if (Strings.isEmpty(globalPrefix()))
            return labelName;

        return globalPrefix() + "_" + labelName;
    };

}
