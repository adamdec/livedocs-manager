package com.indexdata.livedocs.manager.ui.window.main.elements.table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiscTableData {

    private final long fileId;
    private final String path;
    private final String[] fieldValueList;

    public DiscTableData(final String[] fieldValueList) {
        super();
        this.fileId = -1L;
        this.path = "";
        this.fieldValueList = fieldValueList;
    }

    public DiscTableData(final long fileId, final String path, final String[] fieldValueList) {
        super();
        this.fileId = fileId;
        this.path = path;
        this.fieldValueList = fieldValueList;
    }

    public String getPath() {
        return path;
    }

    public long getFileId() {
        return fileId;
    }

    public String[] getFieldValueList() {
        return fieldValueList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
