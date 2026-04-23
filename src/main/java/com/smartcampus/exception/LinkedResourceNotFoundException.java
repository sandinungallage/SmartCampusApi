package com.smartcampus.exception;

/**
 * Custom exception thrown when a referenced resource does not exist in the system.
 * 
 * Example use case:
 * - A Sensor is linked to a Room, but the Room ID provided does not exist
 * 
 * This exception typically maps to HTTP 422 (Unprocessable Entity),
 * indicating that the request is syntactically correct but semantically invalid.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    // Type of the resource that was expected (e.g., "Room", "Sensor")
    private String resourceType;

    // Identifier of the missing resource
    private String resourceId;

    /**
     * Constructs a new LinkedResourceNotFoundException with details
     * about the missing referenced resource.
     *
     * @param resourceType the type of resource (e.g., Room, Sensor)
     * @param resourceId the ID of the resource that was not found
     */
    public LinkedResourceNotFoundException(String resourceType, String resourceId) {

        // Build a descriptive error message for debugging and API response clarity
        super("Referenced " + resourceType + " with ID '" + resourceId + "' does not exist.");

        // Store details for later retrieval (e.g., error handling, logging, API response)
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Returns the type of the missing resource.
     *
     * @return resource type (e.g., "Room", "Sensor")
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Returns the ID of the missing resource.
     *
     * @return resource ID that was referenced but not found
     */
    public String getResourceId() {
        return resourceId;
    }
}