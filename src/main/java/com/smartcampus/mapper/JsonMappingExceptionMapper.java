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
 * JSON Mapping Exception Mapper - Catch type/field mismatch errors
 * Valid JSON but wrong types (e.g., string instead of int) = CLIENT ERROR = 400
 * Returns HTTP 400 Bad Request with clean JSON (no stack trace)
 * Prevents information disclosure - logs internally for admins only
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<MismatchedInputException> {
    private static final Logger LOGGER = Logger.getLogger(JsonMappingExceptionMapper.class.getName());

    @Override
    public Response toResponse(MismatchedInputException exception) {
        // Log internally with full stack trace for admins only
        System.err.println(">>> MISMATCHEDINPUTEXCEPTIONMAPPER TRIGGERED <<<");
        LOGGER.log(Level.WARNING, "JSON type mismatch: " + exception.getOriginalMessage(), exception);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", "Invalid JSON structure. Please check request fields and types.");
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("errorId", System.nanoTime());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}
