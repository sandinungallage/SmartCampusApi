package com.smartcampus.exception;

/**
 * Exception thrown when a referenced resource cannot be found.
 *
 * This typically occurs when a request contains a valid structure,
 * but refers to another entity (such as a foreign key) that does not exist
 * in the system.
 *
 * For example:
 * - Assigning a sensor to a room that does not exist
 * - Referencing an invalid resource ID in a request payload
 *
 * This situation is mapped to HTTP 422 (Unprocessable Entity),
 * as the request is syntactically correct but semantically invalid.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    // Type of the referenced resource (e.g., "Room", "Sensor")
    private final String refType;

    // Identifier of the missing resource
    private final String refId;

    /**
     * Constructs the exception with details about the missing reference.
     *
     * @param refType type of resource being referenced
     * @param refId   identifier of the resource that could not be found
     */
    public LinkedResourceNotFoundException(String refType, String refId) {

        // Build a descriptive error message for debugging and client responses
        super(String.format(
                "The referenced %s with identifier '%s' could not be found in the system.",
                refType, refId));

        this.refType = refType;
        this.refId = refId;
    }

    /**
     * Returns the type of the missing referenced resource.
     */
    public String getRefType() {
        return refType;
    }

    /**
     * Returns the identifier of the missing referenced resource.
     */
    public String getRefId() {
        return refId;
    }
}