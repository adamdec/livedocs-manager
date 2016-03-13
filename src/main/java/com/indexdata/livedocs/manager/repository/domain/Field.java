package com.indexdata.livedocs.manager.repository.domain;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter.FilterClassTypeEnum;

/**
 * @author Adam Dec
 * @since 0.0.6
 */
@Embeddable
public class Field {

    private String name;
    private String value;
    private FilterClassTypeEnum filterClassType;

    public Field() {
    }

    public Field(String name, String value, FilterClassTypeEnum filterClassType) {
        super();
        this.name = name;
        this.value = value;
        this.filterClassType = filterClassType;
    }

    public Field(String name, String value) {
        super();
        this.name = name;
        this.value = value;
        this.filterClassType = FilterClassTypeEnum.STRING;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FilterClassTypeEnum getFilterClassType() {
        return filterClassType;
    }

    public void setFilterClassType(FilterClassTypeEnum filterClassType) {
        this.filterClassType = filterClassType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Field other = (Field) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}