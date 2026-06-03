package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

public interface LocalFileManager {

    DbBeanFile get(long id);

    DbBeanFile getOrCreate(long id);

    boolean isIdOK(long id);

    boolean isIdOK(long id, DbTransaction transaction);

    String getFilename(long id);

    String getDefaultUploadDir();

    DbBeanFileInternalFilenameCalculator getDefaultFileStoredFileNameCalculator();

}
