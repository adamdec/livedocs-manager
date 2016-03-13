package com.indexdata.livedocs.manager.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initializes Spring application context.
 *
 * @author Adam Dec
 * @since 0.0.2
 */
public class SpringInitializer extends BaseInitializer {

    private static Logger LOGGER = LoggerFactory.getLogger(SpringInitializer.class);

    public static final String SPRING_THREAD_NAME = "SpringAppThread";

    private Future<SpringThreadResult> springThreadResult;

    public SpringInitializer() {
        super(SPRING_THREAD_NAME);
    }

    public void init() throws InterruptedException, ExecutionException {
        if (this.springThreadResult != null && getSpringThreadResult().getSpringContext() != null) {
            getSpringThreadResult().getSpringContext().close();
        }
        this.springThreadResult = executor.submit(new SpringThread());
        LOGGER.info("Submitted thread {}.", SPRING_THREAD_NAME);
    }

    public SpringThreadResult getSpringThreadResult() throws InterruptedException, ExecutionException {
        return springThreadResult.get();
    }

    @Override
    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
            LOGGER.info("{} executor shutdown.", SPRING_THREAD_NAME);
        }
    }
}