package org.beanmaker.v2.runtime;

import org.jcodegen.html.ATag;

import java.io.File;

import java.sql.Date;
import java.sql.Timestamp;

public interface DbBeanFile extends DbBeanInterface {

    static ATag getLink(DbBeanFile dbBeanFile) {
        return new ATag(dbBeanFile.getFilename(), dbBeanFile.getFileUrl());
    }

    static ATag getLink(DbBeanFile dbBeanFile, String linkLabel) {
        return new ATag(linkLabel, dbBeanFile.getFileUrl());
    }

    String getCode();
    String getFilename();
    String getInternalFilename();
    String getAltDir();

    boolean isAltDirEmpty();

    File getFile();
    String getFileUrl();

    Date getModificationDate();
    Timestamp getModificationTimestamp();

}
