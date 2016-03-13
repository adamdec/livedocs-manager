package com.indexdata.livedocs.manager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.repository.FileRepository;
import com.indexdata.livedocs.manager.repository.custom.FileCustomRepository;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;

/**
 * This class will provide service layer for file domain.
 * 
 * @author Adam Dec
 * @since 0.0.6
 */
@Service
@Transactional
public class FileService extends BaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileCustomRepository fileCustomRepository;

    @Autowired
    private ConversionService conversionService;

    public long count() {
        try {
            return fileRepository.count();
        } finally {
            logCacheStats();
        }
    }

    public FileEntity getFileByFileId(long fileId) {
        try {
            return fileRepository.findOne(fileId);
        } finally {
            logCacheStats();
        }
    }

    public long getAllFilesCount(final Long profileId) {
        try {
            return fileCustomRepository.getAllFilesCount(profileId);
        } finally {
            logCacheStats();
        }
    }

    public long getAllFilesCountWithFilter(final Long profileId, final List<QueryFilter> queryList) {
        try {
            if (queryList.isEmpty()) {
                return getAllFilesCount(profileId);
            }
            else {
                return fileCustomRepository.getAllFilesCount(profileId, queryList);
            }
        } catch (Exception e) {
            LOGGER.error("Could not get files", e);
            return -1L;
        } finally {
            logCacheStats();
        }
    }

    public List<File> getAllFiles(final Long profileId, final FilePageRequest filePageRequest,
            final List<QueryFilter> queryList) {
        try {
            List<FileEntity> fileEntityList = null;
            try {
                final PageRequest pageRequest = new PageRequest(filePageRequest.getOffset(),
                        filePageRequest.getLimit(), filePageRequest.getOrder().getDirection(), filePageRequest
                                .getOrder().getProperty());
                if (queryList.isEmpty()) {
                    fileEntityList = fileCustomRepository.getAllFiles(profileId, pageRequest);
                }
                else {
                    fileEntityList = fileCustomRepository.getAllFiles(profileId, pageRequest, queryList);
                }
            } catch (Exception e) {
                LOGGER.error("Could not get files", e);
                fileEntityList = Collections.emptyList();
            }

            final List<File> fileList = new ArrayList<File>(fileEntityList.size());
            for (FileEntity fileEntity : fileEntityList) {
                fileList.add(conversionService.convert(fileEntity, File.class));
            }
            return fileList;
        } finally {
            logCacheStats();
        }
    }

    public List<File> getAllFilesByDiscId(final Long discId) {
        try {
            final List<FileEntity> fileEntityList = fileCustomRepository.getAllFilesByDiscId(discId);
            final List<File> fileList = new ArrayList<File>(fileEntityList.size());
            for (FileEntity fileEntity : fileEntityList) {
                fileList.add(conversionService.convert(fileEntity, File.class));
            }
            return fileList;
        } finally {
            logCacheStats();
        }
    }

    private void logCacheStats() {
        if (ManagerConfiguration.traceCacheStatistics()) {
            int count = counter.incrementAndGet();
            DaoHibernateUtils.printStats(em, FileEntity.class, count);
        }
    }
}
