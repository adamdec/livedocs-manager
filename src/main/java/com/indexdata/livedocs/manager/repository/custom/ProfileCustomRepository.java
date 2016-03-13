package com.indexdata.livedocs.manager.repository.custom;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.repository.domain.QProfileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;

/**
 * @author Adam Dec
 * @since 0.0.2
 */
@Repository
@Transactional
public class ProfileCustomRepository extends BaseCustomRepository {

    public List<ProfileEntity> getAllProfilesSortedByName() {
        return DaoHibernateUtils.getCachedHibernateQuery(em, ProfileEntity.class).from(QProfileEntity.profileEntity)
                .orderBy(QProfileEntity.profileEntity.profileName.asc()).setReadOnly(true)
                .list(QProfileEntity.profileEntity);
    }

    public ProfileEntity getProfileByName(final String profileName) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, ProfileEntity.class).from(QProfileEntity.profileEntity)
                .where(QProfileEntity.profileEntity.profileName.eq(profileName)).setReadOnly(true)
                .singleResult(QProfileEntity.profileEntity);
    }

    public ProfileEntity getProfileById(final long profileId) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, ProfileEntity.class).from(QProfileEntity.profileEntity)
                .where(QProfileEntity.profileEntity.id.eq(profileId)).setReadOnly(true)
                .singleResult(QProfileEntity.profileEntity);
    }
}
