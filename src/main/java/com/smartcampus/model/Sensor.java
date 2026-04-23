package com.smartcampus.model;

/**
 * Sensor Entity.
 * 
 * Represents a sensor device deployed within a room in the Smart Campus system.
 * Each sensor collects environmental or system data (e.g., temperature, humidity)
 * and is associated with a specific room.
 * 
 * This class is a core domain model used in sensor management APIs.
 */
public class Sensor {

    // Unique identifier for the sensor
    private String id;

    // Type of sensor (e.g., temperature, humidity, CO2, motion)
    private String type;

    // Operational status of the sensor (e.g., ACTIVE, OFFLINE, MAINTENANCE)
    private String status;

    // Latest recorded value from the sensor
    private double currentValue;

    // ID of the room to which this sensor is assigned
    private String roomId;

    /**
     * Default constructor required for serialization/deserialization frameworks.
     */
    public Sensor() {
    }

    /**
     * Parameterized constructor for creating a fully initialized Sensor object.
     *
     * @param id unique sensor identifier
     * @param type type/category of sensor
     * @param status current operational status
     * @param currentValue latest recorded reading
     * @param roomId associated room identifier
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    /**
     * Returns the sensor ID.
     *
     * @return sensor identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the sensor ID.
     *
     * @param id unique sensor identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the sensor type.
     *
     * @return sensor type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the sensor type.
     *
     * @param type sensor category (e.g., temperature)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the current operational status of the sensor.
     *
     * @return sensor status (e.g., ACTIVE, OFFLINE)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the operational status of the sensor.
     *
     * @param status sensor state
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the latest recorded sensor value.
     *
     * @return current sensor reading
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets the latest sensor value.
     *
     * @param currentValue latest reading from sensor
     */
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Returns the ID of the room this sensor is assigned to.
     *
     * @return room identifier
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the room ID for this sensor.
     *
     * @param roomId associated room identifier
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns a string representation of the Sensor object.
     * Useful for logging and debugging purposes.
     *
     * @return formatted sensor details
     */
    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", currentValue=" + currentValue +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}