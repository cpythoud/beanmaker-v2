package org.beanmaker.v2.runtime;

import java.io.File;

public interface DbBeanFileEditor {

    DbBeanEditor getAssociatedEditor();

    String getCode();
    String getFilename();
    String getInternalFilename();
    String getAltDir();

    void setCode(String code);
    void setFilename(String filename);
    void setInternalFilename(String internalFilename);
    void setAltDir(String altDir);

    boolean isAltDirEmpty();

    DbBeanFile getDbBeanFile();

    File getFile();

    void makeSureFileExists();

}
