package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.repository.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * REST resource for managing Room entities.
 *
 * Base path: /api/v1/rooms
 *
 * This class provides endpoints for creating, retrieving, listing,
 * and deleting rooms in the Smart Campus system.
 *
 * Responsibility:
 * - Acts as the API controller layer
 * - Delegates data operations to the DataStore
 * - Enforces validation and business rules at the API boundary
 *
 * The resource follows REST principles and maintains referential integrity
 * between rooms and associated sensors.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // Shared in-memory data store (singleton)
    private final DataStore store = DataStore.getInstance();

    /**
     * Retrieves all rooms in the system.
     *
     * Endpoint: GET /api/v1/rooms
     *
     * Response includes:
     * - Total number of rooms
     * - List of all room entities
     * - Server-generated timestamp
     */
    @GET
    public Response listAllRooms() {

        // Fetch all stored rooms
        Collection<Room> roomCollection = store.fetchAllRooms();

        // Construct response payload
        Map<String, Object> body = new HashMap<>();
        body.put("count", roomCollection.size());
        body.put("items", roomCollection);
        body.put("timestamp", System.currentTimeMillis());

        return Response.ok(body).build();
    }

    /**
     * Retrieves detailed information for a specific room.
     *
     * Endpoint: GET /api/v1/rooms/{roomId}
     *
     * Includes:
     * - Room details
     * - Computed sensor statistics (total and active sensors)
     *
     * Returns 404 if the room does not exist.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomDetails(@PathParam("roomId") String roomId) {

        // Fetch room by identifier
        Room room = store.retrieveRoom(roomId);

        // Handle missing resource
        if (room == null) {
            return buildErrorResponse(
                    404,
                    "NotFound",
                    "Room '" + roomId + "' does not exist in the system."
            );
        }

        // Compute sensor-related metadata
        int activeSensorCount = store.countActiveSensorsInRoom(roomId);

        // Build response payload
        Map<String, Object> body = new HashMap<>();
        body.put("room", room);

        // Additional computed metadata
        body.put("metadata", new HashMap<String, Object>() {{
            put("activeSensors", activeSensorCount);
            put("totalSensors", room.getSensorIds().size());
        }});

        return Response.ok(body).build();
    }

    /**
     * Creates a new room.
     *
     * Endpoint: POST /api/v1/rooms
     *
     * Validation rules:
     * - Room ID must not be null or empty
     * - Room ID must be unique
     *
     * Returns:
     * - 201 Created on success
     * - 400 Bad Request for invalid input
     * - 409 Conflict if resource already exists
     */
    @POST
    public Response createRoom(Room incomingRoom) {

        // Validate room ID
        if (incomingRoom.getId() == null || incomingRoom.getId().trim().isEmpty()) {
            return buildErrorResponse(
                    400,
                    "ValidationError",
                    "Room ID is mandatory and cannot be blank."
            );
        }

        // Prevent duplicate room creation
        if (store.roomExists(incomingRoom.getId())) {
            return buildErrorResponse(
                    409,
                    "DuplicateResource",
                    "Room '" + incomingRoom.getId() + "' already exists."
            );
        }

        // Persist new room
        Room created = store.saveRoom(incomingRoom);

        // Build success response
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Room provisioned successfully.");
        body.put("createdRoom", created);

        return Response
                .status(Response.Status.CREATED)
                .entity(body)
                .build();
    }

    /**
     * Deletes a room from the system.
     *
     * Endpoint: DELETE /api/v1/rooms/{roomId}
     *
     * Business rules:
     * - A room cannot be deleted if it contains active sensors
     * - Ensures referential integrity between rooms and sensors
     *
     * Behavior:
     * - 200 OK if deletion is successful
     * - 404 Not Found if room does not exist
     * - 409 Conflict (via exception) if room contains active sensors
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        // Retrieve room
        Room room = store.retrieveRoom(roomId);

        // Handle missing room
        if (room == null) {
            return buildErrorResponse(
                    404,
                    "NotFound",
                    "Room '" + roomId + "' not found. Nothing to delete."
            );
        }

        // Prevent deletion if active sensors exist
        if (store.roomHasActiveSensors(roomId)) {
            throw new RoomNotEmptyException(roomId);
        }

        // Remove room
        store.removeRoom(roomId);

        // Build response
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Room successfully decommissioned.");
        body.put("deletedRoom", roomId);
        body.put("timestamp", System.currentTimeMillis());

        return Response.ok(body).build();
    }

    /**
     * Builds a standardized error response structure.
     *
     * Ensures consistency across all endpoints for error handling.
     *
     * @param statusCode HTTP status code
     * @param errorType logical error identifier
     * @param detail human-readable error message
     * @return structured HTTP response
     */
    private Response buildErrorResponse(int statusCode, String errorType, String detail) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", statusCode);
        errorBody.put("error", errorType);
        errorBody.put("detail", detail);
        errorBody.put("timestamp", System.currentTimeMillis());

        return Response
                .status(statusCode)
                .entity(errorBody)
                .type("application/json")
                .build();
    }
}