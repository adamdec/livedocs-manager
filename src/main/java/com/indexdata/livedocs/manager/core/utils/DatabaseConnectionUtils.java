package com.indexdata.livedocs.manager.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseConnectionUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionUtils.class);

    private static final String LIVEDOCS_MANAGER_DB_PROPERTIES = "livedocs_manager_db.properties";
    public static final String IP_ADDRESS = "IP_ADDRESS";
    public static final String PORT = "PORT";

    private DatabaseConnectionUtils() {
    }

    public static void createPostgresConnectionProperties(final String ipAddress, final String port) {
        System.setProperty("jdbc.serverName", ipAddress);
        System.setProperty("jdbc.portNumber", port);
    }

    public static String createH2Url(final String ipAddress, final String port) {
        return "jdbc:h2:tcp://" + ipAddress + ":" + port
                + "/live_docs_data;FILE_LOCK=SOCKET;AUTO_SERVER=TRUE";
    }

    public static String getIPAddres() {
        final Properties properties = new Properties();
        final File tempDir = FileUtils.getTempDirectory();
        final File file = new File(tempDir, LIVEDOCS_MANAGER_DB_PROPERTIES);
        try {
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            properties.setProperty(IP_ADDRESS, "127.0.0.1");
        }
        return properties.getProperty(IP_ADDRESS);
    }

    public static String getPort() {
        final Properties properties = new Properties();
        final File tempDir = FileUtils.getTempDirectory();
        final File file = new File(tempDir, LIVEDOCS_MANAGER_DB_PROPERTIES);
        try {
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            properties.setProperty(PORT, "9092");
        }
        return properties.getProperty(PORT);
    }

    public static void storeProperties(String ipAddress, String port) {
        final File tempDir = FileUtils.getTempDirectory();
        final Properties properties = new Properties();
        properties.setProperty(IP_ADDRESS, ipAddress);
        properties.setProperty(PORT, port);

        final File file = new File(tempDir, LIVEDOCS_MANAGER_DB_PROPERTIES);
        if (file.exists()) {
            if (!file.delete()) {
                LOGGER.warn("Could not delete {} file in a temp directory", LIVEDOCS_MANAGER_DB_PROPERTIES);
            }
        }

        try {
            if (file.createNewFile()) {
                LOGGER.debug("Created new file={}", file.getPath());
            }
        } catch (IOException e) {
            LOGGER.error("Could not create {} file in a temp directory", LIVEDOCS_MANAGER_DB_PROPERTIES, e);
        }

        try {
            properties.store(new FileOutputStream(file), "Comment=" + ipAddress + ":" + port);
        } catch (Exception e) {
            LOGGER.error("Could not save {} file in a temp directory", LIVEDOCS_MANAGER_DB_PROPERTIES, e);
        }
    }
}