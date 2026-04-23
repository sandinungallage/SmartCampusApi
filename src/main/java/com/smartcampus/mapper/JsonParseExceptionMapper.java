package com.smartcampus.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON Processing Exception Mapper - Handles general JSON parsing and processing errors.
 * 
 * This mapper captures exceptions thrown by Jackson when:
 * - JSON is syntactically invalid (e.g., missing braces, commas)
 * - JSON cannot be parsed into Java objects
 * - Higher-level processing errors occur during deserialization
 * 
 * Important Note:
 * - These errors typically indicate malformed request bodies
 * - Classified here as HTTP 500 Internal Server Error (server-side processing failure)
 * 
 * Security Consideration:
 * - No stack traces are returned to clients
 * - Full error details are logged internally for debugging purposes only
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    // Logger used to record JSON processing failures for system monitoring
    private static final Logger LOGGER = Logger.getLogger(JsonParseExceptionMapper.class.getName());

    /**
     * Converts JsonProcessingException into a standardized HTTP response.
     *
     * @param exception the JSON parsing or processing exception
     * @return HTTP 500 response with structured JSON error body
     */
    @Override
    public Response toResponse(JsonProcessingException exception) {

        // Debug marker to confirm this mapper is triggered at runtime
        System.err.println(">>> JSONPROCESSINGEXCEPTIONMAPPER TRIGGERED: "
                + exception.getClass().getName() + " <<<");

        // Log full exception details internally for developers/admins
        LOGGER.log(Level.SEVERE,
                "JSON processing error: " + exception.getClass().getSimpleName(),
                exception);

        // Construct standardized error response for clients
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 500);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "Request processing failed. Please contact support.");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Unique ID to trace this specific error instance in logs
        errorResponse.put("errorId", System.nanoTime());

        // Return HTTP 500 response with JSON body
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}