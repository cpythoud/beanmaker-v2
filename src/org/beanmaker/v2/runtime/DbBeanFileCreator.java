package org.beanmaker.v2.runtime;

import org.apache.commons.fileupload.FileItem;

import java.io.File;

public class DbBeanFileCreator {

    public static final String SUBDIR_PREFIX = "P-";

    private final String defaultUploadDir;
    private final String alternateUploadDir;
    private final DbBeanFileStoredFilenameCalculator storedFilenameCalculator;
    private final int newUploadSubDirFileCountThreshold;

    public DbBeanFileCreator(String defaultUploadDir, int newUploadSubDirFileCountThreshold) {
        this(defaultUploadDir, new DbBeanFileIdentityStoredFilenameCalculator(), newUploadSubDirFileCountThreshold);
    }

    public DbBeanFileCreator(
            String defaultUploadDir,
            DbBeanFileStoredFilenameCalculator storedFilenameCalculator,
            int newUploadSubDirFileCountThreshold)
    {
        this(defaultUploadDir, null, storedFilenameCalculator, newUploadSubDirFileCountThreshold);
    }

    public DbBeanFileCreator(
            String defaultUploadDir,
            String alternateUploadDir,
            DbBeanFileStoredFilenameCalculator storedFilenameCalculator,
            int newUploadSubDirFileCountThreshold)
    {
        this.defaultUploadDir = defaultUploadDir;
        if (alternateUploadDir == null || alternateUploadDir.equals(defaultUploadDir))
            this.alternateUploadDir = null;
        else
            this.alternateUploadDir = alternateUploadDir;
        this.storedFilenameCalculator = storedFilenameCalculator;
        this.newUploadSubDirFileCountThreshold = newUploadSubDirFileCountThreshold;
    }

    public DbBeanFile create(DbBeanFile dbBeanFile, FileItem fileItem) {
        boolean newRecord = false;

        dbBeanFile.setOrigFilename(fileItem.getName());
        String filename = storedFilenameCalculator.calc(fileItem.getName());
        if (filename.equals(dbBeanFile.getOrigFilename()))
            dbBeanFile.setStoredFilename(null);
        else
            dbBeanFile.setStoredFilename(filename);

        if (alternateUploadDir != null)
            dbBeanFile.setAltDir(alternateUploadDir);

        if (dbBeanFile.getId() == 0) {
            dbBeanFile.updateDB();
            newRecord = true;
        }

        try {
            fileItem.write(new File(getOrCreateUploadDirectory(dbBeanFile), filename));
        } catch (Exception ex) {  // function write() is actually marked as throwing Exception !!!
            if (newRecord)
                dbBeanFile.delete();
            throw new RuntimeException(ex);
        }

        if (!newRecord)
            dbBeanFile.updateDB();

        return dbBeanFile;
    }

    private File getOrCreateUploadDirectory(DbBeanFile dbBeanFile) {
        File uploadDirectory = getUploadDirectory(dbBeanFile, defaultUploadDir, newUploadSubDirFileCountThreshold);

        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs())
                throw new IllegalArgumentException("Could not create directory: " + uploadDirectory.getAbsolutePath());
        } else if (!uploadDirectory.isDirectory())
            throw new IllegalArgumentException(uploadDirectory.getAbsolutePath() + " is not a directory");

        return uploadDirectory;
    }

    public static File getUploadDirectory(DbBeanFile dbBeanFile, String defaultUploadDir, int newUploadSubDirFileCountThreshold) {
        if (dbBeanFile.isAltDirEmpty()) {
            File baseDir;
            if (newUploadSubDirFileCountThreshold == 0)
                baseDir = new File(defaultUploadDir);
            else
                baseDir = new File(defaultUploadDir, SUBDIR_PREFIX + (dbBeanFile.getId() / newUploadSubDirFileCountThreshold));
            return new File(baseDir, Long.toString(dbBeanFile.getId()));
        }

        return new File(dbBeanFile.getAltDir(), Long.toString(dbBeanFile.getId()));
    }
}
