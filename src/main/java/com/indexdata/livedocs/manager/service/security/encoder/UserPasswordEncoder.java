package com.indexdata.livedocs.manager.service.security.encoder;

import org.jasypt.digest.StringDigester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * Password encoder using SHA-1.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
public class UserPasswordEncoder implements PasswordEncoder {

    private static Logger LOGGER = LoggerFactory.getLogger(UserPasswordEncoder.class);

    private final StringDigester digester;

    public UserPasswordEncoder(StringDigester digester) {
        Assert.notNull(digester, "Digester cannot be null");
        this.digester = digester;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        String result = null;
        if (rawPassword != null) {
            try {
                result = digester.digest(rawPassword.toString());
            } catch (Exception e) {
                throw new BadCredentialsException("Bad credential");
            }
        }
        LOGGER.trace("Digested [{}] = [{}]", rawPassword, result);
        return result;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean result = false;
        LOGGER.trace("Matching [{}] and [{}]", rawPassword, encodedPassword);
        if (rawPassword != null) {
            try {
                result = digester.matches(rawPassword.toString(), encodedPassword);
            } catch (Exception e) {
                throw new BadCredentialsException("Bad credential");
            }
        }
        LOGGER.trace("Matched [{}] and [{}] = [{}]", rawPassword, encodedPassword, result);
        return result;
    }

    @Override
    public String toString() {
        return "UserPasswordEncoder [digester=" + digester + "]";
    }
}