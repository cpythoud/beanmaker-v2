package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldValidationResult {
    
    public static final FieldValidationResult OK =
            new FieldValidationResult(true, false, true, null, null);

    private final boolean ok;
    private final boolean warning;
    private final boolean continueOnError;
    private final String labelName;
    private final List<Object> labelParameters;

    private FieldValidationResult(
            boolean ok,
            boolean warning,
            boolean continueOnError,
            String labelName,
            List<String> labelParameters)
    {
        this.ok = ok;
        this.warning = warning;
        this.continueOnError = continueOnError;
        this.labelName = labelName;
        this.labelParameters = labelParameters == null ? null : Collections.unmodifiableList(labelParameters);
    }

    private static FieldValidationResult create(
            boolean ok,
            boolean warning,
            boolean continueOnError,
            String labelName,
            Object... labelParameters)
    {
        return new FieldValidationResult(ok, warning, continueOnError, labelName, convertParameters(labelParameters));
    }

    private static List<String> convertParameters(Object[] labelParameters) {
        if (labelParameters == null)
            return Collections.emptyList();

        var stringParameters = new ArrayList<String>();
        for (Object parameter: labelParameters)
            stringParameters.add(parameter.toString());  // * we do not want mutable objects as parameters
        return stringParameters;
    }

    public static FieldValidationResult fatal(String labelName, Object... labelParameters) {
        return new FieldValidationResult(
                false,
                false,
                false,
                labelName,
                convertParameters(labelParameters)
        );
    }

    public static FieldValidationResult error(String labelName, Object... labelParameters) {
        return new FieldValidationResult(
                false,
                false,
                true,
                labelName,
                convertParameters(labelParameters)
        );
    }

    public static FieldValidationResult warning(String labelName, Object... labelParameters) {
        return new FieldValidationResult(
                true,
                true,
                true,
                labelName,
                convertParameters(labelParameters)
        );
    }

    public boolean ok() {
        return ok;
    }

    public boolean isWarning() {
        return warning;
    }

    public boolean continueOnError() {
        return continueOnError;
    }

    public String getLabelName() {
        return labelName;
    }

    public List<Object> getLabelParameters() {
        return labelParameters;
    }

}
