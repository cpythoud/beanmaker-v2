package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

public interface IdBasedReference {

    long getId();

    default String getSid() {
        throw new UnsupportedOperationException("No SID available for this element");
    }

    default String getIdOrSid() {
        return Long.toString(getId());
    }

}
