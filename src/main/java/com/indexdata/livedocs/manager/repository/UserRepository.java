package com.indexdata.livedocs.manager.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.UserEntity;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Transactional
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "UserEntity")
    })
    UserEntity getUserByUserName(String userName);
}
