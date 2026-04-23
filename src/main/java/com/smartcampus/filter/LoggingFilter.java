package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging filter for HTTP requests and responses.
 *
 * This class acts as a cross-cutting component that intercepts all incoming
 * requests and outgoing responses in the application.
 *
 * Purpose:
 * - Capture request metadata before processing
 * - Measure request processing time
 * - Log response status and performance metrics
 * - Provide consistent logging across all API endpoints
 *
 * Benefits of using a filter instead of manual logging in controllers:
 * - Centralized logging logic (no duplication across endpoints)
 * - Consistent log format for all requests
 * - Easier maintenance and updates
 * - Automatic performance tracking per request
 * - Improved traceability between request and response lifecycle
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Logger instance for this class
    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    // Key used to store request start time inside request context
    private static final String TIMING_KEY = "req.timing.start";

    /**
     * Intercepts incoming HTTP requests before they reach the resource layer.
     *
     * Responsibilities:
     * - Capture request start timestamp
     * - Extract request method, URI, and content type
     * - Log basic request details for monitoring and debugging
     */
    @Override
    public void filter(ContainerRequestContext req) throws IOException {

        // Record request start time for performance calculation
        long ts = System.currentTimeMillis();
        req.setProperty(TIMING_KEY, ts);

        // Extract request metadata
        String verb = req.getMethod();
        String path = req.getUriInfo().getRequestUri().toString();
        String mediaType = req.getHeaderString("Content-Type");

        // Log incoming request details
        LOG.log(Level.INFO, String.format(
            "[REQ] %s %s | Media=%s | TS=%d",
            verb, path, mediaType != null ? mediaType : "none", ts));
    }

    /**
     * Intercepts outgoing HTTP responses after resource processing is complete.
     *
     * Responsibilities:
     * - Retrieve request start time
     * - Calculate total processing time
     * - Log response status and execution duration
     * - Assign log level based on response status code
     */
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res)
            throws IOException {

        // Retrieve stored start timestamp
        Object tsObj = req.getProperty(TIMING_KEY);
        long elapsed = 0;

        // Calculate request processing time if timestamp exists
        if (tsObj != null) {
            elapsed = System.currentTimeMillis() - (long) tsObj;
        }

        // Extract request/response details
        String verb = req.getMethod();
        String path = req.getUriInfo().getRequestUri().toString();
        int code = res.getStatus();

        // Determine log severity based on HTTP status code
        Level lv = code >= 500 ? Level.SEVERE : 
                   code >= 400 ? Level.WARNING : Level.INFO;

        // Log response details with execution time
        LOG.log(lv, String.format(
            "[RES] %s %s | Code=%d | Elapsed=%dms",
            verb, path, code, elapsed));
    }
}