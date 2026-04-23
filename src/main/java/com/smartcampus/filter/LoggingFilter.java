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
 * Logging Filter - A cross-cutting concern used to log all HTTP requests and responses.
 * 
 * This filter intercepts every request and response in the JAX-RS lifecycle,
 * providing centralized logging without polluting business logic inside resource classes.
 * 
 * Implements:
 * - ContainerRequestFilter: for incoming request logging
 * - ContainerResponseFilter: for outgoing response logging
 * 
 * Key benefits of filter-based logging:
 * 1. Centralized logging logic (single place for all endpoints)
 * 2. No duplication across resource methods
 * 3. Keeps business logic clean and focused
 * 4. Enables request duration tracking for performance monitoring
 * 5. Supports observability and debugging with consistent format
 * 6. Can be extended for correlation IDs / tracing systems
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Logger instance used for all request/response logs
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // Key used to store request start time in request context
    private static final String REQUEST_START_TIME = "request-start-time";

    /**
     * Intercepts and logs incoming HTTP requests before they reach resource methods.
     * 
     * Also stores the request start time in the request context so that response
     * filter can calculate total processing duration.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Capture request start time for performance tracking
        long startTime = System.currentTimeMillis();

        // Store start time in request context for later use in response filter
        requestContext.setProperty(REQUEST_START_TIME, startTime);

        // Extract request details for logging
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        String contentType = requestContext.getHeaderString("Content-Type");

        // Log incoming request with timestamp
        LOGGER.log(Level.INFO, String.format(
                "[REQUEST] Method: %s | URI: %s | Content-Type: %s | Timestamp: %d",
                method,
                uri,
                contentType != null ? contentType : "N/A",
                startTime
        ));
    }

    /**
     * Intercepts and logs outgoing HTTP responses after resource processing is complete.
     * 
     * Calculates total request duration using the start time stored in the request filter
     * and logs response status along with performance metrics.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Retrieve start time from request context (set in request filter)
        Object startTimeObj = requestContext.getProperty(REQUEST_START_TIME);
        long duration = 0;

        // Calculate request processing duration if start time is available
        if (startTimeObj != null) {
            long startTime = (long) startTimeObj;
            duration = System.currentTimeMillis() - startTime;
        }

        // Extract response and request details
        int status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        // Determine log level based on HTTP status code
        Level logLevel = status >= 500 ? Level.SEVERE
                : status >= 400 ? Level.WARNING
                : Level.INFO;

        // Log outgoing response with performance metrics
        LOGGER.log(logLevel, String.format(
                "[RESPONSE] Method: %s | URI: %s | Status: %d | Duration: %dms",
                method,
                uri,
                status,
                duration
        ));
    }
}