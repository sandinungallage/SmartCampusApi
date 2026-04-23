package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Room Entity.
 * 
 * Represents a physical room within the Smart Campus system.
 * Each room can have multiple sensors assigned to it and has
 * attributes such as name and capacity.
 * 
 * This class is used as a core domain model for room management APIs.
 */
public class Room {

    // Unique identifier for the room
    private String id;

    // Human-readable name of the room (e.g., "Lecture Hall A")
    private String name;

    // Maximum number of occupants the room can hold
    private int capacity;

    // List of sensor IDs assigned to this room
    private List<String> sensorIds;

    /**
     * Default constructor.
     * Initializes the sensor list to avoid null pointer issues.
     */
    public Room() {
        this.sensorIds = new ArrayList<>();
    }

    /**
     * Parameterized constructor for creating a room with basic details.
     *
     * @param id unique room identifier
     * @param name name of the room
     * @param capacity maximum capacity of the room
     */
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;

        // Initialize sensor list for safe operations
        this.sensorIds = new ArrayList<>();
    }

    /**
     * Returns the room ID.
     *
     * @return room identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the room ID.
     *
     * @param id unique identifier for the room
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the room name.
     *
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the room name.
     *
     * @param name name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the maximum capacity of the room.
     *
     * @return room capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the room capacity.
     *
     * @param capacity maximum number of occupants
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns the list of sensor IDs assigned to this room.
     *
     * @return list of sensor identifiers
     */
    public List<String> getSensorIds() {
        return sensorIds;
    }

    /**
     * Sets the list of sensor IDs for this room.
     *
     * @param sensorIds list of sensor identifiers
     */
    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    /**
     * Returns a string representation of the Room object.
     * Useful for logging and debugging purposes.
     *
     * @return formatted string representation of room
     */
    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", sensorIds=" + sensorIds +
                '}';
    }
}