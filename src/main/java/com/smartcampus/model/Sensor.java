package com.smartcampus.model;

/**
 * Represents a sensor device in the smart campus system.
 *
 * Sensors are physical or virtual devices responsible for collecting
 * and reporting measurements such as temperature, humidity, or occupancy.
 *
 * Each sensor:
 * - Has a unique identifier
 * - Belongs to a specific type (e.g., temperature, motion, etc.)
 * - Maintains a current operational status
 * - Stores its latest measured value
 * - Is associated with a specific room in the system
 *
 * This entity supports real-time monitoring and data collection
 * within the campus infrastructure.
 */
public class Sensor {

    // Unique identifier for the sensor
    private String id;

    // Type/category of the sensor (e.g., temperature, humidity)
    private String type;

    // Operational status of the sensor (e.g., ACTIVE, INACTIVE)
    private String status;

    // Latest recorded measurement from the sensor
    private double currentValue;

    // Identifier of the room this sensor belongs to
    private String roomId;

    /**
     * Default constructor required for frameworks and serialization.
     */
    public Sensor() {
    }

    /**
     * Fully parameterized constructor for creating a sensor instance.
     *
     * @param sensorId unique sensor identifier
     * @param sensorType type/category of sensor
     * @param sensorStatus current operational state
     * @param initialValue initial reading value
     * @param parentRoomId associated room identifier
     */
    public Sensor(String sensorId, String sensorType, String sensorStatus,
                  double initialValue, String parentRoomId) {
        this.id = sensorId;
        this.type = sensorType;
        this.status = sensorStatus;
        this.currentValue = initialValue;
        this.roomId = parentRoomId;
    }

    // Getter and setter for sensor ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for sensor type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and setter for sensor status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and setter for current sensor value
    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    // Getter and setter for associated room ID
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns a readable representation of the sensor object.
     *
     * Useful for logging and debugging purposes.
     */
    @Override
    public String toString() {
        return String.format(
            "Sensor[id=%s, type=%s, status=%s, val=%.2f, room=%s]",
            id, type, status, currentValue, roomId
        );
    }
}