package com.indexdata.livedocs.manager.ui.window.users.table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserToRowMapping {

    private final Long userId;
    private final Integer rowIndex;

    public UserToRowMapping(Long userId, Integer rowIndex) {
        super();
        this.userId = userId;
        this.rowIndex = rowIndex;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}