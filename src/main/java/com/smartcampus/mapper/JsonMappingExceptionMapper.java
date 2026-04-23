package com.smartcampus.mapper;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON Mapping Exception Mapper - Handles JSON type mismatch errors during request parsing.
 * 
 * This mapper is triggered when:
 * - JSON is syntactically valid
 * - BUT field types do not match expected Java types
 *   (e.g., sending a string instead of an integer)
 * 
 * Example:
 * { "capacity": "twenty" } → invalid if capacity is an int
 * 
 * Response Strategy:
 * - Returns HTTP 400 Bad Request
 * - Sends a clean, user-friendly JSON error response
 * - Prevents internal stack trace leakage to clients
 * - Logs full error details internally for debugging/admin use
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    // Logger used to record JSON mapping issues for debugging purposes
    private static final Logger LOGGER = Logger.getLogger(JsonMappingExceptionMapper.class.getName());

    /**
     * Converts Jackson MismatchedInputException into a structured HTTP response.
     *
     * @param exception the JSON mapping/type mismatch exception
     * @return HTTP 400 response with structured JSON error body
     */
    @Override
    public Response toResponse(MismatchedInputException exception) {

        // Debug marker to confirm this mapper is triggered during runtime
        System.err.println(">>> MISMATCHEDINPUTEXCEPTIONMAPPER TRIGGERED <<<");

        // Log full exception details internally (useful for developers/admins)
        LOGGER.log(Level.WARNING,
                "JSON type mismatch: " + exception.getOriginalMessage(),
                exception);

        // Build structured error response for client
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", "Invalid JSON structure. Please check request fields and types.");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Unique identifier for tracking this specific error occurrence
        errorResponse.put("errorId", System.nanoTime());

        // Return standardized HTTP 400 response with JSON body
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}