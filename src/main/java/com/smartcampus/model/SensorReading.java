package com.smartcampus.model;

import java.util.UUID;

/**
 * Represents a single sensor reading in the system.
 *
 * This is a time-series data entity used to store individual measurements
 * captured from sensors at a specific point in time.
 *
 * Each reading contains:
 * - A unique identifier
 * - A timestamp indicating when the measurement was taken
 * - The recorded sensor value
 *
 * This model is fundamental for tracking historical sensor data
 * and enabling analytics over time.
 */
public class SensorReading {

    // Unique identifier for the reading
    private String id;

    // Timestamp of when the reading was recorded (epoch milliseconds)
    private long timestamp;

    // Measured value from the sensor
    private double value;

    /**
     * Default constructor.
     * Automatically generates a unique ID and assigns the current timestamp.
     */
    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for creating a reading with a specified value.
     * ID and timestamp are automatically generated.
     *
     * @param measurementValue sensor measurement value
     */
    public SensorReading(double measurementValue) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = measurementValue;
    }

    // Getter and setter for reading ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getter and setter for measured value
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns a formatted string representation of the sensor reading.
     *
     * Useful for logging and debugging purposes.
     */
    @Override
    public String toString() {
        return String.format(
            "SensorReading[id=%s, ts=%d, val=%.2f]",
            id, timestamp, value
        );
    }
}