package com.indexdata.livedocs.manager.core;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.indexdata.livedocs.manager.core.utils.FileSizeFormatter;

public class SambaManagerTest {

    static {
        System.setProperty("jcifs.smb.client.responseTimeout", "3000");
        System.setProperty("jcifs.resolveOrder", "DNS");
        System.setProperty("jcifs.util.loglevel", "2");
        System.setProperty("jcifs.smb.client.useExtendedSecurity", "false");
    }

    @Ignore
    @Test
    public void shouldCopyFile() throws Exception {
        // given
        final String login = "admin";
        final String password = "admin";
        final String server = "192.168.0.104";
        final String subDirs = "/DIR1/DIR2";
        final String obfuscatedFileName = "obfuscatedFileName";
        final SambaManager sambaManager = new SambaManager(login, password, server);

        // when
        final File fileToCopy = new File(SambaManagerTest.class.getResource("/static/pdf/X17433-0000_6842437.pdf")
                .toURI());
        sambaManager.copyFileToServer(fileToCopy, subDirs, obfuscatedFileName);

        // then
        long size = sambaManager.size();
        System.out.println(size);
        sambaManager.deleteFiles(subDirs);
    }

    @Test
    public void shouldCalculateDirectorySize() throws Exception {
        // given
        final String login = "admin";
        final String password = "admin";
        final String server = "192.168.0.108";
        final SambaManager sambaManager = new SambaManager(login, password, server);

        // when
        final long size = sambaManager.size();

        System.out.println(FileSizeFormatter.formatSize(size));

        // then
        Assert.assertTrue(size > 0);
    }
}
