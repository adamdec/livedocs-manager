package com.indexdata.livedocs.manager.repository.utils;

import java.util.Arrays;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import com.indexdata.livedocs.manager.core.utils.DateConversionUtils;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.jpa.hibernate.HibernateUtil;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpression;

/**
 * @author Adam Dec
 * @since 0.0.3
 */
public final class DaoHibernateUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(DaoHibernateUtils.class);

    private DaoHibernateUtils() {
    }

    public static void printStats(EntityManager em, Class<?> entityCLass, int i) {
        final Statistics statistics = getSession(em).getSessionFactory().getStatistics();
        StringBuilder sb = new StringBuilder();
        sb.append("\n***** " + i + " *****");
        sb.append("\n" + Arrays.toString(statistics.getSecondLevelCacheRegionNames()));
        sb.append("\n" + Arrays.toString(statistics.getEntityNames()));
        sb.append("\n*********************");
        sb.append("\nEntity Fetch Count=" + statistics.getEntityFetchCount());
        sb.append("\nSecond Level Hit Count=" + statistics.getSecondLevelCacheHitCount());
        sb.append("\nSecond Level Miss Count=" + statistics.getSecondLevelCacheMissCount());
        sb.append("\nSecond Level Put Count=" + statistics.getSecondLevelCachePutCount());

        sb.append("\n***** QueryStatistics *****");
        sb.append("\nQueryCacheHitCount=" + statistics.getQueryCacheHitCount());
        sb.append("\nQueryCacheMissCount=" + statistics.getQueryCacheMissCount());
        sb.append("\nQueryCachePutCount=" + statistics.getQueryCachePutCount());
        sb.append("\nQueryExecutionCount=" + statistics.getQueryExecutionCount());
        sb.append("\nQueryExecutionMaxTime=" + statistics.getQueryExecutionMaxTime());
        sb.append("\nQueryExecutionMaxTimeQueryString=" + statistics.getQueryExecutionMaxTimeQueryString());

        sb.append("\n***** SecondLevelCacheStatistics *****");
        final SecondLevelCacheStatistics secondLevelCacheStatistics = statistics
                .getSecondLevelCacheStatistics(entityCLass.getSimpleName());
        sb.append("\n" + entityCLass.getSimpleName() + " - ElementCountInMemory="
                + secondLevelCacheStatistics.getElementCountInMemory());
        sb.append("\n" + entityCLass.getSimpleName() + " - ElementCountOnDisk="
                + secondLevelCacheStatistics.getElementCountOnDisk());
        sb.append("\n" + entityCLass.getSimpleName() + " - Hit Count=" + secondLevelCacheStatistics.getHitCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - Miss Count=" + secondLevelCacheStatistics.getMissCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - Put Count=" + secondLevelCacheStatistics.getPutCount());

        sb.append("\n***** EntityStatistics *****");
        final EntityStatistics entityStatistics = statistics.getEntityStatistics(entityCLass.getName());
        sb.append("\n" + entityCLass.getSimpleName() + " - DeleteCount=" + entityStatistics.getDeleteCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - FetchCount=" + entityStatistics.getFetchCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - InsertCount=" + entityStatistics.getInsertCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - LoadCount=" + entityStatistics.getLoadCount());
        sb.append("\n" + entityCLass.getSimpleName() + " - UpdateCount=" + entityStatistics.getUpdateCount());

        LOGGER.debug(sb.toString());
    }

    /**
     * Return Hibernate Query with cache capabilities
     * 
     * @param entityManager
     * @param cacheRegion
     * @return HibernateQuery
     */
    public static HibernateQuery getCachedHibernateQuery(EntityManager entityManager, Class<?> entityCLass) {
        final HibernateQuery query = new HibernateQuery(getSession(entityManager));
        query.setCacheable(true);
        query.setCacheRegion(entityCLass.getSimpleName());
        return query;
    }

    /**
     * Return Hibernate Query with cache capabilities
     * 
     * @param entityManager
     * @return HibernateQuery
     */
    public static HibernateQuery getCachedHibernateQuery(EntityManager entityManager) {
        final HibernateQuery query = new HibernateQuery(getSession(entityManager));
        query.setCacheable(true);
        return query;
    }

    /**
     * Return Hibernate Query
     * 
     * @param entityManager
     * @return HibernateQuery
     */
    public static HibernateQuery getHibernateQuery(EntityManager entityManager) {
        return new HibernateQuery(getSession(entityManager));
    }

    /**
     * Return read only Hibernate Query. <br>
     * <ul>
     * <li>FlushMode.MANUAL</li>
     * <li>readOnly=true</li>
     * </ul>
     * 
     * @param entityManager
     * @return HibernateQuery
     */
    public static HibernateQuery getHibernateReadOnlyQuery(EntityManager entityManager) {
        final HibernateQuery readOnlyQuery = new HibernateQuery(getSession(entityManager));
        readOnlyQuery.setFlushMode(FlushMode.MANUAL);
        readOnlyQuery.setReadOnly(true);
        return readOnlyQuery;
    }

    /**
     * Return read-only Hibernate Query and apply pagination
     * 
     * @param entityManager
     * @param pageRequest
     * @return HibernateQuery
     */
    public static HibernateQuery getHibernateReadOnlyQuery(EntityManager entityManager, PageRequest pageRequest) {
        final HibernateQuery query = getHibernateQuery(entityManager, pageRequest);
        query.setReadOnly(true);
        return query;
    }

    /**
     * Return Hibernate Query and apply pagination
     * 
     * @param entityManager
     * @param pagination
     * @return
     */
    public static HibernateQuery getHibernateQuery(EntityManager entityManager, PageRequest pageRequest) {
        final HibernateQuery query = getHibernateQuery(entityManager);
        if (pageRequest == null) {
            return query;
        }

        query.limit(pageRequest.getPageSize());
        query.offset(pageRequest.getOffset());
        return query;
    }

    /**
     * Return Hibernate Query and apply pagination
     * 
     * @param entityManager
     * @param pageRequest
     * @param entityCLass
     * @return HibernateQuery
     */
    public static HibernateQuery getCachedHibernateQuery(EntityManager entityManager, PageRequest pageRequest,
            Class<?> entityCLass) {
        final HibernateQuery query = getHibernateQuery(entityManager);
        query.setCacheable(true);
        query.setCacheRegion(entityCLass.getSimpleName());
        if (pageRequest == null) {
            return query;
        }

        query.limit(pageRequest.getPageSize());
        query.offset(pageRequest.getOffset());
        return query;
    }

    /**
     * Applies Hibernate constants.
     */
    public static void applyHibernateConstant(Query query, Map<Object, String> constants,
            Map<ParamExpression<?>, Object> params) {
        HibernateUtil.setConstants(query, constants, params);
    }

    /**
     * Return Hibernate session.
     * 
     * @param entityManager
     * @return
     */
    public static Session getSession(EntityManager entityManager) {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Return boolean expression with 'and' destinationExpression or destinationExpression when sourceExpression is
     * null.
     * 
     * @param sourceExpression
     * @param destinationExpression
     * @return
     */
    public static BooleanExpression and(BooleanExpression sourceExpression, BooleanExpression destinationExpression) {
        if (sourceExpression == null) {
            return destinationExpression;
        }
        return sourceExpression.and(destinationExpression);
    }

    /**
     * Return boolean expression with 'or' destinationExpression or destinationExpression when sourceExpression is null.
     * 
     * @param sourceExpression
     * @param destinationExpression
     * @return
     */
    public static BooleanExpression or(BooleanExpression sourceExpression, BooleanExpression destinationExpression) {
        if (sourceExpression == null) {
            return destinationExpression;
        }
        return sourceExpression.or(destinationExpression);
    }

    public static BooleanExpression and(BooleanExpression expression, QueryFilter filter,
            @SuppressWarnings("rawtypes") ComparableExpression comparableExpression) {
        BooleanExpression booleanExpression = getExpression(filter, comparableExpression);
        if (expression == null) {
            return booleanExpression;
        }
        return expression.and(booleanExpression);
    }

    public static BooleanExpression or(BooleanExpression expression, QueryFilter filter,
            @SuppressWarnings("rawtypes") ComparableExpression comparableExpression) {
        BooleanExpression booleanExpression = getExpression(filter, comparableExpression);
        if (expression == null) {
            return booleanExpression;
        }
        return expression.or(booleanExpression);
    }

    /**
     * Return new boolean expression based on filter data and entity field (comparableExpression)
     * 
     * @param filter
     * @param comparableExpression
     * @return boolean expression
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static BooleanExpression getExpression(QueryFilter filter, ComparableExpression comparableExpression) {
        Comparable value = null;
        switch (filter.getFilterClassType()) {
            case BOOLEAN:
                value = Boolean.valueOf(filter.getValue());
                break;
            case DATE:
                value = DateConversionUtils.createUtcDateFromLocalTZ(Long.valueOf(filter.getValue()));
                break;
            case LONG:
                value = Long.valueOf(filter.getValue());
                break;
            case STRING:
                value = filter.getValue();
                break;
            default:
                value = filter.getValue();
        }

        BooleanExpression returnExpression = null;
        switch (filter.getFilterComparisonType()) {
            case EQUALS:
                returnExpression = comparableExpression.eq(value);
                break;
            case GREATER:
                returnExpression = comparableExpression.goe(value);
                break;
            case LESS:
                returnExpression = comparableExpression.loe(value);
                break;
            case LIKE:
                returnExpression = comparableExpression.stringValue().like("%" + value.toString() + "%");
                break;
            default:
                LOGGER.warn("Unsupported filter={}", filter.getFilterComparisonType());
                returnExpression = comparableExpression.eq(value);
        }

        return returnExpression;
    }
}
