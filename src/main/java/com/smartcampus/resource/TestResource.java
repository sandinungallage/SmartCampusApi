package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test Resource
 *
 * Used ONLY for demonstration/testing purposes.
 * This endpoint intentionally throws a RuntimeException
 * to trigger the GlobalExceptionMapper.
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    /**
     * GET /api/v1/test/error
     *
     * Simulates a server-side failure
     * This will be caught by GlobalExceptionMapper
     */
    @GET
    @Path("/error")
    public Response triggerError() {

        // Simulate unexpected server failure
        throw new RuntimeException("Simulated server crash");

    }
}