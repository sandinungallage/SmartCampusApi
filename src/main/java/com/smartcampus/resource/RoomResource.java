package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Room Resource
 * Manages /api/v1/rooms endpoint
 * Implements CRUD operations with safety constraints
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore dataStore = DataStore.getInstance();

    /**
     * GET /api/v1/rooms
     * Returns all rooms
     */
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = dataStore.getAllRooms();

        Map<String, Object> response = new HashMap<>();
        response.put("total", rooms.size());
        response.put("data", rooms);

        return Response.ok(response).build();
    }

    /**
     * GET /api/v1/rooms/{roomId}
     * Returns specific room with active sensor count
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);

        if (room == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Room with ID '" + roomId + "' not found.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", room);
        response.put("activeSensorCount", dataStore.getActiveSensorCount(roomId));

        return Response.ok(response).build();
    }

    /**
     * POST /api/v1/rooms
     * Creates new room
     */
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Room ID is required.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }

        if (dataStore.getRoom(room.getId()) != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 409);
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", "Room with ID '" + room.getId() + "' already exists.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(errorResponse)
                    .build();
        }

        Room createdRoom = dataStore.addRoom(room);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room created successfully.");
        response.put("data", createdRoom);

        return Response
                .status(Response.Status.CREATED)
                .location(URI.create("/api/v1/rooms/" + createdRoom.getId()))
                .entity(response)
                .build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Deletes room with constraint: cannot delete if room has active sensors
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);

        if (room == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Room with ID '" + roomId + "' not found.");
            errorResponse.put("timestamp", System.currentTimeMillis());

            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        }

        // CONSTRAINT: Cannot delete room with active sensors
        if (dataStore.hasActiveSensors(roomId)) {
            throw new RoomNotEmptyException(roomId);
        }

        dataStore.deleteRoom(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room deleted successfully.");
        response.put("roomId", roomId);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }
}