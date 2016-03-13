package com.indexdata.livedocs.manager.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringThreadResult {

    private final ClassPathXmlApplicationContext springContext;
    private final String errorMessage;

    public SpringThreadResult(ClassPathXmlApplicationContext springContext) {
        super();
        this.springContext = springContext;
        this.errorMessage = null;
    }

    public SpringThreadResult(String errorMessage) {
        super();
        this.springContext = null;
        this.errorMessage = errorMessage;
    }

    public ClassPathXmlApplicationContext getSpringContext() {
        return springContext;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}