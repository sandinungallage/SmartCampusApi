package com.smartcampus.exception;

/**
 * Exception thrown when attempting to add reading to a sensor in MAINTENANCE or
 * OFFLINE status
 * HTTP 403 Forbidden
 */
public class SensorUnavailableException extends RuntimeException {
    private String sensorId;
    private String status;

    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' is currently in '" + status + "' status and cannot accept new readings.");
        this.sensorId = sensorId;
        this.status = status;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getStatus() {
        return status;
    }
}