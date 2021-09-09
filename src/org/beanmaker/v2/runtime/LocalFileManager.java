package org.beanmaker.v2.runtime;

import org.dbbeans.sql.DBTransaction;

public interface LocalFileManager {

    DbBeanFile get(long id);

    DbBeanFile getOrCreate(long id);

    boolean isIdOK(long id);

    boolean isIdOK(long id, DBTransaction transaction);

    String getFilename(long id);

    String getDefaultUploadDir();

    DbBeanFileStoredFilenameCalculator getDefaultFileStoredFileNameCalculator();

}
