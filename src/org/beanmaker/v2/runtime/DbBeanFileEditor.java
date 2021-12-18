package org.beanmaker.v2.runtime;

import java.io.File;

public abstract class DbBeanFileEditor extends DbBeanEditor {

    protected DbBeanFileEditor(DbBeanParameters parameters) {
        super(parameters);
    }

    public abstract String getCode();
    public abstract String getFilename();
    public abstract String getInternalFilename();
    public abstract String getAltDir();

    public abstract void setCode(final String code);
    public abstract void setFilename(final String origFilename);
    public abstract void setInternalFilename(final String storedFilename);
    public abstract void setAltDir(final String altDir);

    public abstract boolean isAltDirEmpty();

    public abstract DbBeanFile getDbBeanFile();

    public abstract File getFile();

    public abstract void makeSureFileExists();

    /*String getFileUrl();

    Date getModificationDate();
    Timestamp getModificationTimestamp();*/

}
