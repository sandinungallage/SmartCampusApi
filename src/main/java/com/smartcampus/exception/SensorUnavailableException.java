package com.smartcampus.exception;

/**
 * Exception thrown when a sensor is not in a valid state to accept readings.
 *
 * This occurs when a client attempts to submit data to a sensor that is
 * not operational (for example, when it is in MAINTENANCE or OFFLINE state).
 *
 * The exception represents a state-based restriction rather than a structural
 * issue with the request.
 *
 * This situation is mapped to HTTP 403 (Forbidden), indicating that the
 * request is understood but cannot be processed due to the current state
 * of the resource.
 */
public class SensorUnavailableException extends RuntimeException {

    // Identifier of the sensor that rejected the operation
    private final String sensorId;

    // Current state of the sensor (e.g., ACTIVE, MAINTENANCE, OFFLINE)
    private final String state;

    /**
     * Constructs the exception with details about the sensor and its state.
     *
     * @param sensorId the identifier of the sensor
     * @param state    the current state preventing the operation
     */
    public SensorUnavailableException(String sensorId, String state) {

        // Descriptive message explaining why the operation was rejected
        super(String.format(
            "Sensor '%s' is in '%s' state and cannot process incoming readings at this time. Please retry when sensor is ACTIVE.",
            sensorId, state));

        this.sensorId = sensorId;
        this.state = state;
    }

    /**
     * Returns the identifier of the affected sensor.
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * Returns the current state of the sensor.
     */
    public String getState() {
        return state;
    }
}