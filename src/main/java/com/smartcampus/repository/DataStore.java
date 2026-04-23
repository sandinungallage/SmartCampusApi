package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central in-memory data repository for the Smart Campus system.
 *
 * This class acts as a simple persistence layer using the Singleton pattern.
 * It stores all application data in memory, including rooms, sensors,
 * and sensor readings.
 *
 * Key characteristics:
 * - Singleton ensures a single shared data store across the application
 * - ConcurrentHashMap provides thread-safe access for concurrent requests
 * - No external database is used; data is stored in memory only
 *
 * Architecture context:
 * - Each HTTP request creates a new resource instance (JAX-RS behavior)
 * - All resources interact with this single shared DataStore instance
 * - Thread-safe collections prevent race conditions during concurrent access
 */
public class DataStore {

    // Singleton instance (volatile ensures visibility across threads)
    private static volatile DataStore instance;

    // Stores all room entities indexed by room ID
    private final Map<String, Room> roomRegistry;

    // Stores all sensor entities indexed by sensor ID
    private final Map<String, Sensor> sensorRegistry;

    // Stores historical sensor readings per sensor ID
    private final Map<String, List<SensorReading>> readingHistory;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes in-memory collections and seed data.
     */
    private DataStore() {
        this.roomRegistry = new ConcurrentHashMap<>();
        this.sensorRegistry = new ConcurrentHashMap<>();
        this.readingHistory = new ConcurrentHashMap<>();
        initializeSystemData();
    }

    /**
     * Returns the single instance of DataStore.
     * Synchronized to ensure thread-safe lazy initialization.
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initializes sample system data for rooms, sensors, and relationships.
     *
     * This acts as bootstrap data for the application at startup.
     */
    private void initializeSystemData() {

        // Initialize room data (campus locations)
        roomRegistry.put("ENG-201", new Room("ENG-201", "Engineering Lab Block A", 30));
        roomRegistry.put("SCI-104", new Room("SCI-104", "Science Lecture Hall", 100));
        roomRegistry.put("ADM-501", new Room("ADM-501", "Administration Meeting Room", 20));

        // Initialize sensors assigned to rooms
        sensorRegistry.put("HUM-101", new Sensor("HUM-101", "Humidity", "ACTIVE", 55.0, "ENG-201"));
        sensorRegistry.put("LIGHT-101", new Sensor("LIGHT-101", "Lighting", "ACTIVE", 800.0, "ENG-201"));
        sensorRegistry.put("TEMP-201", new Sensor("TEMP-201", "Temperature", "ACTIVE", 21.5, "SCI-104"));
        sensorRegistry.put("SOUND-101", new Sensor("SOUND-101", "Noise", "MAINTENANCE", 65.0, "ADM-501"));

        // Establish room-to-sensor relationships
        roomRegistry.get("ENG-201").getSensorIds().addAll(Arrays.asList("HUM-101", "LIGHT-101"));
        roomRegistry.get("SCI-104").getSensorIds().add("TEMP-201");
        roomRegistry.get("ADM-501").getSensorIds().add("SOUND-101");

        // Initialize reading history containers
        readingHistory.put("HUM-101", new ArrayList<>());
        readingHistory.put("LIGHT-101", new ArrayList<>());
        readingHistory.put("TEMP-201", new ArrayList<>());
        readingHistory.put("SOUND-101", new ArrayList<>());

        // Seed initial reading example
        SensorReading initialReading = new SensorReading(55.0);
        readingHistory.get("HUM-101").add(initialReading);
    }

    // ================= ROOM OPERATIONS =================

    // Retrieve a room by ID
    public Room retrieveRoom(String roomId) {
        return roomRegistry.get(roomId);
    }

    // Retrieve all rooms
    public Collection<Room> fetchAllRooms() {
        return roomRegistry.values();
    }

    // Save or update a room
    public Room saveRoom(Room room) {
        roomRegistry.put(room.getId(), room);
        return room;
    }

    // Remove a room by ID
    public boolean removeRoom(String roomId) {
        return roomRegistry.remove(roomId) != null;
    }

    // Check if a room exists
    public boolean roomExists(String roomId) {
        return roomRegistry.containsKey(roomId);
    }

    // ================= SENSOR OPERATIONS =================

    // Retrieve a sensor by ID
    public Sensor retrieveSensor(String sensorId) {
        return sensorRegistry.get(sensorId);
    }

    // Retrieve all sensors
    public Collection<Sensor> fetchAllSensors() {
        return sensorRegistry.values();
    }

    // Find sensors by type (case-insensitive match)
    public Collection<Sensor> findSensorsByType(String sensorType) {
        List<Sensor> matches = new ArrayList<>();
        for (Sensor sensor : sensorRegistry.values()) {
            if (sensor.getType().equalsIgnoreCase(sensorType)) {
                matches.add(sensor);
            }
        }
        return matches;
    }

    // Save sensor and link it to its room
    public Sensor saveSensor(Sensor sensor) {
        sensorRegistry.put(sensor.getId(), sensor);

        Room room = roomRegistry.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }
        return sensor;
    }

    // Remove sensor and clean up references
    public boolean removeSensor(String sensorId) {
        Sensor sensor = sensorRegistry.remove(sensorId);
        if (sensor != null) {

            Room room = roomRegistry.get(sensor.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(sensorId);
            }

            // Remove historical readings as well
            readingHistory.remove(sensorId);
            return true;
        }
        return false;
    }

    // Check sensor existence
    public boolean sensorExists(String sensorId) {
        return sensorRegistry.containsKey(sensorId);
    }

    // Update current sensor value
    public void updateSensorCurrentValue(String sensorId, double newValue) {
        Sensor sensor = sensorRegistry.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(newValue);
        }
    }

    // ================= SENSOR READINGS =================

    // Get reading history for a sensor
    public List<SensorReading> fetchReadingHistory(String sensorId) {
        return readingHistory.getOrDefault(sensorId, new ArrayList<>());
    }

    // Save a new sensor reading
    public SensorReading saveReading(String sensorId, SensorReading reading) {
        readingHistory
            .computeIfAbsent(sensorId, k -> new ArrayList<>())
            .add(reading);
        return reading;
    }

    // ================= BUSINESS LOGIC QUERIES =================

    // Check if a room has at least one active sensor
    public boolean roomHasActiveSensors(String roomId) {
        Room room = roomRegistry.get(roomId);
        if (room == null) {
            return false;
        }

        for (String sensorId : room.getSensorIds()) {
            Sensor sensor = sensorRegistry.get(sensorId);
            if (sensor != null && "ACTIVE".equals(sensor.getStatus())) {
                return true;
            }
        }
        return false;
    }

    // Count active sensors in a room
    public int countActiveSensorsInRoom(String roomId) {
        Room room = roomRegistry.get(roomId);
        if (room == null) {
            return 0;
        }

        int counter = 0;
        for (String sensorId : room.getSensorIds()) {
            Sensor sensor = sensorRegistry.get(sensorId);
            if (sensor != null && "ACTIVE".equals(sensor.getStatus())) {
                counter++;
            }
        }
        return counter;
    }
}