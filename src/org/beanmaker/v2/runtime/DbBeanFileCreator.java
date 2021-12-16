package org.beanmaker.v2.runtime;

import org.apache.commons.fileupload.FileItem;

import org.dbbeans.sql.DBTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.sql.Blob;
import java.sql.SQLException;

public class DbBeanFileCreator {

    public static final String SUBDIR_PREFIX = "P-";

    private final String defaultUploadDir;
    private final String alternateUploadDir;
    private final DbBeanFileInternalFilenameCalculator storedFilenameCalculator;
    private final int newUploadSubDirFileCountThreshold;

    public DbBeanFileCreator(String defaultUploadDir, int newUploadSubDirFileCountThreshold) {
        this(defaultUploadDir, new DbBeanFileDefaultInternalFilenameCalculator(), newUploadSubDirFileCountThreshold);
    }

    public DbBeanFileCreator(
            String defaultUploadDir,
            DbBeanFileInternalFilenameCalculator storedFilenameCalculator,
            int newUploadSubDirFileCountThreshold)
    {
        this(defaultUploadDir, null, storedFilenameCalculator, newUploadSubDirFileCountThreshold);
    }

    public DbBeanFileCreator(
            String defaultUploadDir,
            String alternateUploadDir,
            DbBeanFileInternalFilenameCalculator storedFilenameCalculator,
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

    // * Create from web form
    public DbBeanFile create(DbBeanFileEditor dbBeanFileEditor, FileItem fileItem) {
        String uploadFilename = fileItem.getName();
        String internalFilename = storedFilenameCalculator.calc(uploadFilename);

        boolean newRecord = configureEditor(dbBeanFileEditor, uploadFilename, internalFilename, null);

        try {
            fileItem.write(new File(getOrCreateUploadDirectory(dbBeanFileEditor), internalFilename));
        } catch (Exception ex) {  // function write() is actually marked as throwing Exception !!!
            if (newRecord)
                dbBeanFileEditor.delete();
            throw new RuntimeException(ex);
        }

        return getActualDbBeanFile(dbBeanFileEditor, newRecord, null);
    }

    private boolean configureEditor(DbBeanFileEditor dbBeanFileEditor, String filename, String internalFilename, DBTransaction transaction) {
        boolean newRecord = false;

        dbBeanFileEditor.setFilename(filename);
        dbBeanFileEditor.setInternalFilename(internalFilename);

        if (alternateUploadDir != null)
            dbBeanFileEditor.setAltDir(alternateUploadDir);

        if (dbBeanFileEditor.getId() == 0) {
            if (transaction == null)
                dbBeanFileEditor.updateDB();
            else
                dbBeanFileEditor.updateDB(transaction);
            newRecord = true;
        }

        return newRecord;
    }

    private File getOrCreateUploadDirectory(DbBeanFileEditor dbBeanFileEditor) {
        File uploadDirectory = getUploadDirectory(dbBeanFileEditor, defaultUploadDir, newUploadSubDirFileCountThreshold);

        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs())
                throw new IllegalArgumentException("Could not create directory: " + uploadDirectory.getAbsolutePath());
        } else if (!uploadDirectory.isDirectory())
            throw new IllegalArgumentException(uploadDirectory.getAbsolutePath() + " is not a directory");

        return uploadDirectory;
    }

    private DbBeanFile getActualDbBeanFile(DbBeanFileEditor dbBeanFileEditor, boolean newRecord, DBTransaction transaction) {
        if (!newRecord) {
            if (transaction == null)
                dbBeanFileEditor.updateDB();
            else
                dbBeanFileEditor.updateDB(transaction);
        }

        return dbBeanFileEditor.getDbBeanFile();
    }

    // * Create from file system
    public DbBeanFile create(DbBeanFileEditor dbBeanFileEditor, Path path, String filename) {
        return create(dbBeanFileEditor, path, filename, null);
    }

    // * Create from file system
    public DbBeanFile create(DbBeanFileEditor dbBeanFileEditor, Path path, String filename, DBTransaction transaction) {
        String internalFilename = storedFilenameCalculator.calc(filename);

        boolean newRecord = configureEditor(dbBeanFileEditor, filename, internalFilename, transaction);

        Path dir = getOrCreateUploadDirectory(dbBeanFileEditor).toPath();
        try {
            Files.copy(path, dir.resolve(internalFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }

        return getActualDbBeanFile(dbBeanFileEditor, newRecord, transaction);
    }

    // * Create from database
    public DbBeanFile create(DbBeanFileEditor dbBeanFileEditor, Blob blob, String filename) {
        return create(dbBeanFileEditor, blob, filename, null);
    }

    // * Create from database
    public DbBeanFile create(DbBeanFileEditor dbBeanFileEditor, Blob blob, String filename, DBTransaction transaction) {
        String internalFilename = storedFilenameCalculator.calc(filename);

        boolean newRecord = configureEditor(dbBeanFileEditor, filename, internalFilename, transaction);

        dbBeanFileEditor.makeSureFileExists();
        try {
            InputStream in = blob.getBinaryStream();
            OutputStream out = new FileOutputStream(dbBeanFileEditor.getFile());

            byte[] buff = new byte[4096];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }

            out.close();
        } catch (SQLException | IOException ex) {
            throw new RuntimeException(ex);
        }

        return getActualDbBeanFile(dbBeanFileEditor, newRecord, transaction);
    }

    public static File getUploadDirectory(DbBeanFileEditor dbBeanFileEditor, String defaultUploadDir, int newUploadSubDirFileCountThreshold) {
        if (dbBeanFileEditor.isAltDirEmpty()) {
            File baseDir;
            if (newUploadSubDirFileCountThreshold == 0)
                baseDir = new File(defaultUploadDir);
            else
                baseDir = new File(defaultUploadDir, SUBDIR_PREFIX + (dbBeanFileEditor.getId() / newUploadSubDirFileCountThreshold));
            return new File(baseDir, Long.toString(dbBeanFileEditor.getId()));
        }

        return new File(dbBeanFileEditor.getAltDir(), Long.toString(dbBeanFileEditor.getId()));
    }


}
