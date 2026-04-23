package com.smartcampus.exception;

/**
 * Custom exception thrown when an attempt is made to add a reading
 * to a sensor that is not in an active state.
 * 
 * This typically occurs when a sensor is in MAINTENANCE or OFFLINE status,
 * meaning it is temporarily or permanently unable to process new data.
 * 
 * In REST API terms, this is commonly mapped to HTTP 403 Forbidden,
 * since the operation is not allowed in the current sensor state.
 */
public class SensorUnavailableException extends RuntimeException {

    // Unique identifier of the sensor that triggered the exception
    private String sensorId;

    // Current operational status of the sensor (e.g., MAINTENANCE, OFFLINE)
    private String status;

    /**
     * Constructs a new SensorUnavailableException with sensor details.
     *
     * @param sensorId the ID of the sensor that cannot accept readings
     * @param status the current status of the sensor
     */
    public SensorUnavailableException(String sensorId, String status) {

        // Detailed message explaining why the sensor operation is blocked
        super("Sensor '" + sensorId + "' is currently in '" + status + "' status and cannot accept new readings.");

        // Store sensor ID for later use in error handling or API responses
        this.sensorId = sensorId;

        // Store sensor status for debugging and client feedback
        this.status = status;
    }

    /**
     * Returns the ID of the sensor that caused the exception.
     *
     * @return sensor ID
     */
    public String getSensorId() {
        return sensorId;
    }

    /**
     * Returns the current status of the sensor.
     *
     * @return sensor status (e.g., MAINTENANCE, OFFLINE)
     */
    public String getStatus() {
        return status;
    }
}