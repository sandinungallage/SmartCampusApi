package com.smartcampus.exception;
/**
 * Exception thrown when attempting to delete a room that has active sensors
 */
public class RoomNotEmptyException extends RuntimeException {
    private String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Room '" + roomId + "' cannot be deleted because it still has active sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}