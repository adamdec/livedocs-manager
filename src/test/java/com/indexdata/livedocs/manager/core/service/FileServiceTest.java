package com.indexdata.livedocs.manager.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.utils.AttributeNameValue;
import com.indexdata.livedocs.manager.repository.utils.QueryUtils;
import com.indexdata.livedocs.manager.service.AttributeService;
import com.indexdata.livedocs.manager.service.FileService;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/common-context.xml", "/spring/jpa-test-context.xml", "/spring/security-context.xml"
})
@Transactional
@TransactionConfiguration(defaultRollback = false)
public class FileServiceTest extends BaseServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(FileServiceRealTest.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private QueryUtils queryListUtils;

    @Rollback(true)
    @Test
    public void shouldGetFilesWithFilterOnOneAttributeAfterFirstAttributRemove() {
        // given
        final Long profileId = profileEntity.getId();
        Long attributeId = null;
        for (AttributeEntity attr : profileEntity.getAttributes()) {
            if (attr.getAttributeName().equals("Name")) {
                attributeId = attr.getId();
            }
        }

        // when
        attributeService.removeAttributeByAttributeIdAndAttributeName(attributeId, "Name", profileId);

        // then
        FilePageRequest pageRequest = new FilePageRequest(0, 20, new Order(Direction.ASC, "field2"));
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(2);
        keyValueList.add(new AttributeNameValue("SurName", "Dec*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));
        Assert.assertTrue(fileList.size() == 20);

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
    }

    @Rollback(false)
    @Test
    public void shouldGetFilesWithFilterOnTwoAttributesAfterAttributeNameChange() {
        // given
        final Long profileId = profileEntity.getId();
        Long attributeId = null;
        for (AttributeEntity attr : profileEntity.getAttributes()) {
            if (attr.getAttributeName().equals("Name")) {
                attributeId = attr.getId();
            }
        }

        attributeService.changeAttributeNameByAttributeNameAndProfileId("name", attributeId, profileId);

        FilePageRequest pageRequest = new FilePageRequest(0, 21);
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(2);
        keyValueList.add(new AttributeNameValue("name", "Adam*"));
        keyValueList.add(new AttributeNameValue("SurName", "Dec*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertTrue(fileList.size() == 20);

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }

        attributeService.changeAttributeNameByAttributeNameAndProfileId("Name", attributeId, profileId);
    }

    @Test
    public void shouldGetFilesWithFilterSortedBySurname() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 20, new Order(Direction.ASC, "field2"));
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(2);
        keyValueList.add(new AttributeNameValue("Name", "Adam*"));
        keyValueList.add(new AttributeNameValue("SurName", "Dec*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertTrue(fileList.size() == 20);

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
    }

    @Test
    public void shouldGetFilesWithFilterOnTwoAttributes() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 21);
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(2);
        keyValueList.add(new AttributeNameValue("Name", "Adam*"));
        keyValueList.add(new AttributeNameValue("SurName", "Dec*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertTrue(fileList.size() == 20);

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
    }

    @Test
    public void shouldGetFilesWithPagination_0_5AndFilter() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 5);
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(1);
        keyValueList.add(new AttributeNameValue("Name", "Adam"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertTrue(fileList.size() == 1);
        Assert.assertEquals(1, fileList.get(0).getId().intValue());

        for (File file : fileList) {
            LOGGER.debug(file.getId() + " - " + file.getFields());
        }
    }

    @Test
    public void shouldGetFilesWithPagination_0_5AndFilterExpression() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 5);
        final List<AttributeNameValue> keyValueList = new ArrayList<AttributeNameValue>(1);
        keyValueList.add(new AttributeNameValue("Name", "Adam*"));

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest,
                queryListUtils.preserveAttributeNameOrdering(queryListUtils.createFilterList(keyValueList), profileId));

        // then
        Assert.assertTrue(fileList.size() == 5);
        Assert.assertEquals(1, fileList.get(0).getId().intValue());
        Assert.assertEquals(2, fileList.get(1).getId().intValue());
        Assert.assertEquals(3, fileList.get(2).getId().intValue());
        Assert.assertEquals(4, fileList.get(3).getId().intValue());
        Assert.assertEquals(5, fileList.get(4).getId().intValue());

        for (File file : fileList) {
            System.out.println(file.getId() + " - " + file.getFields());
        }
    }

    @Test
    public void shouldGetFilesWithPagination_0_2() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 2);

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest, Collections.emptyList());

        // then
        Assert.assertTrue(fileList.size() == 2);
        Assert.assertEquals(1, fileList.get(0).getId().intValue());
        Assert.assertEquals(2, fileList.get(1).getId().intValue());
    }

    @Test
    public void shouldGetFilesWithPagination_1_2() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(1, 2);

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest, Collections.emptyList());

        // then
        Assert.assertTrue(fileList.size() == 2);
        Assert.assertEquals(3, fileList.get(0).getId().intValue());
        Assert.assertEquals(4, fileList.get(1).getId().intValue());
    }

    @Test
    public void shouldGetFilesWithPagination_0_21() {
        // given
        final Long profileId = profileEntity.getId();
        FilePageRequest pageRequest = new FilePageRequest(0, 41);

        // when
        final List<File> fileList = fileService.getAllFiles(profileId, pageRequest, Collections.emptyList());

        // then
        Assert.assertTrue(fileList.size() == 40);
        Assert.assertEquals(39, fileList.get(38).getId().intValue());
        Assert.assertEquals(40, fileList.get(39).getId().intValue());
    }

    @Test
    public void shouldCountAllFiles() {
        // given
        final Long profileId = profileEntity.getId();

        // when
        long filesCount = fileService.getAllFilesCount(profileId);

        // then
        Assert.assertEquals(40, filesCount);
    }
}
