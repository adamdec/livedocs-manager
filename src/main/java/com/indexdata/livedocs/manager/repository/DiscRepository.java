package com.indexdata.livedocs.manager.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.DiscEntity;

/**
 * @author Adam Dec
 * @since 0.0.1
 */
@Transactional
public interface DiscRepository extends PagingAndSortingRepository<DiscEntity, Long>,
        JpaSpecificationExecutor<DiscEntity> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "DiscEntity")
    })
    DiscEntity getDiscEntityByBatchNumber(String batchNumber);
}