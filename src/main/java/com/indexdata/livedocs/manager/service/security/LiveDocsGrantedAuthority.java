package com.indexdata.livedocs.manager.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.indexdata.livedocs.manager.service.model.enums.AccessMode;

/**
 * @author Adam Dec
 * @since 0.0.9
 */
public class LiveDocsGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 5000884227261324201L;

    private final String authorityName;
    private final AccessMode accessMode;

    public LiveDocsGrantedAuthority(String authorityName, AccessMode accessMode) {
        super();
        Assert.hasText(authorityName, "A granted authority textual representation is required");
        this.authorityName = authorityName;
        this.accessMode = accessMode;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public String getAuthority() {
        return authorityName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessMode == null) ? 0 : accessMode.hashCode());
        result = prime * result + ((authorityName == null) ? 0 : authorityName.hashCode());
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
        LiveDocsGrantedAuthority other = (LiveDocsGrantedAuthority) obj;
        if (accessMode != other.accessMode) {
            return false;
        }
        if (authorityName == null) {
            if (other.authorityName != null) {
                return false;
            }
        } else if (!authorityName.equals(other.authorityName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LiveDocsGrantedAuthority [authorityName=" + authorityName + ", accessMode=" + accessMode + "]";
    }
}