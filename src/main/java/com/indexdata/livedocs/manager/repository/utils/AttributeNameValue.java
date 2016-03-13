package com.indexdata.livedocs.manager.repository.utils;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AttributeNameValue implements Serializable {

    private static final long serialVersionUID = -2607258160557846560L;

    private final String name;
    private final String value;

    public AttributeNameValue() {
        this.name = null;
        this.value = null;
    }

    public AttributeNameValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}