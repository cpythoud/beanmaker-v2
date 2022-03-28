package org.beanmaker.v2.runtime;

import java.util.Map;
import java.util.Optional;

public interface GenericDbBeanLabels {

    static Optional<String> findMatch(Map<String, String> labelNameEndingsMap, String labelName) {
        for (String ending: labelNameEndingsMap.keySet())
            if (labelName.endsWith(ending))
                return Optional.of(labelNameEndingsMap.get(ending));

        return Optional.empty();
    }

    Optional<String> findMatch(String labelName);

}
