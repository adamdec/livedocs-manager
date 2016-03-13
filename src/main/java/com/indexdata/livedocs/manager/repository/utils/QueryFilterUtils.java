package com.indexdata.livedocs.manager.repository.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;

/**
 * @author Adam Dec
 * @since 0.0.3
 */
public final class QueryFilterUtils {

    private QueryFilterUtils() {
    }

    /**
     * Fills filter values and remove filters with null values
     * 
     * @param filterMap
     * @param filters
     */
    public static void prepareFilters(Map<String, QueryFilter> filterMap, List<AttributeNameValue> filters) {
        if (filterMap == null) {
            return;
        }
        if (filters == null || filters.isEmpty()) {
            // remove filters with null values
            filterMap.clear();
            return;
        }

        // fill values
        for (AttributeNameValue keyValue : filters) {
            if (filterMap.containsKey(keyValue.getName())) {
                filterMap.get(keyValue.getName()).setValue(keyValue.getValue());
            }
        }

        // remove filters with null values
        Map<String, QueryFilter> temp = new HashMap<>();
        for (Map.Entry<String, QueryFilter> entry : filterMap.entrySet()) {
            if (entry.getValue().getValue() != null && !"".equals(entry.getValue().getValue().trim())) {
                temp.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, QueryFilter> entry : temp.entrySet()) {
            // change date to 00:00:00 minus 1 millis for greater
            if (entry.getValue().getFilterClassType() == QueryFilter.FilterClassTypeEnum.DATE
                    && entry.getValue().getFilterComparisonType() == QueryFilter.FilterComparisonTypeEnum.GREATER) {
                entry.getValue().setValue("" + new DateTime(Long.valueOf(entry.getValue().getValue()))
                        .withTimeAtStartOfDay().minusMillis(1).getMillis());
            }

            // change date to 23:59:59 plus 1 millis for less
            if (entry.getValue().getFilterClassType() == QueryFilter.FilterClassTypeEnum.DATE
                    && entry.getValue().getFilterComparisonType() == QueryFilter.FilterComparisonTypeEnum.LESS) {
                entry.getValue().setValue("" + new DateTime(Long.valueOf(entry.getValue().getValue()))
                        .plusDays(1).withTimeAtStartOfDay().getMillis());
            }
        }

        filterMap.clear();
        filterMap.putAll(temp);
    }
}