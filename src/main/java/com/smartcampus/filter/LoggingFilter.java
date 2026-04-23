package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging Filter - Cross-cutting concern for request/response logging
 * Implements ContainerRequestFilter and ContainerResponseFilter for complete
 * lifecycle tracking
 *
 * Benefits of filter-based logging vs manual Logger.info() in methods:
 * 1. Single point of change - update format once, applies everywhere
 * 2. No code duplication - logs all endpoints automatically
 * 3. Business logic stays clean - no logging noise in resource methods
 * 4. Performance monitoring - duration tracking built-in
 * 5. Request correlation - can add correlation IDs for tracing
 * 6. Consistent format - ensures uniform logging across entire API
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    private static final String REQUEST_START_TIME = "request-start-time";

    /**
     * Logs incoming HTTP request
     * CRITICAL: Sets request-start-time property for response filter to calculate
     * duration
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // ✅ CRITICAL FIX: Set property FIRST before response filter accesses it
        long startTime = System.currentTimeMillis();
        requestContext.setProperty(REQUEST_START_TIME, startTime);

        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        String contentType = requestContext.getHeaderString("Content-Type");

        LOGGER.log(Level.INFO, String.format(
                "[REQUEST] Method: %s | URI: %s | Content-Type: %s | Timestamp: %d",
                method, uri, contentType != null ? contentType : "N/A", startTime));
    }

    /**
     * Logs outgoing HTTP response
     * Calculates request duration and logs with status code for observability
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        // ✅ Safe to retrieve - property was set in request filter
        Object startTimeObj = requestContext.getProperty(REQUEST_START_TIME);
        long duration = 0;

        if (startTimeObj != null) {
            long startTime = (long) startTimeObj;
            duration = System.currentTimeMillis() - startTime;
        }

        int status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        // Log with appropriate level based on status
        Level logLevel = status >= 500 ? Level.SEVERE : status >= 400 ? Level.WARNING : Level.INFO;

        LOGGER.log(logLevel, String.format(
                "[RESPONSE] Method: %s | URI: %s | Status: %d | Duration: %dms",
                method, uri, status, duration));
    }
}