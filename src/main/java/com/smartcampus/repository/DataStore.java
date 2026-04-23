package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository Pattern - DataStore.
 * 
 * Acts as the central in-memory persistence layer for the Smart Campus system.
 * Implements a thread-safe singleton to ensure consistent shared state across
 * all API requests.
 * 
 * Key Design Features:
 * - Singleton pattern ensures a single shared instance
 * - ConcurrentHashMap provides thread-safe access for concurrent requests
 * - Acts as a lightweight in-memory database replacement
 * 
 * Lifecycle:
 * - All REST resources access this shared instance
 * - Supports concurrent read/write operations safely
 */
public class DataStore {

    // Singleton instance of the DataStore
    private static DataStore instance;

    // Stores all room entities mapped by room ID
    private final Map<String, Room> rooms;

    // Stores all sensor entities mapped by sensor ID
    private final Map<String, Sensor> sensors;

    // Stores sensor readings grouped by sensor ID
    private final Map<String, List<SensorReading>> sensorReadings;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes in-memory collections and loads sample data.
     */
    private DataStore() {
        this.rooms = new ConcurrentHashMap<>();
        this.sensors = new ConcurrentHashMap<>();
        this.sensorReadings = new ConcurrentHashMap<>();
        initializeDefaultData();
    }

    /**
     * Returns the singleton instance of DataStore.
     * Ensures thread-safe lazy initialization.
     *
     * @return shared DataStore instance
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initializes sample data for demonstration and testing purposes.
     * Populates rooms, sensors, and initial sensor readings.
     */
    private void initializeDefaultData() {

        // Sample room data
        rooms.put("LIB-301", new Room("LIB-301", "Library Quiet Study", 25));
        rooms.put("LAB-102", new Room("LAB-102", "Computer Lab", 50));
        rooms.put("CONF-501", new Room("CONF-501", "Conference Room", 100));

        // Sample sensor data
        sensors.put("TEMP-001", new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301"));
        sensors.put("CO2-001", new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LIB-301"));
        sensors.put("OCC-001", new Sensor("OCC-001", "Occupancy", "ACTIVE", 12.0, "LAB-102"));
        sensors.put("TEMP-002", new Sensor("TEMP-002", "Temperature", "MAINTENANCE", 21.0, "CONF-501"));

        // Link sensors to their respective rooms
        rooms.get("LIB-301").getSensorIds().addAll(Arrays.asList("TEMP-001", "CO2-001"));
        rooms.get("LAB-102").getSensorIds().add("OCC-001");
        rooms.get("CONF-501").getSensorIds().add("TEMP-002");

        // Initialize empty reading lists for each sensor
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.put("CO2-001", new ArrayList<>());
        sensorReadings.put("OCC-001", new ArrayList<>());
        sensorReadings.put("TEMP-002", new ArrayList<>());

        // Add a sample reading for demonstration
        SensorReading tempReading = new SensorReading(22.5);
        sensorReadings.get("TEMP-001").add(tempReading);
    }

    // ===== ROOM OPERATIONS =====

    /**
     * Retrieves a room by its ID.
     */
    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * Returns all rooms in the system.
     */
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    /**
     * Adds a new room to the system.
     */
    public Room addRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    /**
     * Deletes a room by ID.
     */
    public boolean deleteRoom(String roomId) {
        return rooms.remove(roomId) != null;
    }

    /**
     * Checks if a room exists in the system.
     */
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    // ===== SENSOR OPERATIONS =====

    /**
     * Retrieves a sensor by its ID.
     */
    public Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    /**
     * Returns all sensors in the system.
     */
    public Collection<Sensor> getAllSensors() {
        return sensors.values();
    }

    /**
     * Retrieves sensors filtered by type (case-insensitive).
     */
    public Collection<Sensor> getSensorsByType(String type) {
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType().equalsIgnoreCase(type)) {
                filtered.add(sensor);
            }
        }
        return filtered;
    }

    /**
     * Adds a new sensor and links it to its associated room.
     */
    public Sensor addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);

        // Link sensor to room if room exists
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }

        return sensor;
    }

    /**
     * Deletes a sensor and removes all associated references.
     */
    public boolean deleteSensor(String sensorId) {
        Sensor sensor = sensors.remove(sensorId);
        if (sensor != null) {

            // Remove sensor from its room
            Room room = rooms.get(sensor.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(sensorId);
            }

            // Remove all stored readings for this sensor
            sensorReadings.remove(sensorId);
            return true;
        }
        return false;
    }

    /**
     * Checks if a sensor exists.
     */
    public boolean sensorExists(String sensorId) {
        return sensors.containsKey(sensorId);
    }

    /**
     * Updates the latest value of a sensor.
     */
    public void updateSensorCurrentValue(String sensorId, double value) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(value);
        }
    }

    // ===== SENSOR READING OPERATIONS =====

    /**
     * Retrieves all readings for a specific sensor.
     */
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    /**
     * Adds a new reading for a sensor.
     */
    public SensorReading addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return reading;
    }

    // ===== BUSINESS LOGIC OPERATIONS =====

    /**
     * Checks if a room has any ACTIVE sensors.
     */
    public boolean hasActiveSensors(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            return false;
        }

        for (String sensorId : room.getSensorIds()) {
            Sensor sensor = sensors.get(sensorId);
            if (sensor != null && "ACTIVE".equals(sensor.getStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of ACTIVE sensors in a room.
     */
    public int getActiveSensorCount(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            return 0;
        }

        int count = 0;
        for (String sensorId : room.getSensorIds()) {
            Sensor sensor = sensors.get(sensorId);
            if (sensor != null && "ACTIVE".equals(sensor.getStatus())) {
                count++;
            }
        }
        return count;
    }
}