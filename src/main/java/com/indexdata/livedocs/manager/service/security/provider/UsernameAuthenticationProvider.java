package com.indexdata.livedocs.manager.service.security.provider;

import org.springframework.aop.support.AopUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * This class will provide UserDetals for given user name. User i searched in DB using userDetailsService.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
public class UsernameAuthenticationProvider extends DaoAuthenticationProvider {

    private PasswordEncoder passwordEncoder;

    @Override
    public void setPasswordEncoder(Object passwordEncoder) {
        Assert.isAssignable(PasswordEncoder.class, passwordEncoder.getClass(), "PasswordEncoder must be of class "
                + PasswordEncoder.class.getName());
        this.passwordEncoder = (PasswordEncoder) passwordEncoder;
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public String toString() {
        return "UsernameAuthenticationProvider [passwordEncoder=" + passwordEncoder + ", userDetailsService="
                + AopUtils.getTargetClass(getUserDetailsService()) + "]";
    }
}