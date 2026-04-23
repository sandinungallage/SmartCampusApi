package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.repository.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

/**
 * Sub-resource for managing sensor readings.
 *
 * Base path: /api/v1/sensors/{sensorId}/readings
 *
 * This class is responsible for handling all operations related to
 * sensor readings for a specific sensor.
 *
 * Design pattern used: Sub-Resource Locator
 * - This resource is not directly instantiated by the framework
 * - It is created by a parent SensorResource
 * - The sensorId is passed as contextual state
 *
 * Responsibilities:
 * - Retrieve historical sensor readings
 * - Record new sensor measurements
 * - Maintain consistency between sensor state and readings
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Sensor ID provided by the parent resource
    private final String contextSensorId;

    // Shared in-memory data store
    private final DataStore store = DataStore.getInstance();

    /**
     * Constructor used by parent resource to inject sensor context.
     *
     * @param sensorId identifier of the target sensor
     */
    public SensorReadingResource(String sensorId) {
        this.contextSensorId = sensorId;
    }

    /**
     * Retrieves all historical readings for a sensor.
     *
     * Endpoint: GET /api/v1/sensors/{sensorId}/readings
     *
     * Response includes:
     * - Sensor metadata (ID and type)
     * - Total number of readings
     * - Full reading history
     * - Latest recorded reading (if available)
     *
     * Returns 404 if the sensor does not exist.
     */
    @GET
    public Response getReadingHistory() {

        // Validate sensor existence
        Sensor sensor = store.retrieveSensor(contextSensorId);

        if (sensor == null) {
            return buildErrorResponse(
                    404,
                    "NotFound",
                    "Sensor '" + contextSensorId + "' not found. Cannot retrieve readings."
            );
        }

        // Retrieve reading history
        List<SensorReading> history = store.fetchReadingHistory(contextSensorId);

        // Build response payload
        Map<String, Object> body = new HashMap<>();
        body.put("sensorId", contextSensorId);
        body.put("sensorType", sensor.getType());
        body.put("recordCount", history.size());
        body.put("readings", history);

        // Include latest reading for convenience
        if (!history.isEmpty()) {
            SensorReading latest = history.get(history.size() - 1);

            body.put("latestReading", new HashMap<String, Object>() {{
                put("value", latest.getValue());
                put("timestamp", latest.getTimestamp());
            }});
        }

        return Response.ok(body).build();
    }

    /**
     * Records a new reading for the sensor.
     *
     * Endpoint: POST /api/v1/sensors/{sensorId}/readings
     *
     * Business rules:
     * - Sensor must exist
     * - Sensor must be in ACTIVE state
     *
     * Restrictions:
     * - MAINTENANCE or OFFLINE sensors cannot accept readings
     *
     * Side effects:
     * - Stores reading in history
     * - Updates sensor's current value for quick access
     */
    @POST
    public Response recordReading(SensorReading incomingReading) {

        // Validate sensor existence
        Sensor sensor = store.retrieveSensor(contextSensorId);

        if (sensor == null) {
            return buildErrorResponse(
                    404,
                    "NotFound",
                    "Sensor '" + contextSensorId + "' not found. Cannot record reading."
            );
        }

        // Validate sensor operational state
        String sensorState = sensor.getStatus();
        if ("MAINTENANCE".equals(sensorState) || "OFFLINE".equals(sensorState)) {
            throw new SensorUnavailableException(contextSensorId, sensorState);
        }

        // Persist new reading
        SensorReading recorded = store.saveReading(contextSensorId, incomingReading);

        // Update latest sensor value for quick access
        store.updateSensorCurrentValue(contextSensorId, incomingReading.getValue());

        // Build response payload
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Reading successfully recorded.");
        body.put("recordedReading", recorded);

        // Confirmation of sensor update
        body.put("sensorUpdated", new HashMap<String, Object>() {{
            put("sensorId", contextSensorId);
            put("newCurrentValue", incomingReading.getValue());
        }});

        return Response
                .status(Response.Status.CREATED)
                .entity(body)
                .build();
    }

    /**
     * Builds a standardized error response.
     *
     * Ensures consistent structure across all error responses:
     * - status code
     * - error type
     * - descriptive message
     * - timestamp for debugging
     *
     * @param statusCode HTTP status code
     * @param errorType logical error identifier
     * @param detail human-readable error message
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