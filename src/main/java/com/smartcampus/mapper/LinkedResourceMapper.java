package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for LinkedResourceNotFoundException.
 * 
 * This mapper handles cases where a requested or referenced resource
 * (e.g., Room, Sensor) does not exist in the system.
 * 
 * Typical scenario:
 * - A resource depends on another entity that is missing
 * - Example: Sensor references a Room ID that does not exist
 * 
 * HTTP Response:
 * - Returns HTTP 422 Unprocessable Entity
 * - Provides structured JSON error response with debugging details
 */
@Provider
public class LinkedResourceMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    /**
     * Converts LinkedResourceNotFoundException into a standardized HTTP response.
     *
     * @param exception the custom exception containing missing resource details
     * @return HTTP 422 response with structured JSON error body
     */
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {

        // Build structured error response for client consumption
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("status", 422);
        errorResponse.put("error", "Unprocessable Entity");

        // Detailed message from exception explaining the issue
        errorResponse.put("message", exception.getMessage());

        // Timestamp for when the error occurred
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Additional context for debugging and traceability
        errorResponse.put("resourceType", exception.getResourceType());
        errorResponse.put("resourceId", exception.getResourceId());

        // Return HTTP 422 response with JSON payload
        return Response
                .status(422)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}