package com.indexdata.livedocs.manager.core;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.indexdata.livedocs.manager.core.resources.Resources;

/**
 * @author Adam Dec
 * @since 0.0.2
 */
public class SpringThread implements Callable<SpringThreadResult> {

    private ClassPathXmlApplicationContext springContext;

    @Override
    public SpringThreadResult call() throws Exception {
        try {
            this.springContext = new ClassPathXmlApplicationContext(Resources.SPRING_CONTEXT_FILE.getValue());
            this.springContext.registerShutdownHook();
            return new SpringThreadResult(springContext);
        } catch (Exception e) {
            return new SpringThreadResult(ExceptionUtils.getRootCauseMessage(e));
        }
    }
}