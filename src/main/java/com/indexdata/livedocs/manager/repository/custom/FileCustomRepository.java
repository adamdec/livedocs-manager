package com.indexdata.livedocs.manager.repository.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.QField;
import com.indexdata.livedocs.manager.repository.domain.QFileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;
import com.indexdata.livedocs.manager.ui.window.profile.disc.ImportDiscWindow;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

/**
 * @author Adam Dec
 * @since 0.0.2
 */
@Repository
@Transactional
public class FileCustomRepository extends BaseCustomRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(FileCustomRepository.class);

    public List<String> getFileAttributeNames(final Long profileId) {
        final Tuple tuple = DaoHibernateUtils
                .getCachedHibernateQuery(em, FileEntity.class)
                .from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.disc.profile.id.eq(profileId))
                .distinct().singleResult(QFileEntity.fileEntity.field1.name, QFileEntity.fileEntity.field2.name,
                        QFileEntity.fileEntity.field3.name, QFileEntity.fileEntity.field4.name,
                        QFileEntity.fileEntity.field5.name, QFileEntity.fileEntity.field6.name,
                        QFileEntity.fileEntity.field7.name, QFileEntity.fileEntity.field8.name,
                        QFileEntity.fileEntity.field9.name, QFileEntity.fileEntity.field10.name);

        if (tuple == null) {
            return Collections.emptyList();
        }

        final List<String> attributeNameList = new ArrayList<String>(ImportDiscWindow.MAPPING_LIMIT);
        for (int i = 0; i < ImportDiscWindow.MAPPING_LIMIT; i++) {
            attributeNameList.add(tuple.get(i, String.class));
        }
        return attributeNameList;
    }

    public long getAllFilesCount(final Long profileId) {
        return DaoHibernateUtils.getCachedHibernateQuery(em, FileEntity.class).from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.disc.profile.id.eq(profileId)).count();
    }

    public long getAllFilesCount(final Long profileId, final List<QueryFilter> queryFilterList) {
        final HibernateQuery hibernateQuery = DaoHibernateUtils.getCachedHibernateQuery(em, FileEntity.class)
                .from(QFileEntity.fileEntity).where(QFileEntity.fileEntity.disc.profile.id.eq(profileId));
        applyFiltering(hibernateQuery, profileId, null, queryFilterList);
        return hibernateQuery.count();
    }

    public List<FileEntity> getAllFilesByProfileId(final Long profileId) {
        final HibernateQuery hibernateQuery = DaoHibernateUtils.getCachedHibernateQuery(em, FileEntity.class)
                .from(QFileEntity.fileEntity).where(QFileEntity.fileEntity.disc.profile.id.eq(profileId));
        return hibernateQuery.list(QFileEntity.fileEntity);
    }

    public List<FileEntity> getAllFilesByDiscId(final Long discId) {
        final HibernateQuery hibernateQuery = DaoHibernateUtils.getCachedHibernateQuery(em, FileEntity.class)
                .from(QFileEntity.fileEntity).where(QFileEntity.fileEntity.disc.id.eq(discId));
        return hibernateQuery.list(QFileEntity.fileEntity);
    }

    public List<FileEntity> getAllFiles(final Long profileId, final PageRequest pageRequest) {
        final HibernateQuery hibernateQuery = DaoHibernateUtils
                .getCachedHibernateQuery(em, pageRequest, FileEntity.class).from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.disc.profile.id.eq(profileId));

        if (pageRequest.getSort() != null) {
            final PathBuilder<FileEntity> entityPath = new PathBuilder<FileEntity>(FileEntity.class, "fileEntity");
            final Order order = pageRequest.getSort().iterator().next();
            final QField sortField = new QField(entityPath.get(new StringPath(order.getProperty())).getMetadata());
            hibernateQuery.orderBy(order.getDirection() == Direction.ASC ? sortField.value.asc() : sortField.value
                    .desc());
        }

        LOGGER.debug("Query expression to be applied\n{}", hibernateQuery);
        return hibernateQuery.list(QFileEntity.fileEntity);
    }

    public List<FileEntity> getAllFiles(final Long profileId, final PageRequest pageRequest,
            final List<QueryFilter> queryFilterList) {
        final HibernateQuery hibernateQuery = DaoHibernateUtils.getCachedHibernateQuery(em, pageRequest,
                FileEntity.class).from(QFileEntity.fileEntity);
        applyFiltering(hibernateQuery, profileId, pageRequest, queryFilterList);
        return hibernateQuery.list(QFileEntity.fileEntity);
    }

    private void applyFiltering(final HibernateQuery hibernateQuery, final Long profileId,
            final PageRequest pageRequest, final List<QueryFilter> queryFilterList) {
        final PathBuilder<FileEntity> entityPath = new PathBuilder<FileEntity>(FileEntity.class, "fileEntity");
        final BooleanBuilder filterBuilder = new BooleanBuilder(QFileEntity.fileEntity.disc.profile.id.eq(profileId));
        int count = 1;
        for (QueryFilter queryFilter : queryFilterList) {
            if (queryFilter.getColumn() == null) {
                count++;
                continue;
            }

            final QField filterField = new QField(entityPath.get(new StringPath("field" + count)).getMetadata());
            switch (queryFilter.getFilterComparisonType()) {
            case EQUALS:
                filterBuilder.and(filterField.name.eq(queryFilter.getColumn())).and(
                        filterField.value.equalsIgnoreCase(queryFilter.getValue()));
                break;
            case LIKE:
                filterBuilder.and(filterField.name.eq(queryFilter.getColumn())).and(
                        filterField.value.like(queryFilter.getValue().toUpperCase()));
                break;
            default:
                break;
            }
            count++;
        }

        hibernateQuery.where(filterBuilder);

        if (pageRequest != null && pageRequest.getSort() != null) {
            final Order order = pageRequest.getSort().iterator().next();
            final QField sortField = new QField(entityPath.get(new StringPath(order.getProperty())).getMetadata());
            hibernateQuery.orderBy(order.getDirection() == Direction.ASC ? sortField.value.asc() : sortField.value
                    .desc());
        }

        LOGGER.debug("Query expression to be applied\n{}", hibernateQuery);
    }
}