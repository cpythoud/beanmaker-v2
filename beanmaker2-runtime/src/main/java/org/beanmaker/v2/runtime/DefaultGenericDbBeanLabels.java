package org.beanmaker.v2.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DefaultGenericDbBeanLabels implements GenericDbBeanLabels {

    private static final Map<String, String> LABEL_MAP;
    private static final GenericDbBeanLabels INSTANCE;

    static {
        LABEL_MAP = new HashMap<>();
        LABEL_MAP.put("_required", "required");

        INSTANCE = new DefaultGenericDbBeanLabels();
    }

    public static GenericDbBeanLabels getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<String> findMatch(String labelName) {
        return GenericDbBeanLabels.findMatch(LABEL_MAP, labelName);
    }

}
