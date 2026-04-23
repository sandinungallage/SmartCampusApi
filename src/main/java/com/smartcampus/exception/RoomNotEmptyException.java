package com.smartcampus.exception;

/**
 * Custom exception thrown when an attempt is made to delete a room
 * that still has active sensors assigned to it.
 * 
 * This prevents accidental data loss and enforces referential integrity
 * within the Smart Campus system.
 * 
 * Typically mapped to HTTP 409 Conflict or 422 Unprocessable Entity,
 * depending on API design decisions.
 */
public class RoomNotEmptyException extends RuntimeException {

    // Identifier of the room that cannot be deleted
    private String roomId;

    /**
     * Constructs a new RoomNotEmptyException with the specified room ID.
     *
     * @param roomId the ID of the room that contains active sensors
     */
    public RoomNotEmptyException(String roomId) {

        // Detailed error message explaining why deletion is not allowed
        super("Room '" + roomId + "' cannot be deleted because it still has active sensors assigned to it.");

        // Store room ID for later retrieval in error handling or API responses
        this.roomId = roomId;
    }

    /**
     * Returns the ID of the room that caused the exception.
     *
     * @return room ID that cannot be deleted
     */
    public String getRoomId() {
        return roomId;
    }
}