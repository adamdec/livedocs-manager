package com.indexdata.livedocs.manager.core.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.indexdata.livedocs.manager.core.utils.DatabaseConnectionUtils;
import com.indexdata.livedocs.manager.repository.FileRepository;
import com.indexdata.livedocs.manager.repository.ProfileRepository;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/common-context.xml", "/spring/jpa-context.xml", "/spring/security-context.xml"
})
@Transactional
@TransactionConfiguration(defaultRollback = false)
public class FileServiceBatchTest extends BaseServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(FileServiceBatchTest.class);
    static {
        DatabaseConnectionUtils.createPostgresConnectionProperties("localhost", "5432");
        System.setProperty("spring.profiles.active", "POSTGRES");
    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private FileRepository fileRespository;

    @PersistenceContext(unitName = "LiveDocsPU")
    protected EntityManager em;

    @Ignore
    @Rollback(true)
    @Test
    public void shouldInsertFilesInBatches() {
        final int batchSize = 100;
        StopWatch watch = new StopWatch();
        watch.start();

        final DiscEntity discEntity = new DiscEntity();
        for (int i = 0; i < 500000; i++) {
            final FileEntity fileEntity = new FileEntity();
            fileEntity.setExtension("ext" + i);
            fileEntity.setPath("path" + i);
            fileEntity.setSize(4096L);
            fileEntity.setField1(new Field("name1", "value1"));
            fileEntity.setField2(new Field("name2", "value2"));
            // discEntity.getFiles().add(fileEntity);
            em.persist(fileEntity);

            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        profileEntity.getDiscs().add(discEntity);

        LOGGER.debug("Persisting profileEntity={}...", profileEntity.getId());
        em.persist(profileEntity);
        watch.stop();
        LOGGER.debug("[BATCH] Persisted profileEntity={} after={} seconds", profileEntity.getId(),
                watch.getTotalTimeSeconds());
    }

    @Ignore
    @Rollback(true)
    @Test
    public void shouldInsertFiles() {
        StopWatch watch = new StopWatch();
        watch.start();

        final DiscEntity discEntity = new DiscEntity();
        for (int i = 0; i < 500000; i++) {
            final FileEntity fileEntity = new FileEntity();
            fileEntity.setExtension("ext" + i);
            fileEntity.setPath("path" + i);
            fileEntity.setSize(4096L);
            fileEntity.setField1(new Field("name1", "value1"));
            fileEntity.setField2(new Field("name2", "value2"));
            // discEntity.getFiles().add(fileEntity);
            fileRespository.save(fileEntity);
        }
        profileEntity.getDiscs().add(discEntity);

        LOGGER.debug("Persisting profileEntity={}...", profileEntity.getId());
        profileRepository.save(profileEntity);
        watch.stop();
        LOGGER.debug("[NORMAL] Persisted profileEntity={} after={} seconds", profileEntity.getId(),
                watch.getTotalTimeSeconds());
    }
}