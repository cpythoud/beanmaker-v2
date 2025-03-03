package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import java.util.List;
import java.util.Optional;

public abstract class DbBeanLabelBasicFunctions {

    public String get(String prefix, String labelName, DbBeanLanguage language) {
        String fullName = prefix + "_" + labelName;
        if (isNameOK(fullName) || !lookForGlobalLabel())
            return get(fullName, language);

        String globalName = getGlobalName(labelName);
        if (!isNameOK(globalName) && lookForGenericLabel()) {
            String genericLabelName = getGenericLabels().findMatch(labelName).orElse(null);
            if (genericLabelName != null)
                return get(getGenericName(genericLabelName), language, fullName, globalName);
        }

        return get(globalName, language, fullName);
    }

    public String get(String prefix, String labelName, DbBeanLanguage language, List<Object> parameters) {
        String fullName = prefix + "_" + labelName;
        if (isNameOK(fullName) || !lookForGlobalLabel())
            return get(fullName, language, parameters);

        String globalName = getGlobalName(labelName);
        if (!isNameOK(globalName) && lookForGenericLabel()) {
            String genericLabelName = getGenericLabels().findMatch(labelName).orElse(null);
            if (genericLabelName != null)
                return get(getGenericName(genericLabelName), language, parameters, fullName, globalName);
        }

        return get(getGlobalName(labelName), language, parameters, fullName);
    }

    public abstract DbBeanLabel getLabel(long id);

    public abstract Optional<DbBeanLabel> getPossibleLabel(String labelName);

    public abstract boolean isNameOK(String labelName);

    public abstract DbBeanLanguage getDefaultLanguage();

    protected boolean lenient() {
        return true;
    }

    protected boolean lookForGlobalLabel() {
        return true;
    }

    protected boolean lookForGenericLabel() {
        return true;
    }

    protected boolean fallbackToDefaultLanguage() {
        return false;
    }

    protected String globalPrefix() {
        return "global";
    }

    protected String genericPrefix() {
        return "generic";
    }

    protected String missingLabelErrorPrefix() {
        return "MISSING LABEL(S): ";
    }

    protected String missingLanguageErrorPrefix() {
        return "MISSING LANGUAGE: ";
    }

    protected GenericDbBeanLabels getGenericLabels() {
        return DefaultGenericDbBeanLabels.getInstance();
    }

    private String retrieveText(DbBeanLabel label, DbBeanLanguage language) {
        if (fallbackToDefaultLanguage())
            return label.getSafeValue(language);
        return label.get(language);
    }

    private String retrieveText(DbBeanLabel label, DbBeanLanguage language, List<Object> parameters) {
        if (fallbackToDefaultLanguage())
            return label.getSafeValue(language, parameters);
        return label.get(language, parameters);
    }

    protected String get(String labelName, DbBeanLanguage language) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language)).orElse(null),
                labelName,
                language,
                null,
                null
        );
    }

    protected String get(String labelName, DbBeanLanguage language, List<Object> parameters) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language, parameters)).orElse(null),
                labelName,
                language,
                null,
                null
        );
    }

    protected String get(String labelName, DbBeanLanguage language, String fullyQualifiedLabel) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language)).orElse(null),
                labelName,
                language,
                fullyQualifiedLabel,
                null
        );
    }

    protected String get(String labelName, DbBeanLanguage language, List<Object> parameters, String fullyQualifiedLabel) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language, parameters)).orElse(null),
                labelName,
                language,
                fullyQualifiedLabel,
                null
        );
    }

    protected String get(String labelName, DbBeanLanguage language, String fullyQualifiedLabel, String globalLabelName) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language)).orElse(null),
                labelName,
                language,
                fullyQualifiedLabel,
                globalLabelName
        );
    }

    protected String get(String labelName, DbBeanLanguage language, List<Object> parameters, String fullyQualifiedLabel, String globalLabelName) {
        return processContent(
                getPossibleLabel(labelName).map(label -> retrieveText(label, language, parameters)).orElse(null),
                labelName,
                language,
                fullyQualifiedLabel,
                globalLabelName
        );
    }

    private String processContent(
            String content,
            String labelName,
            DbBeanLanguage language,
            String fullyQualifiedLabel,
            String globalLabelName)
    {
        if (content == null) {
            String errorMessage =
                    missingLabelErrorPrefix()
                            + (fullyQualifiedLabel == null ? "" : fullyQualifiedLabel + ", ")
                            + (globalLabelName == null ? "" : globalLabelName + ", ")
                            + labelName;
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

    private String getGlobalName(String labelName) {
        if (Strings.isEmpty(globalPrefix()))
            return labelName;

        return globalPrefix() + "_" + labelName;
    }

    private String getGenericName(String labelName) {
        if (Strings.isEmpty(genericPrefix()))
            return labelName;

        return genericPrefix() + "_" + labelName;
    }

}
