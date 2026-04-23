package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical room within the smart campus system.
 *
 * This entity models core room information along with its association
 * to sensors installed within the room.
 *
 * Each room can contain multiple sensors, referenced by their IDs.
 *
 * Key responsibilities:
 * - Store basic room metadata (id, name, capacity)
 * - Maintain references to associated sensors
 * - Provide a simple model for room-sensor relationships
 */
public class Room {

    // Unique identifier for the room
    private String id;

    // Human-readable name of the room
    private String name;

    // Maximum occupancy capacity of the room
    private int capacity;

    // List of sensor IDs associated with this room
    private List<String> sensorIds;

    /**
     * Default constructor initializes an empty sensor list.
     */
    public Room() {
        this.sensorIds = new ArrayList<>();
    }

    /**
     * Parameterized constructor for creating a fully initialized room object.
     *
     * @param roomId unique room identifier
     * @param roomName display name of the room
     * @param maxCapacity maximum occupancy capacity
     */
    public Room(String roomId, String roomName, int maxCapacity) {
        this.id = roomId;
        this.name = roomName;
        this.capacity = maxCapacity;
        this.sensorIds = new ArrayList<>();
    }

    // Getter and setter for room ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for room name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for room capacity
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Getter and setter for sensor ID list
    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    /**
     * Provides a readable string representation of the room object.
     *
     * Includes basic metadata and number of associated sensors.
     */
    @Override
    public String toString() {
        return String.format(
            "Room[id=%s, name=%s, capacity=%d, sensors=%d]",
            id, name, capacity, sensorIds.size()
        );
    }
}