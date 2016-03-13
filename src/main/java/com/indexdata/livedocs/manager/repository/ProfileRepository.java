package com.indexdata.livedocs.manager.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;

/**
 * @author Adam Dec
 * @since 0.0.1
 */
@Transactional
public interface ProfileRepository extends PagingAndSortingRepository<ProfileEntity, Long>,
        JpaSpecificationExecutor<ProfileEntity> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "ProfileEntity")
    })
    ProfileEntity getProfileEntityById(final long id);
}
