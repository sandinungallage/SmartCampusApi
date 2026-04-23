package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for RoomNotEmptyException.
 *
 * This mapper handles cases where a room cannot be deleted or modified
 * because it still contains dependent resources (e.g., active sensors).
 *
 * HTTP status used: 409 Conflict
 *
 * Why 409 Conflict is used:
 * - The request is valid in format but cannot be completed due to the current state
 * - The resource exists, but its current state violates the requested operation
 * - Indicates that the client must resolve dependencies before retrying
 *
 * Typical resolution:
 * - Remove all associated sensors or dependent entities first
 * - Retry the requested operation after the state is cleared
 */
@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    /**
     * Converts RoomNotEmptyException into a structured HTTP response.
     *
     * @param ex exception containing details about the conflicting room state
     * @return HTTP 409 response with structured error details
     */
    @Override
    public Response toResponse(RoomNotEmptyException ex) {

        // Construct error response payload
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", 409);
        errorBody.put("error", "ConflictingState");
        errorBody.put("message", ex.getMessage());

        // Include affected resource information for client-side handling
        errorBody.put("affectedRoom", ex.getId());

        // Provide actionable guidance for resolving the conflict
        errorBody.put("retryAfter", "Remove all active sensors from the room");

        // Timestamp for debugging and traceability
        errorBody.put("timestamp", System.currentTimeMillis());

        // Return HTTP 409 Conflict response
        return Response
                .status(Response.Status.CONFLICT)
                .entity(errorBody)
                .type("application/json")
                .build();
    }
}