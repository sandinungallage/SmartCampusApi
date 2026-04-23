package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for LinkedResourceNotFoundException
 * Returns HTTP 422 Unprocessable Entity
 */
@Provider
public class LinkedResourceMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 422);
        errorResponse.put("error", "Unprocessable Entity");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("resourceType", exception.getResourceType());
        errorResponse.put("resourceId", exception.getResourceId());

        return Response
                .status(422)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}