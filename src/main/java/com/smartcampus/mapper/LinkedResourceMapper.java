package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for handling LinkedResourceNotFoundException.
 *
 * This mapper converts validation errors related to invalid foreign key references
 * in request payloads into a structured HTTP response.
 *
 * It specifically handles cases where a referenced resource does not exist
 * in the system but the request format itself is valid.
 *
 * HTTP status used: 422 Unprocessable Entity
 *
 * Rationale for using 422 instead of 404:
 * - 404 indicates that the requested API endpoint or primary resource does not exist
 * - 422 indicates that the request is syntactically correct but semantically invalid
 *   due to referencing a non-existent related entity
 * - This distinction improves API clarity and client-side error handling
 */
@Provider
public class LinkedResourceMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    /**
     * Converts LinkedResourceNotFoundException into a structured HTTP response.
     *
     * @param ex the exception containing details about the invalid reference
     * @return HTTP 422 response with detailed error information
     */
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {

        // Build structured error response body
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", 422);
        errorBody.put("error", "InvalidReference");
        errorBody.put("message", ex.getMessage());

        // Include metadata about the invalid reference for debugging
        errorBody.put("recordType", ex.getRefType());
        errorBody.put("recordIdentifier", ex.getRefId());

        // Add timestamp for traceability
        errorBody.put("timestamp", System.currentTimeMillis());

        // Return HTTP 422 response with JSON payload
        return Response
                .status(422)
                .entity(errorBody)
                .type("application/json")
                .build();
    }
}