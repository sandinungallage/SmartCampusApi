package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper for RoomNotEmptyException
 * Returns HTTP 409 Conflict
 */
@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 409);
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("resourceId", exception.getRoomId());

        return Response
                .status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}