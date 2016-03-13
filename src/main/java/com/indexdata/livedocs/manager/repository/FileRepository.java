package com.indexdata.livedocs.manager.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.FileEntity;

/**
 * @author Adam Dec
 * @since 0.0.6
 */
@Transactional
public interface FileRepository extends PagingAndSortingRepository<FileEntity, Long>,
        JpaSpecificationExecutor<FileEntity> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "FileEntity")
    })
    FileEntity getFileEntityByPath(final String path);
}