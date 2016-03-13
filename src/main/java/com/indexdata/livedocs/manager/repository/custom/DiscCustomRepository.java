package com.indexdata.livedocs.manager.repository.custom;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.QDiscEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;

/**
 * @author Adam Dec
 * @since 0.0.2
 */
@Repository
@Transactional
public class DiscCustomRepository extends BaseCustomRepository {

    public List<DiscEntity> getAllDiscsByProfileId(final Long profileId) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, DiscEntity.class).from(QDiscEntity.discEntity)
                .where(QDiscEntity.discEntity.profile.id.eq(profileId)).setReadOnly(true).list(QDiscEntity.discEntity);
    }

    public long countAllDiscsByProfileId(final Long profileId) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, DiscEntity.class).from(QDiscEntity.discEntity)
                .where(QDiscEntity.discEntity.profile.id.eq(profileId)).setReadOnly(true).count();
    }
}