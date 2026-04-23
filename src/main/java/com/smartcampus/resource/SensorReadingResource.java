package com.smartcampus.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Sensor Reading Resource (Sub-Resource)
 * Manages /api/v1/sensors/{sensorId}/readings endpoint
 * Handles historical reading data for a specific sensor
 *
 * SUB-RESOURCE LOCATOR PATTERN:
 * - Instantiated by SensorResource.getSensorReadings()
 * - Receives sensorId context via constructor
 * - Manages complex nested operations separately from parent
 * - Benefits: Reduced complexity, separation of concerns, easier testing
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private final String sensorId;
    private final DataStore dataStore = DataStore.getInstance();
    private static final ObjectMapper mapper = new ObjectMapper();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     * Returns all readings for a sensor
     */
    @GET
    public Response getReadings() {
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

        List<SensorReading> readings = dataStore.getReadingsForSensor(sensorId);

        Map<String, Object> response = new HashMap<>();
        response.put("sensorId", sensorId);
        response.put("sensorType", sensor.getType());
        response.put("total", readings.size());
        response.put("data", readings);
        if (!readings.isEmpty()) {
            response.put("latestValue", readings.get(readings.size() - 1).getValue());
        }

        return Response.ok(response).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings
     * Adds new reading for sensor
     *
     * CONSTRAINT: Sensor must be ACTIVE (not MAINTENANCE or OFFLINE)
     * SIDE EFFECT: Updates parent Sensor's currentValue for data consistency
     * 
     * NOTE: Manually deserialize JSON to allow exception mappers to catch parsing
     * errors
     * This ensures malformed JSON (500 error) vs type mismatch (400 error) are
     * handled correctly
     */
    @POST
    public Response addReading(String jsonBody) throws com.fasterxml.jackson.core.JsonProcessingException {
        // Manually deserialize JSON - allows exception mappers to catch parsing errors
        SensorReading reading = mapper.readValue(jsonBody, SensorReading.class);

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

        // CONSTRAINT: Check sensor status - must be ACTIVE
        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        if ("OFFLINE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Add reading to history
        SensorReading addedReading = dataStore.addReading(sensorId, reading);

        // SIDE EFFECT: Update sensor's current value for data consistency
        dataStore.updateSensorCurrentValue(sensorId, reading.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reading recorded successfully.");
        response.put("sensorId", sensorId);
        response.put("data", addedReading);
        response.put("sensorUpdated", new HashMap<String, Object>() {
            {
                put("id", sensorId);
                put("currentValue", reading.getValue());
            }
        });

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}