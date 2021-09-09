package org.beanmaker.v2.runtime;

public class DbBeanFileIdentityStoredFilenameCalculator implements DbBeanFileStoredFilenameCalculator {

    @Override
    public String calc(String originalFilename) {
        return originalFilename;
    }
}
