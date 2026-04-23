package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.repository.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

/**
 * REST resource for managing Sensor entities.
 *
 * Base path: /api/v1/sensors
 *
 * This class provides endpoints for:
 * - Registering new sensors
 * - Retrieving sensor details
 * - Listing sensors with optional filtering
 * - Delegating reading management to a sub-resource
 *
 * Responsibilities:
 * - Acts as the controller layer for sensor-related operations
 * - Enforces validation rules and referential integrity
 * - Delegates persistence to the DataStore
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // Shared in-memory data store (singleton)
    private final DataStore store = DataStore.getInstance();

    /**
     * Retrieves all sensors or filters them by type.
     *
     * Endpoints:
     * - GET /api/v1/sensors
     * - GET /api/v1/sensors?type=Temperature
     *
     * Query parameter filtering is used instead of path-based filtering
     * to support flexible and extensible queries.
     *
     * Benefits of @QueryParam:
     * - Supports multiple optional filters
     * - Avoids excessive endpoint proliferation
     * - Aligns with RESTful filtering conventions
     */
    @GET
    public Response retrieveSensors(@QueryParam("type") String filterType) {

        Collection<Sensor> results;

        // Apply filter if provided
        if (filterType != null && !filterType.trim().isEmpty()) {
            results = store.findSensorsByType(filterType);
        } else {
            results = store.fetchAllSensors();
        }

        // Build response payload
        Map<String, Object> body = new HashMap<>();
        body.put("count", results.size());

        // Include applied filter information if present
        if (filterType != null) {
            body.put("appliedFilter", "type=" + filterType);
        }

        body.put("items", results);
        body.put("timestamp", System.currentTimeMillis());

        return Response.ok(body).build();
    }

    /**
     * Registers a new sensor in the system.
     *
     * Endpoint: POST /api/v1/sensors
     *
     * Validation rules:
     * - Sensor ID must be provided and unique
     * - Associated room must exist (foreign key constraint)
     *
     * JAX-RS content negotiation behavior:
     * - Only application/json is accepted
     * - Invalid media types (e.g. text/plain, XML) are rejected
     *   before method execution with HTTP 415
     */
    @POST
    public Response registerSensor(Sensor incomingSensor) {

        // Validate sensor identifier
        if (incomingSensor.getId() == null || incomingSensor.getId().trim().isEmpty()) {
            return buildErrorResponse(
                    400,
                    "InvalidInput",
                    "Sensor identifier is required."
            );
        }

        // Validate room association
        if (incomingSensor.getRoomId() == null || incomingSensor.getRoomId().trim().isEmpty()) {
            return buildErrorResponse(
                    400,
                    "InvalidInput",
                    "Parent room identifier is required."
            );
        }

        // Enforce referential integrity (room must exist)
        if (!store.roomExists(incomingSensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", incomingSensor.getRoomId());
        }

        // Prevent duplicate sensor registration
        if (store.sensorExists(incomingSensor.getId())) {
            return buildErrorResponse(
                    409,
                    "DuplicateResource",
                    "Sensor '" + incomingSensor.getId() + "' is already registered."
            );
        }

        // Set default status if not provided
        if (incomingSensor.getStatus() == null || incomingSensor.getStatus().trim().isEmpty()) {
            incomingSensor.setStatus("ACTIVE");
        }

        // Persist sensor
        Sensor registered = store.saveSensor(incomingSensor);

        // Build success response
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Sensor successfully registered in system.");
        body.put("registeredSensor", registered);

        return Response
                .status(Response.Status.CREATED)
                .entity(body)
                .build();
    }

    /**
     * Retrieves details of a specific sensor.
     *
     * Endpoint: GET /api/v1/sensors/{sensorId}
     *
     * Returns 404 if the sensor does not exist.
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorDetails(@PathParam("sensorId") String sensorId) {

        Sensor sensor = store.retrieveSensor(sensorId);

        if (sensor == null) {
            return buildErrorResponse(
                    404,
                    "NotFound",
                    "Sensor '" + sensorId + "' could not be found."
            );
        }

        Map<String, Object> body = new HashMap<>();
        body.put("sensor", sensor);

        return Response.ok(body).build();
    }

    /**
     * Sub-resource locator for sensor readings.
     *
     * Endpoint: /api/v1/sensors/{sensorId}/readings
     *
     * Design benefits:
     * - Separates reading logic from sensor management
     * - Improves modularity and readability
     * - Enables independent testing of sub-resource
     * - Keeps controller size manageable as system scales
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource accessSensorReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    /**
     * Builds a standardized error response structure.
     *
     * Ensures consistent error formatting across all endpoints.
     *
     * @param statusCode HTTP status code
     * @param errorType logical error identifier
     * @param detail human-readable message
     * @return structured HTTP response
     */
    private Response buildErrorResponse(int statusCode, String errorType, String detail) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", statusCode);
        errorBody.put("error", errorType);
        errorBody.put("detail", detail);
        errorBody.put("timestamp", System.currentTimeMillis());

        return Response
                .status(statusCode)
                .entity(errorBody)
                .type("application/json")
                .build();
    }
}