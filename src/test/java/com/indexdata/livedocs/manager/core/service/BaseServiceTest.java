package com.indexdata.livedocs.manager.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.indexdata.livedocs.manager.repository.ProfileRepository;
import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;

public class BaseServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(BaseServiceTest.class);

    public static final int FILE_COUNT = 20;
    public static final int ATTRIBUTE_COUNT = 2;

    @Autowired
    private ProfileRepository profileRepository;

    protected ProfileEntity profileEntity;

    @Before
    public void init() {
        this.profileEntity = createProfile(1L);
    }

    protected ProfileEntity createProfile(long id) {
        ProfileEntity localProfileEntity = profileRepository.findOne(id);
        if (localProfileEntity == null) {
            localProfileEntity = new ProfileEntity();
            localProfileEntity.setProfileName("TestProfile-" + id);
            localProfileEntity.setDateCreated(new Date());
            localProfileEntity.setTotalDocumentsNumber(1L);
            localProfileEntity.setTotalDocumentsSize(1L);

            final List<AttributeEntity> attributes = new ArrayList<AttributeEntity>(ATTRIBUTE_COUNT);
            AttributeEntity attribute1 = new AttributeEntity();
            attribute1.setAttributeName("Name");
            attribute1.setDateCreated(new Date());
            attribute1.setMapped(true);
            attribute1.setProfile(localProfileEntity);
            attributes.add(attribute1);

            AttributeEntity attribute2 = new AttributeEntity();
            attribute2.setAttributeName("SurName");
            attribute2.setDateCreated(new Date());
            attribute2.setMapped(true);
            attribute2.setProfile(localProfileEntity);
            attributes.add(attribute2);

            localProfileEntity.setAttributes(attributes);

            final DiscEntity disc1 = new DiscEntity();
            disc1.setBatchNumber("Batch1-" + id);
            disc1.setDateImported(new Date());
            disc1.setDiscTitle("Title");
            disc1.setFilesNumber(10L);
            disc1.setFilesSize(10L);
            disc1.setProfile(localProfileEntity);

            final List<FileEntity> files1 = new ArrayList<FileEntity>(10);
            FileEntity file = null;
            for (int k = 0; k < FILE_COUNT; k++) {
                file = new FileEntity();
                file.setDisc(disc1);
                file.setExtension("extension");
                file.setPath("path");
                file.setSize(100L);
                if (k == 0) {
                    file.setField1(new Field("Name", "ADAM"));
                    file.setField2(new Field("SurName", "DEC"));
                }
                else {
                    file.setField1(new Field("Name", "ADAM" + rndChar()));
                    file.setField2(new Field("SurName", "DEC" + rndChar()));
                }
                files1.add(file);
            }
            disc1.setFiles(files1);

            DiscEntity disc2 = new DiscEntity();
            disc2.setBatchNumber("Batch2-" + id);
            disc2.setDateImported(new Date());
            disc2.setDiscTitle("Title");
            disc2.setFilesNumber(10L);
            disc2.setFilesSize(10L);
            disc2.setProfile(localProfileEntity);

            List<FileEntity> files2 = new ArrayList<FileEntity>(10);
            for (int k = 0; k < FILE_COUNT; k++) {
                file = new FileEntity();
                file.setDisc(disc2);
                file.setExtension("extension");
                file.setPath("path");
                file.setSize(100L);
                if (k == 0) {
                    file.setField1(new Field("Name", "KASIA"));
                    file.setField2(new Field("SurName", "KRZYZAK"));
                }
                else {
                    file.setField1(new Field("Name", "KASIA" + rndChar()));
                    file.setField2(new Field("SurName", "KRZYZAK" + rndChar()));
                }
                files2.add(file);
            }
            disc2.setFiles(files2);

            final List<DiscEntity> discs = new ArrayList<DiscEntity>(2);
            discs.add(disc1);
            discs.add(disc2);
            localProfileEntity.setDiscs(discs);
            LOGGER.debug("Persisted profile with id={}", profileRepository.save(localProfileEntity).getId());
        }
        return localProfileEntity;
    }

    private static char rndChar() {
        int rnd = (int) (Math.random() * 52);
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);
    }
}