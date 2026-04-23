package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for RoomNotEmptyException.
 * 
 * This mapper handles cases where a room cannot be deleted because it
 * still contains active or linked resources (e.g., sensors).
 * 
 * Business rule:
 * - A room must be empty before deletion is allowed
 * 
 * HTTP Response:
 * - Returns HTTP 409 Conflict
 * - Indicates that the request conflicts with current system state
 */
@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    /**
     * Converts RoomNotEmptyException into a standardized HTTP response.
     *
     * @param exception the custom exception containing room constraint details
     * @return HTTP 409 response with structured JSON error body
     */
    @Override
    public Response toResponse(RoomNotEmptyException exception) {

        // Build structured error response for client
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("status", 409);
        errorResponse.put("error", "Conflict");

        // Human-readable message describing why deletion failed
        errorResponse.put("message", exception.getMessage());

        // Timestamp of when the error occurred
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Room identifier that caused the conflict
        errorResponse.put("resourceId", exception.getRoomId());

        // Return HTTP 409 Conflict response with JSON body
        return Response
                .status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}