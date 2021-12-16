package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldValidationResult {
    
    public static final FieldValidationResult OK = new FieldValidationResult(true, null, null);

    private final boolean status;
    private final String labelName;
    private final List<Object> labelParameters;

    private FieldValidationResult(boolean status, String labelName, List<String> labelParameters) {
        this.status = status;
        this.labelName = labelName;
        this.labelParameters = labelParameters == null ? null : Collections.unmodifiableList(labelParameters);
    }

    public static FieldValidationResult create(String labelName) {
        return new FieldValidationResult(false, labelName, null);
    }

    public static FieldValidationResult create(String labelName, Object... labelParameters) {
        return create(labelName, Arrays.asList(labelParameters));
    }

    public static FieldValidationResult create(String labelName, List<Object> labelParameters) {
        var stringParameters = new ArrayList<String>();
        for (var parameter: labelParameters)
            stringParameters.add(parameter.toString());  // * we do not want mutable objects as parameters
        return new FieldValidationResult(false, labelName, stringParameters);
    }

    public boolean ok() {
        return status;
    }

    public String getLabelName() {
        return labelName;
    }

    public List<Object> getLabelParameters() {
        return labelParameters;
    }

}
