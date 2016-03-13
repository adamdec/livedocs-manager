package com.indexdata.livedocs.manager.core.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.core.SambaManager;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;

/**
 * @author Adam Dec
 * @since 0.0.6
 */
public class DiscFileUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(DiscFileUtils.class);

    private static volatile SambaManager SAMBA_MANAGER;

    public static long getFreeSpaceOnMappedDrive() throws Exception {
        return getSambaManager().size();
    }

    public static DiscFileData copyDiscFile(final String srcDir, final String indexDataFilePath,
            final String batchNumber) {
        final File fileToCopy = new File(srcDir, indexDataFilePath);
        if (!fileToCopy.exists() || !fileToCopy.isFile()) {
            LOGGER.error("Could not locate file [{}]", fileToCopy.getPath());
            return null;
        }

        // Takes only first part of original directory and appends batch number
        // So if 'data' directory from original disc contains several sub-directories then all will be copied to 1 dir
        final String obfuscatedFileSubDir = "/" + split(indexDataFilePath) + "/"
                + batchNumber.replaceAll("\\r\\n|\\r|\\n", "").replaceAll(" ", "");
        final String obfuscatedFileName = new File(FilenameUtils.removeExtension(fileToCopy.getPath())).getName();

        // Copy file
        try {
            if (getSambaManager().copyFileToServer(fileToCopy, obfuscatedFileSubDir, obfuscatedFileName)) {
                return new DiscFileData(FileUtils.sizeOf(fileToCopy), obfuscatedFileSubDir + "/" + obfuscatedFileName,
                        FilenameUtils.getExtension(indexDataFilePath));
            }
        } catch (Exception e) {
            LOGGER.error("Could not copy file [{}] to [{}]\ndue to error={}", fileToCopy.getPath(),
                    fileToCopy.getPath(), e.getMessage());

        }
        return null;
    }

    public static File openDiscFile(final String filePath, final String extension) throws IOException {
        final byte[] copyFileFromServer = getSambaManager().copyFileFromServer(filePath);
        if (copyFileFromServer == null) {
            return null;
        }
        final File deObfuscatedFile = FileSerializationUtils.createFile(copyFileFromServer, extension);
        deObfuscatedFile.deleteOnExit();
        return deObfuscatedFile;
    }

    public static long calculateDiscSize(final File directory) throws Exception {
        final SizeCounter counter = new SizeCounter();
        directory.listFiles(counter);
        LOGGER.debug("Calculated size of directory={}", counter.getTotal());
        return counter.getTotal();
    }

    public static void deleteDiscFiles(final List<com.indexdata.livedocs.manager.service.model.File> fileList) {
        if (fileList.size() == 0) {
            return;
        }

        String subPath = "";
        for (com.indexdata.livedocs.manager.service.model.File file : fileList) {
            String[] pathParts = file.getPath().split("/");
            if (pathParts.length == 1) {
                pathParts = file.getPath().split("\\\\");
            }

            if (!subPath.equals(pathParts[1])) {
                try {
                    subPath = pathParts[1];
                    if (getSambaManager().deleteFiles(subPath)) {
                        LOGGER.debug("Deleted directory={}", SambaManager.getFileUploadFolder() + "/" + pathParts[1]);
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not delete directory={}", SambaManager.getFileUploadFolder() + "/"
                            + pathParts[1], e);
                }
            }
        }
    }

    private static String split(final String filepath) {
        if (filepath.contains("\\")) {
            return filepath.split("\\\\")[0].replaceAll(" ", "_");
        }
        if (filepath.contains("/")) {
            return filepath.split("/")[0].replaceAll(" ", "_");
        }
        return filepath;
    }

    public static SambaManager getSambaManager() {
        if (SAMBA_MANAGER == null) {
            synchronized (DiscFileUtils.class) {
                if (SAMBA_MANAGER == null) {
                    SAMBA_MANAGER = new SambaManager(LiveDocsResourceManager.getSambaLogin(),
                            LiveDocsResourceManager.getSambaPassword(), LiveDocsResourceManager.getSambaServer());
                }
            }
        }
        return SAMBA_MANAGER;
    }

    public static void removeSambaManager() {
        SAMBA_MANAGER = null;
    }

    public static class SizeCounter implements FileFilter {

        private long total = 0;

        public SizeCounter() {
        }

        public boolean accept(File pathname) {
            if (pathname.isFile()) {
                total += pathname.length();
            }
            else {
                pathname.listFiles(this);
            }
            return false;
        }

        public long getTotal() {
            return total;
        }
    }
}