package com.indexdata.livedocs.manager.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Component
public class UserAuthenticationManager {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationManager.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserAuthenticationContext userAuthenticationContext;

    public void setAppAuthenticationdata(final String userName, final String userPassword) {
        try {
            onSuccessfulAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userName, userPassword)));
            userAuthenticationContext.setProvidedPassword(userPassword);
        } catch (AuthenticationException e) {
            onUnsuccessfulAuthentication(e);
        }
    }

    protected void onSuccessfulAuthentication(Authentication authentication) {
        clearSecurityContext();
        LOGGER.debug("Authentication success: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void onUnsuccessfulAuthentication(AuthenticationException e) {
        clearSecurityContext();
        LOGGER.error("Authentication failed: {}", e.getMessage());
        throw new BadCredentialsException(e.getMessage());
    }

    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
