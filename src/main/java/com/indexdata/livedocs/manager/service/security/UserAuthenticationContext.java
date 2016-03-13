package com.indexdata.livedocs.manager.service.security;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.indexdata.livedocs.manager.repository.UserRepository;
import com.indexdata.livedocs.manager.repository.domain.UserEntity;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.security.encoder.UserPasswordEncoder;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Component
public class UserAuthenticationContext {

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    private String providedPassword;

    /**
     * Checks user access for given authority name.
     * 
     * @param name section or profile name
     * @return true/false
     */
    public AccessMode getAccessTo(final String name) {
        final LiveDocsUser userDetails = (LiveDocsUser) getCurrentPrincipal();
        if (userDetails == null) {
            throw new IllegalStateException("UserDetails is null!");
        }
        for (GrantedAuthority auth : userDetails.getAuthorities()) {
            final LiveDocsGrantedAuthority liveDocsAuth = (LiveDocsGrantedAuthority) auth;
            if (name.equals(liveDocsAuth.getAuthority())) {
                return liveDocsAuth.getAccessMode();
            }
        }
        return AccessMode.Denied;
    }

    /**
     * Checks user access to profiles for given authority name.
     * 
     * @param name section or profile name
     * @return true/false
     */
    public AccessMode getAccessToProfiles(final Long profileId) {
        final LiveDocsUser userDetails = (LiveDocsUser) getCurrentPrincipal();
        if (userDetails == null) {
            throw new IllegalStateException("UserDetails is null!");
        }
        for (GrantedAuthority auth : userDetails.getProfilePermissions()) {
            final LiveDocsGrantedAuthority liveDocsAuth = (LiveDocsGrantedAuthority) auth;
            if (profileId.equals(Long.valueOf(liveDocsAuth.getAuthority()))) {
                return liveDocsAuth.getAccessMode();
            }
        }
        return AccessMode.Denied;
    }

    /**
     * @return authenticated user name
     */
    public String getCurrentUserName() {
        return getAuthentication().getName();
    }

    /**
     * @return authenticated user details object
     */
    public UserDetails getCurrentPrincipal() {
        return (UserDetails) getAuthentication().getPrincipal();
    }

    /**
     * @return authentication object for authenticated user
     */
    private Authentication getAuthentication() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null!");
        }
        return authentication;
    }

    public DefaultKeyValue getUserNameAndPasssword() {
        final UserDetails userDetails = getCurrentPrincipal();
        if (userDetails == null) {
            return null;
        }
        final UserEntity userEntity = userRepository.getUserByUserName(userDetails.getUsername());
        if (userEntity == null) {
            return null;
        }
        return new DefaultKeyValue(userEntity.getUserName(), userEntity.getPassword());
    }

    public boolean checkPassword(String rawPassword) {
        final DefaultKeyValue userNameANdPassword = getUserNameAndPasssword();
        return userPasswordEncoder.matches(rawPassword, (String) userNameANdPassword.getValue());
    }

    public String encodePassword(String rawPassword) {
        return userPasswordEncoder.encode(rawPassword);
    }

    public String getProvidedPassword() {
        return providedPassword;
    }

    public void setProvidedPassword(String providedPassword) {
        this.providedPassword = providedPassword;
    }
}