package org.beanmaker.v2.runtime;

import java.io.File;

public interface DbBeanFileEditor extends DbBeanEditor {

    String getCode();
    String getFilename();
    String getInternalFilename();
    String getAltDir();

    void setCode(final String code);
    void setFilename(final String origFilename);
    void setInternalFilename(final String storedFilename);
    void setAltDir(final String altDir);

    boolean isAltDirEmpty();

    DbBeanFile getDbBeanFile();

    File getFile();

    void makeSureFileExists();

    /*String getFileUrl();

    Date getModificationDate();
    Timestamp getModificationTimestamp();*/

}
