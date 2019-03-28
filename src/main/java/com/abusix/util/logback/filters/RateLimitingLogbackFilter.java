package com.abusix.util.logback.filters;

import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class RateLimitingLogbackFilter extends Filter<ILoggingEvent>{
    
    public static final int MAXIMUM_CACHE_SIZE_DEFAULT = 1000;
    public static final Duration EXPIRE_AFTER_DURATION_DEFAULT = Duration.ofMinutes(2);

    private Cache<EventKey, Boolean> eventCache;

    private int maximumCacheSize;
    private Duration expireAfterDuration;
    
    public RateLimitingLogbackFilter() {
        maximumCacheSize = MAXIMUM_CACHE_SIZE_DEFAULT;
        expireAfterDuration = EXPIRE_AFTER_DURATION_DEFAULT;
    }
    
    public void setMaximumCacheSize(final int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
    }
    
    /**
     * 
     * @param expireAfterDuration The formats accepted are based on the ISO-8601 duration format
     * PnDTnHnMn.nS with days considered to be exactly 24 hours.
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-">Duration.parse Docs</a>
     *
     */
    public void setExpireAfterDuration(final String expireAfterDuration) {
        this.expireAfterDuration = Duration.parse(expireAfterDuration);
    }

    @Override
    public void start() {
        eventCache = CacheBuilder.newBuilder()
                .maximumSize(maximumCacheSize)
                .expireAfterWrite(expireAfterDuration)
                .build();
        super.start();
    }

    
    @Override
    public FilterReply decide(final ILoggingEvent event) {
        EventKey key = new EventKey(event);
        FilterReply reply;
        if (eventCache.getIfPresent(key) != null) {
            reply = FilterReply.DENY;
        } else {
            reply = FilterReply.NEUTRAL;
            eventCache.put(key, true);
        }
        return reply;
    }
    
    private static class EventKey {

        private final int hash;

        public EventKey(final ILoggingEvent event) {
            hash = computeHash(event);
        }
        
        public int computeHash(final ILoggingEvent event) {
            int hash = 7;
            hash = 31 * hash + (int) event.getLoggerName().hashCode();
            IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (event.getThrowableProxy() != null && throwableProxy.getStackTraceElementProxyArray().length > 0) {
                hash = 31 * hash + throwableProxy.getStackTraceElementProxyArray()[0].hashCode();
            } else {
                hash = 31 * hash + event.getMessage().hashCode();
            }
            return hash;
        }
        
        @Override
        public int hashCode() {
            return hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this.hashCode() == obj.hashCode();
        }

    }
}
