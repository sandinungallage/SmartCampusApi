package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for SensorUnavailableException
 * Returns HTTP 403 Forbidden
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 403);
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("sensorId", exception.getSensorId());
        errorResponse.put("currentStatus", exception.getStatus());

        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}