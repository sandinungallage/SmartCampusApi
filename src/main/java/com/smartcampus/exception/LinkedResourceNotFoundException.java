package com.smartcampus.exception;

/**
 * Exception thrown when a referenced resource (e.g., Room referenced by Sensor)
 * doesn't exist
 * HTTP 422 Unprocessable Entity
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    private String resourceType;
    private String resourceId;

    public LinkedResourceNotFoundException(String resourceType, String resourceId) {
        super("Referenced " + resourceType + " with ID '" + resourceId + "' does not exist.");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}