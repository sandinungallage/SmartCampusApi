package com.smartcampus.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON Processing Exception Mapper - Catch JsonProcessingException (parent of
 * parse and mapping errors)
 * Malformed JSON (syntax errors like missing braces) = Server Error = 500
 * Returns HTTP 500 Internal Server Error with clean JSON (no stack trace)
 * Prevents information disclosure - logs internally for admins only
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    private static final Logger LOGGER = Logger.getLogger(JsonParseExceptionMapper.class.getName());

    @Override
    public Response toResponse(JsonProcessingException exception) {
        // Log internally with full stack trace for admins only
        System.err.println(">>> JSONPROCESSINGEXCEPTIONMAPPER TRIGGERED: " + exception.getClass().getName() + " <<<");
        LOGGER.log(Level.SEVERE, "JSON processing error: " + exception.getClass().getSimpleName(), exception);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 500);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "Request processing failed. Please contact support.");
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("errorId", System.nanoTime());

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type("application/json")
                .build();
    }
}
