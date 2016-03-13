package com.indexdata.livedocs.manager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.indexdata.livedocs.manager.core.utils.FileSerializationUtils;

public class FileSerializationUtilsTest {

    @Test
    public void shouldSavePDftoTempFileAndOpen() throws IOException, URISyntaxException {
        // given
        final File file = new File(FileSerializationUtilsTest.class.getResource("/static/pdf/X17433-0000_6842437.pdf")
                .toURI());

        // when
        byte[] bytes = FileSerializationUtils.createBytes(file);
        final File pdfFIle = FileSerializationUtils.createFile(bytes, "pdf");

        // then
        Assert.assertNotNull(pdfFIle);
        // Desktop.getDesktop().open(pdfFIle);
    }
}