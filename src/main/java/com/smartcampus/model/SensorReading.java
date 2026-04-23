package com.smartcampus.model;

import java.util.UUID;

/**
 * SensorReading Entity.
 * 
 * Represents a single data reading captured from a sensor at a specific point in time.
 * Each reading contains a unique identifier, timestamp, and measured value.
 * 
 * This class is used to store historical or real-time sensor data in the system.
 */
public class SensorReading {

    // Unique identifier for each sensor reading
    private String id;

    // Timestamp indicating when the reading was recorded (in milliseconds)
    private long timestamp;

    // Actual measured value from the sensor
    private double value;

    /**
     * Default constructor.
     * 
     * Automatically generates a unique ID and assigns the current system time
     * as the timestamp of the reading.
     */
    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor that initializes a sensor reading with a value.
     * 
     * Automatically generates a unique ID and assigns the current system time
     * as the timestamp.
     *
     * @param value measured sensor value
     */
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    /**
     * Returns the unique identifier of the reading.
     *
     * @return reading ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the reading.
     *
     * @param id reading identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the timestamp when the reading was recorded.
     *
     * @return timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the reading.
     *
     * @param timestamp time in milliseconds
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the measured sensor value.
     *
     * @return sensor reading value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the measured sensor value.
     *
     * @param value sensor reading value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the SensorReading object.
     * Useful for logging and debugging purposes.
     *
     * @return formatted sensor reading details
     */
    @Override
    public String toString() {
        return "SensorReading{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}