package com.indexdata.livedocs.manager.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class FileSerializationUtils {

    public static byte[] createBytes(final File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    public static File createFile(final byte[] data, final String extension)
            throws FileNotFoundException, IOException {
        final File file = File.createTempFile("data-" + UUID.randomUUID().toString().substring(0, 16), "."
                + extension);
        FileUtils.writeByteArrayToFile(file, data);
        return file;
    }
}