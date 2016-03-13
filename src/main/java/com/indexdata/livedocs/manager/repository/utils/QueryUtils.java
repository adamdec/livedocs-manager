package com.indexdata.livedocs.manager.repository.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.indexdata.livedocs.manager.repository.custom.FileCustomRepository;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter.FilterClassTypeEnum;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter.FilterComparisonTypeEnum;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;

/**
 * @author Adam Dec
 * @since 0.0.7
 */
@Component
public class QueryUtils {

    private static final String WILDCARD_MANY_CHARS = "\\*";
    private static final String WILDCARD_ONE_CHAR = "\\?";
    private static final String SQL_WILDCARD_MANY_CHARS = "%";
    private static final String SQL_WILDCARD_ONE_CHAR = "_";

    @Autowired
    private FileCustomRepository fileCustomRepository;

    /**
     * Key -> profile attribute name Value -> profile attribute value
     * 
     * @param keyValueList
     * @return filter map
     */
    public Map<String, QueryFilter> createFilterList(final List<AttributeNameValue> keyValueList) {
        final Map<String, QueryFilter> queryFilterMap = new HashMap<String, QueryFilter>(keyValueList.size());
        for (AttributeNameValue keyValue : keyValueList) {
            if (keyValue.getValue() == null) {
                continue;
            }

            if (keyValue.getValue().contains("*") || keyValue.getValue().contains("?")) {
                final String convertedValue = keyValue.getValue()
                        .replaceAll(WILDCARD_MANY_CHARS, SQL_WILDCARD_MANY_CHARS)
                        .replaceAll(WILDCARD_ONE_CHAR, SQL_WILDCARD_ONE_CHAR);
                queryFilterMap.put(keyValue.getName(), new QueryFilter(keyValue.getName(), convertedValue,
                        FilterClassTypeEnum.STRING, FilterComparisonTypeEnum.LIKE));
            }
            else {
                queryFilterMap.put(keyValue.getName(), new QueryFilter(keyValue.getName(), keyValue.getValue(),
                        FilterClassTypeEnum.STRING, FilterComparisonTypeEnum.EQUALS));
            }
        }
        return queryFilterMap;
    }

    public List<QueryFilter> preserveAttributeNameOrdering(final Map<String, QueryFilter> queryFilterMap,
            final Long profileId) {
        final List<QueryFilter> newList = new ArrayList<QueryFilter>(queryFilterMap.size());
        final List<String> fileAttributeNameList = fileCustomRepository.getFileAttributeNames(profileId);
        for (String attributeName : fileAttributeNameList) {
            if (attributeName != null && queryFilterMap.containsKey(attributeName)) {
                newList.add(queryFilterMap.get(attributeName));
            }
            else {
                // Attributes order selection in UI must be maintained
                newList.add(new QueryFilter());
            }
        }
        return newList;
    }

    public FilePageRequest preserveFieldOrdering(final FilePageRequest filePageRequest, final Long profileId) {
        final List<String> fileAttributeNameList = fileCustomRepository.getFileAttributeNames(profileId);
        int i = 1;
        for (String attributeName : fileAttributeNameList) {
            if (attributeName != null && attributeName.equals(filePageRequest.getOrder().getProperty())) {
                return new FilePageRequest(filePageRequest.getOffset(), filePageRequest.getLimit(), new Order(
                        filePageRequest.getOrder().getDirection(), "field" + i));
            }
            i++;
        }
        return filePageRequest;
    }
}
