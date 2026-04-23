package com.smartcampus.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Exception Mapper - Centralized error handling for the entire REST API.
 * 
 * This class acts as a fallback handler for all unhandled exceptions thrown
 * within the application.
 * 
 * Responsibilities:
 * - Map exceptions to appropriate HTTP status codes
 * - Return consistent JSON error responses
 * - Prevent internal stack traces from being exposed to clients
 * - Log all unexpected errors for debugging and monitoring
 * 
 * This ensures a consistent and secure error-handling strategy across the API.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    // Logger used to record all exceptions for debugging and monitoring
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    /**
     * Converts any thrown exception into a standardized HTTP response.
     *
     * @param exception the exception thrown during request processing
     * @return a structured HTTP response with appropriate status and message
     */
    @Override
    public Response toResponse(Throwable exception) {

        // 404 - Resource not found (e.g., invalid endpoint or missing entity)
        if (exception instanceof NotFoundException) {
            return buildResponse(404, "Not Found", "Resource not found");
        }

        // 400 - Bad request (invalid input or malformed request)
        if (exception instanceof BadRequestException) {
            return buildResponse(400, "Bad Request", "Invalid request");
        }

        // 405 - HTTP method not allowed on this endpoint
        if (exception instanceof NotAllowedException) {
            return buildResponse(405, "Method Not Allowed", "HTTP method not allowed for this endpoint");
        }

        // 415 - Unsupported media type (e.g., wrong Content-Type header)
        if (exception instanceof NotSupportedException) {
            return buildResponse(415, "Unsupported Media Type", "Content type not supported");
        }

        // Handle JSON parsing/serialization issues (e.g., invalid request body format)
        if (isJsonProcessingError(exception)) {
            LOGGER.log(Level.SEVERE, "JSON processing error", exception);
            return buildResponse(500, "Internal Server Error",
                    "Request processing failed. Please check JSON format.");
        }

        // Default fallback: unexpected server-side error
        LOGGER.log(Level.SEVERE, "Unexpected error occurred", exception);
        return buildResponse(500, "Internal Server Error",
                "An unexpected error occurred. Please contact support.");
    }

    /**
     * Builds a standardized JSON error response.
     *
     * Ensures all API errors follow a consistent structure:
     * - status: HTTP status code
     * - error: short error label
     * - message: human-readable explanation
     * - timestamp: time of error occurrence
     * - errorId: unique identifier for tracing logs
     *
     * @param status HTTP status code
     * @param error short error title
     * @param message detailed error message
     * @return formatted HTTP Response object
     */
    private Response buildResponse(int status, String error, String message) {

        // Create structured error payload
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Unique ID for tracing/debugging specific error instances
        errorResponse.put("errorId", System.nanoTime());

        // Build and return JSON response
        return Response.status(status)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }

    /**
     * Determines whether the exception is related to Jackson JSON processing errors.
     *
     * This includes:
     * - Direct JsonProcessingException
     * - Wrapped JsonProcessingException inside another exception
     *
     * @param exception the exception to inspect
     * @return true if it is a JSON processing related error
     */
    private boolean isJsonProcessingError(Throwable exception) {

        // Direct Jackson parsing/serialization error
        if (exception instanceof JsonProcessingException) {
            return true;
        }

        // Check nested cause (wrapped exception scenario)
        if (exception.getCause() != null &&
                exception.getCause() instanceof JsonProcessingException) {
            return true;
        }

        return false;
    }
}