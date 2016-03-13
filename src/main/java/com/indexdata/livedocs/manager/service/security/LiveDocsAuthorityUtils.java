package com.indexdata.livedocs.manager.service.security;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.livedocs.manager.service.model.Permission;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
public final class LiveDocsAuthorityUtils {

    private LiveDocsAuthorityUtils() {
    }

    /**
     * Converts LiveDocs section roles into Spring GrantedAuthority classes.
     * 
     * @param list of application sections
     * @return list of Spring GrantedAuthority objects
     */
    public static List<LiveDocsGrantedAuthority> convertToAuthorities(final List<Permission> permissionList) {
        final List<LiveDocsGrantedAuthority> authorities = new ArrayList<>(permissionList.size());
        for (Permission permission : permissionList) {
            final LiveDocsGrantedAuthority auth = new LiveDocsGrantedAuthority(permission.getName(),
                    permission.getAccessMode());
            if (!authorities.contains(auth)) {
                authorities.add(auth);
            }
        }
        return authorities;
    }
}