package org.beanmaker.v2.runtime;

public class DbBeanFileIdenticalInternalFilenameCalculator implements DbBeanFileInternalFilenameCalculator {

    @Override
    public String calc(String originalFilename) {
        return originalFilename;
    }
}
