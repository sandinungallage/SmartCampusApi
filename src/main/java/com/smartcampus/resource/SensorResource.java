package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Sensor Resource
 * Manages /api/v1/sensors endpoint
 * Implements CRUD operations with reference validation and filtering
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final DataStore dataStore = DataStore.getInstance();

    /**
     * GET /api/v1/sensors
     * GET /api/v1/sensors?type=Temperature
     * Returns all sensors or filtered by type using @QueryParam
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> sensors;

        if (type != null && !type.trim().isEmpty()) {
            sensors = dataStore.getSensorsByType(type);
        } else {
            sensors = dataStore.getAllSensors();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", sensors.size());
        if (type != null) {
            response.put("filter", "type=" + type);
        }
        response.put("data", sensors);

        return Response.ok(response).build();
    }

    /**
     * POST /api/v1/sensors
     * Creates new sensor with roomId validation
     * VALIDATES: roomId must reference existing room
     */
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate sensor ID
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Sensor ID is required.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }

        // Validate room ID
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Room ID is required.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }

        // CRITICAL: Validate referenced room exists
        if (!dataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }

        // Check if sensor already exists
        if (dataStore.sensorExists(sensor.getId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 409);
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", "Sensor with ID '" + sensor.getId() + "' already exists.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(errorResponse)
                    .build();
        }

        // Set default status if not provided
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        Sensor createdSensor = dataStore.addSensor(sensor);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sensor created successfully.");
        response.put("data", createdSensor);

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    /**
     * GET /api/v1/sensors/{sensorId}
     * Returns specific sensor
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.getSensor(sensorId);

        if (sensor == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Sensor with ID '" + sensorId + "' not found.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", sensor);

        return Response.ok(response).build();
    }

    /**
     * SUB-RESOURCE LOCATOR PATTERN
     * Returns SensorReadingResource for handling
     * /api/v1/sensors/{sensorId}/readings
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}