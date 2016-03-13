package com.indexdata.livedocs.manager.service.model;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.indexdata.livedocs.manager.service.model.enums.AccessMode;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Immutable
public class Permission {

    private final String name;
    private final AccessMode accessMode;

    public Permission(String name, AccessMode accessMode) {
        super();
        this.name = name;
        this.accessMode = accessMode;
    }

    public String getName() {
        return name;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessMode == null) ? 0 : accessMode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Permission other = (Permission) obj;
        if (accessMode != other.accessMode) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}