package com.indexdata.livedocs.manager.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.ListUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.core.utils.DatabaseConnectionUtils;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.QFileEntity;
import com.indexdata.livedocs.manager.repository.utils.AttributeNameValue;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.repository.utils.QueryUtils;
import com.indexdata.livedocs.manager.service.FileService;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;
import com.mysema.query.Tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/common-context.xml", "/spring/jpa-context.xml", "/spring/security-context.xml"
})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class FileServiceRealTest {

    private static Logger LOGGER = LoggerFactory.getLogger(FileServiceTest.class);
    static {
        System.setProperty("jdbc.url", DatabaseConnectionUtils.createH2Url("127.0.0.1", "9092"));
        System.setProperty("spring.profiles.active", "H2");
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private QueryUtils queryListUtils;

    @PersistenceContext
    protected EntityManager em;

    @Ignore
    @Test
    public void shouldGetFilesWithFilterOnFirstColumnAndSortedByField1Asc() {
        // given
        final Long profileId = 2L;
        final FilePageRequest pageRequest = new FilePageRequest(0, ManagerConfiguration.getGridPaginationLimit(),
                new Order(Direction.ASC, "field1"));
        final List<AttributeNameValue> keyValueList = Arrays.asList(new AttributeNameValue("Job No.", "*0001"),
                new AttributeNameValue());

        // when

        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertFalse(fileList.isEmpty());

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
        LOGGER.debug("Size={}", fileList.size());
    }

    @Ignore
    @Test
    public void shouldGetFilesWithFilterOnTwoColumnsAndSortedByField2Desc() {
        // given
        final Long profileId = 2L;
        final FilePageRequest pageRequest = new FilePageRequest(0, ManagerConfiguration.getGridPaginationLimit(),
                new Order(Direction.DESC, "field2"));
        final List<AttributeNameValue> keyValueList = Arrays.asList(new AttributeNameValue("Job No.", "*0000"),
                new AttributeNameValue("Item No.", "680*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertFalse(fileList.isEmpty());

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
        LOGGER.debug("Size={}", fileList.size());
    }

    @Ignore
    @Test
    public void shouldGetFilesWithFilterOnTwoColumnsAndSortedByField2DescPaginated() {
        // given
        final Long profileId = 2L;
        final FilePageRequest pageRequest1 = new FilePageRequest(0, ManagerConfiguration.getGridPaginationLimit(),
                new Order(Direction.DESC, "field2"));
        final FilePageRequest pageRequest2 = new FilePageRequest(1, ManagerConfiguration.getGridPaginationLimit(),
                new Order(Direction.DESC, "field2"));
        final List<AttributeNameValue> keyValueList = Arrays.asList(new AttributeNameValue("Job No.", "*0000"),
                new AttributeNameValue("Item No.", "680*"));

        // when
        final List<File> fileList1 = fileService.getAllFiles(profileId, pageRequest1,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));
        final List<File> fileList2 = fileService.getAllFiles(profileId, pageRequest2,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertFalse(fileList1.isEmpty());
        Assert.assertFalse(fileList2.isEmpty());

        @SuppressWarnings("unchecked")
        final List<File> sum = ListUtils.sum(fileList1, fileList2);
        for (File file : sum) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
        LOGGER.debug("Size={}", sum.size());
    }

    @Ignore
    @Test
    public void shouldGetAttibuteNames() {
        final Tuple tuple = DaoHibernateUtils
                .getCachedHibernateQuery(em, FileEntity.class)
                .from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.disc.profile.id.eq(2L))
                .distinct()
                .singleResult(QFileEntity.fileEntity.field1.name, QFileEntity.fileEntity.field2.name,
                        QFileEntity.fileEntity.field3.name, QFileEntity.fileEntity.field4.name,
                        QFileEntity.fileEntity.field5.name, QFileEntity.fileEntity.field6.name,
                        QFileEntity.fileEntity.field7.name, QFileEntity.fileEntity.field8.name,
                        QFileEntity.fileEntity.field9.name, QFileEntity.fileEntity.field10.name);

        List<String> attributeNameList = new ArrayList<String>(10);
        for (int i = 0; i < 10; i++) {
            LOGGER.debug(tuple.get(i, String.class));
            attributeNameList.add(tuple.get(i, String.class));
        }
    }
}
