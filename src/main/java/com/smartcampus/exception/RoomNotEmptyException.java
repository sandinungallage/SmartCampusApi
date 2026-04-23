package com.smartcampus.exception;

/**
 * Exception thrown when attempting to delete a room that still
 * contains active sensors.
 *
 * This enforces referential integrity within the system by preventing
 * the removal of a parent resource (room) while dependent resources
 * (sensors) are still associated with it.
 *
 * This situation is mapped to HTTP 409 (Conflict), indicating that
 * the request cannot be completed due to the current state of the resource.
 */
public class RoomNotEmptyException extends RuntimeException {

    // Identifier of the room that cannot be deleted
    private final String id;

    /**
     * Constructs the exception with the affected room ID.
     *
     * @param id the identifier of the room that contains active sensors
     */
    public RoomNotEmptyException(String id) {

        // Provides a clear and descriptive error message for clients and debugging
        super(String.format(
            "Room '%s' contains one or more active sensors and cannot be deleted. Remove all sensors before attempting deletion.",
            id));

        this.id = id;
    }

    /**
     * Returns the identifier of the room that caused the exception.
     */
    public String getId() {
        return id;
    }
}