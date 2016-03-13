package com.indexdata.livedocs.manager.core.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Can get message keys with params.
 * 
 * @author Adam Dec
 * @since 0.0.6
 */
@Component
public class MessageSourceAdapter {

    @Autowired
    private MessageSource messageSource;

    public String getProperty(String key) {
        return messageSource.getMessage(key, new Object[0], "NOT_FOUND", LocaleContextHolder.getLocale());
    }

    public String getProperty(String key, Object... args) {
        return messageSource.getMessage(key, args, "NOT_FOUND", LocaleContextHolder.getLocale());
    }
}