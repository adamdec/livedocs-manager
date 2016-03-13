package com.indexdata.livedocs.manager.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;

/**
 * @author Adam Dec
 * @since 1.0.0
 */
@Transactional
public interface AttributeRepository extends PagingAndSortingRepository<AttributeEntity, Long>,
        JpaSpecificationExecutor<AttributeEntity> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "AttributeEntity")
    })
    AttributeEntity getAttributeEntityByIdAndProfileId(final Long attributeId, Long profileId);
}
