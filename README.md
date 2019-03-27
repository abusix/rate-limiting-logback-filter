# Rate-Limiting Logback Filter

We are using sentry.io for error reporting in some backend applications with high frequency data processing.
Sometimes the same error is being sent to sentry again and again over a longer period of time. The spike
protection feature is only meant for very high short spikes and sentry's client side sampling will lead to
losing important one-time-only events.
 
For our java applications we use sentry-logback. This filter can be used to only send one event per defined
timeframe for a combination of loggername + (first line in stack OR log message).