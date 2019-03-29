![Travis (.org)](https://img.shields.io/travis/abusix/rate-limiting-logback-filter.svg)
![Maven Central](https://img.shields.io/maven-central/v/com.abusix.util/rate-limiting-logback-filter.svg)

# Rate-Limiting Logback Filter

We are using sentry.io for error reporting in some backend applications with high frequency data processing.
Sometimes the same error is being sent to sentry again and again over a longer period of time. The spike
protection feature is only meant for very high short spikes and sentry's client side sampling will lead to
losing important one-time-only events.
 
For our java applications we use sentry-logback. This filter can be used to only send one event per defined
timeframe for a combination of loggername + (first line in stack OR log message).

## Usage

Add to dependencies:

```
<dependency>
    <groupId>com.abusix.util</groupId>
    <artifactId>rate-limiting-logback-filter</artifactId>
    <version>{LATEST VERSION}</version>
</dependency>

```

Add to appender in logback config. E.g.:

```
<appender name="Sentry" class="io.sentry.logback.SentryAppender">
  <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>WARN</level>
  </filter>
  <filter class="com.abusix.util.logback.filters.RateLimitingLogbackFilter">
  </filter>
</appender>
```

## Development

### Publishing snapshot

`./gradlew clean build publishMavenJavaPublicationToCentralRepository`

### Publishing release

`./gradlew clean release`