package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.List;

public final class Types {

    public static <T> List<T> getSubtypeList(List<? extends T> supertypes) {
        return new ArrayList<T>(supertypes);
    }

}
