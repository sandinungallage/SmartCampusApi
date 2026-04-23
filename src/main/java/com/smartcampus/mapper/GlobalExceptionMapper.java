package com.smartcampus.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler for the REST API.
 *
 * This class captures all unhandled exceptions thrown during request processing
 * and converts them into a consistent HTTP 500 response format.
 *
 * Responsibilities:
 * - Intercept unexpected runtime exceptions
 * - Log full exception details internally for debugging
 * - Return a safe, structured error response to clients
 *
 * Security considerations:
 * - Stack traces are never exposed to clients
 * - Internal error details (file paths, libraries, implementation logic) are hidden
 * - Prevents attackers from gaining insights into system internals
 * - Ensures consistent and safe error messaging across the API
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    // Logger for recording internal server errors
    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    /**
     * Converts any unhandled exception into a standardized HTTP response.
     *
     * @param ex the exception thrown during request processing
     * @return a structured HTTP 500 response with safe error details
     */
    @Override
    public Response toResponse(Throwable ex) {

        // Log full exception details internally for debugging and monitoring
        LOG.log(Level.SEVERE, "Unhandled exception in API", ex);

        // Create a safe response body for the client
        Map<String, Object> body = new HashMap<>();
        body.put("code", 500);
        body.put("type", "ServerError");
        body.put("detail", "An unexpected condition occurred. Please contact support.");
        body.put("timestamp", System.currentTimeMillis());

        // Unique identifier to trace the error in logs
        body.put("traceId", generateTraceId());

        // Return standardized HTTP 500 response
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(body)
                .type("application/json")
                .build();
    }

    /**
     * Generates a simple trace ID for correlating logs with client errors.
     *
     * This helps in debugging issues without exposing internal stack traces.
     */
    private String generateTraceId() {
        return "ERR-" + System.nanoTime();
    }
}