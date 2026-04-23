package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for SensorUnavailableException.
 * 
 * This mapper handles cases where a sensor is not allowed to accept new readings
 * due to its current operational state.
 * 
 * Business rule:
 * - Sensors in MAINTENANCE or OFFLINE state cannot process new data
 * 
 * HTTP Response:
 * - Returns HTTP 403 Forbidden
 * - Indicates that the request is understood but not permitted in current state
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    /**
     * Converts SensorUnavailableException into a standardized HTTP response.
     *
     * @param exception the custom exception containing sensor state details
     * @return HTTP 403 response with structured JSON error body
     */
    @Override
    public Response toResponse(SensorUnavailableException exception) {

        // Build structured error response for client
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("status", 403);
        errorResponse.put("error", "Forbidden");

        // Human-readable message describing why the operation is blocked
        errorResponse.put("message", exception.getMessage());

        // Timestamp when the error occurred
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Sensor-specific details for debugging and client feedback
        errorResponse.put("sensorId", exception.getSensorId());
        errorResponse.put("currentStatus", exception.getStatus());

        // Return HTTP 403 response with JSON body
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}