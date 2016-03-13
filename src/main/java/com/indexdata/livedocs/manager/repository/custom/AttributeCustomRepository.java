package com.indexdata.livedocs.manager.repository.custom;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.QAttributeEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;

/**
 * @author Adam Dec
 * @since 0.0.2
 */
@Repository
@Transactional
public class AttributeCustomRepository extends BaseCustomRepository {

    public List<AttributeEntity> getAllAttributesByProfileId(long profileId) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, AttributeEntity.class)
                .from(QAttributeEntity.attributeEntity)
                .where(QAttributeEntity.attributeEntity.profile.id.eq(profileId)).setReadOnly(true)
                .list(QAttributeEntity.attributeEntity);
    }
}
