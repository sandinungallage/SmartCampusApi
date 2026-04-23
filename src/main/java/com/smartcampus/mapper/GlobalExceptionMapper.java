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
 * Global Exception Mapper - Catch-all for unexpected errors
 * Handles known exceptions with correct HTTP status codes
 * Never exposes stack traces to client (logs internally)
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // 1. Handle 404 - Resource not found
        if (exception instanceof NotFoundException) {
            return buildResponse(404, "Not Found", "Resource not found");
        }

        // 2. Handle 400 - Bad request
        if (exception instanceof BadRequestException) {
            return buildResponse(400, "Bad Request", "Invalid request");
        }

        // 3. Handle 405 - Method not allowed
        if (exception instanceof NotAllowedException) {
            return buildResponse(405, "Method Not Allowed", "HTTP method not allowed for this endpoint");
        }

        // 4. Handle 415 - Unsupported media type
        if (exception instanceof NotSupportedException) {
            return buildResponse(415, "Unsupported Media Type", "Content type not supported");
        }

        // 5. Handle JSON processing errors
        if (isJsonProcessingError(exception)) {
            LOGGER.log(Level.SEVERE, "JSON processing error", exception);
            return buildResponse(500, "Internal Server Error",
                    "Request processing failed. Please check JSON format.");
        }

        // 6. Default - Internal server error
        LOGGER.log(Level.SEVERE, "Unexpected error occurred", exception);
        return buildResponse(500, "Internal Server Error",
                "An unexpected error occurred. Please contact support.");
    }

    /**
     * Helper method to build consistent JSON error responses
     */
    private Response buildResponse(int status, String error, String message) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("errorId", System.nanoTime());

        return Response.status(status)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }

    /**
     * Check if exception is or wraps a Jackson JSON processing error
     */
    private boolean isJsonProcessingError(Throwable exception) {

        if (exception instanceof JsonProcessingException) {
            return true;
        }

        // Check cause chain
        if (exception.getCause() != null &&
                exception.getCause() instanceof JsonProcessingException) {
            return true;
        }

        return false;
    }
}