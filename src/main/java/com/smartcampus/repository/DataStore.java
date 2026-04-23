package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository Pattern - DataStore
 * Thread-safe singleton managing all in-memory data persistence
 *
 * CRITICAL: Uses ConcurrentHashMap to handle concurrent access across multiple
 * request threads.
 * This ensures thread safety when handling concurrent API requests without
 * explicit locking.
 *
 * Lifecycle:
 * - Request-scoped resources are instantiated per-request
 * - All resources access this SHARED singleton DataStore
 * - ConcurrentHashMap provides atomic operations for data consistency
 */
public class DataStore {
    private static DataStore instance;
    private final Map<String, Room> rooms;
    private final Map<String, Sensor> sensors;
    private final Map<String, List<SensorReading>> sensorReadings;

    private DataStore() {
        this.rooms = new ConcurrentHashMap<>();
        this.sensors = new ConcurrentHashMap<>();
        this.sensorReadings = new ConcurrentHashMap<>();
        initializeDefaultData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initialize default data for demonstration
     */
    private void initializeDefaultData() {
        // Initialize sample rooms
        rooms.put("LIB-301", new Room("LIB-301", "Library Quiet Study", 25));
        rooms.put("LAB-102", new Room("LAB-102", "Computer Lab", 50));
        rooms.put("CONF-501", new Room("CONF-501", "Conference Room", 100));

        // Initialize sample sensors
        sensors.put("TEMP-001", new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301"));
        sensors.put("CO2-001", new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LIB-301"));
        sensors.put("OCC-001", new Sensor("OCC-001", "Occupancy", "ACTIVE", 12.0, "LAB-102"));
        sensors.put("TEMP-002", new Sensor("TEMP-002", "Temperature", "MAINTENANCE", 21.0, "CONF-501"));

        // Add sensor IDs to rooms
        rooms.get("LIB-301").getSensorIds().addAll(Arrays.asList("TEMP-001", "CO2-001"));
        rooms.get("LAB-102").getSensorIds().add("OCC-001");
        rooms.get("CONF-501").getSensorIds().add("TEMP-002");

        // Initialize sample readings
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.put("CO2-001", new ArrayList<>());
        sensorReadings.put("OCC-001", new ArrayList<>());
        sensorReadings.put("TEMP-002", new ArrayList<>());

        SensorReading tempReading = new SensorReading(22.5);
        sensorReadings.get("TEMP-001").add(tempReading);
    }

    // ===== ROOM OPERATIONS =====

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room addRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public boolean deleteRoom(String roomId) {
        return rooms.remove(roomId) != null;
    }

    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    // ===== SENSOR OPERATIONS =====

    public Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    public Collection<Sensor> getAllSensors() {
        return sensors.values();
    }

    public Collection<Sensor> getSensorsByType(String type) {
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType().equalsIgnoreCase(type)) {
                filtered.add(sensor);
            }
        }
        return filtered;
    }

    public Sensor addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);

        // Add sensor ID to the room
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }

        return sensor;
    }

    public boolean deleteSensor(String sensorId) {
        Sensor sensor = sensors.remove(sensorId);
        if (sensor != null) {
            Room room = rooms.get(sensor.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(sensorId);
            }
            sensorReadings.remove(sensorId);
            return true;
        }
        return false;
    }

    public boolean sensorExists(String sensorId) {
        return sensors.containsKey(sensorId);
    }

    public void updateSensorCurrentValue(String sensorId, double value) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(value);
        }
    }

    // ===== SENSOR READING OPERATIONS =====

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return reading;
    }

    // ===== BUSINESS LOGIC OPERATIONS =====

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