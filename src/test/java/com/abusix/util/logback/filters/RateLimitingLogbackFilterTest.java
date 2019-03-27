/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.abusix.util.logback.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.read.ListAppender;

public class RateLimitingLogbackFilterTest {
    
    private LoggerContext lc;
    private Logger root;


    @BeforeEach
    public void setup() {
        lc = new LoggerContext();
        root = lc.getLogger(Logger.ROOT_LOGGER_NAME);
    }
    

    @Test
    public void testFilteringWithoutStack() throws InterruptedException {
        Logger logger = lc.getLogger(RateLimitingLogbackFilterTest.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        
        RateLimitingLogbackFilter filter = new RateLimitingLogbackFilter();
        filter.setExpireAfterDuration("PT0.2S");
        filter.start();

        listAppender.addFilter(filter);
        root.addAppender(listAppender);
        
        listAppender.start();
        
        logger.info("Hello");
        logger.info("Hello");
        
        assertEquals(1, listAppender.list.size());
        Thread.sleep(200);
        logger.info("Hello");
        
        assertEquals(2, listAppender.list.size());
    }
    
    @Test
    public void testFilteringWithoutStackOnDifferentMessage() {
        Logger logger = lc.getLogger(RateLimitingLogbackFilterTest.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        
        Filter<ILoggingEvent> filter = new RateLimitingLogbackFilter();
        filter.start();

        listAppender.addFilter(filter);
        root.addAppender(listAppender);
        
        listAppender.start();
        
        logger.info("Hello");
        logger.info("Hello you");
        
        assertEquals(2, listAppender.list.size());
        
    }
    
    private void logException1(Logger logger, String randomSuffix) {
        try {
            throw new Exception("Man man man" + randomSuffix);
        } catch (Exception e) {
            logger.error("Failed with exception" + randomSuffix, e);
        }
    }
    
    private void logException2(Logger logger) {
        try {
            throw new Exception("Man man man");
        } catch (Exception e) {
            logger.error("Failed with exception", e);
        }
    }

    @Test
    public void testFilteringWithStack() {
        Logger logger = lc.getLogger(RateLimitingLogbackFilterTest.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        
        Filter<ILoggingEvent> filter = new RateLimitingLogbackFilter();
        filter.start();

        listAppender.addFilter(filter);
        root.addAppender(listAppender);
        
        listAppender.start();
        
        logException1(logger, "");
        logException1(logger, "");
        
        assertEquals(1, listAppender.list.size());
        
    }
    
    @Test
    public void testFilteringWithStackDifferentMessages() {
        Logger logger = lc.getLogger(RateLimitingLogbackFilterTest.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        
        Filter<ILoggingEvent> filter = new RateLimitingLogbackFilter();
        filter.start();

        listAppender.addFilter(filter);
        root.addAppender(listAppender);
        
        listAppender.start();
        
        logException1(logger, "a");
        logException1(logger, "b");
        
        assertEquals(1, listAppender.list.size());
        
    }
    
    @Test
    public void testFilteringWithDifferentStacks() {
        Logger logger = lc.getLogger(RateLimitingLogbackFilterTest.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        
        Filter<ILoggingEvent> filter = new RateLimitingLogbackFilter();
        filter.start();

        listAppender.addFilter(filter);
        root.addAppender(listAppender);
        
        listAppender.start();
        
        logException1(logger, "");
        logException2(logger);
        
        assertEquals(2, listAppender.list.size());
        
    }
}
