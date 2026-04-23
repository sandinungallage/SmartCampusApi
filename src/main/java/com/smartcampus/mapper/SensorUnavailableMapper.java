package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for SensorUnavailableException.
 *
 * This mapper handles cases where a sensor cannot perform operations
 * because it is not in a valid state to accept requests.
 *
 * HTTP status used: 403 Forbidden
 *
 * Why 403 is used:
 * - The request is understood and valid in format
 * - The operation is rejected due to the current state of the sensor
 * - The client is not allowed to proceed until the sensor reaches a valid state
 *
 * Typical scenario:
 * - Sensor is not in ACTIVE state
 * - Only ACTIVE sensors are permitted to process or record readings
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    /**
     * Converts SensorUnavailableException into a structured HTTP response.
     *
     * @param ex exception containing sensor state and validation details
     * @return HTTP 403 response with structured error information
     */
    @Override
    public Response toResponse(SensorUnavailableException ex) {

        // Construct error response body
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", 403);
        errorBody.put("error", "MissingPermission");
        errorBody.put("message", ex.getMessage());

        // Include sensor-specific details for debugging and client handling
        errorBody.put("sensorIdentifier", ex.getSensorId());
        errorBody.put("sensorState", ex.getState());

        // Define valid states that allow operation
        errorBody.put("permittedStates", new String[]{"ACTIVE"});

        // Timestamp for traceability
        errorBody.put("timestamp", System.currentTimeMillis());

        // Return HTTP 403 Forbidden response
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(errorBody)
                .type("application/json")
                .build();
    }
}