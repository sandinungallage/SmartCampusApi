package com.smartcampus.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic API response wrapper used across all endpoints.
 *
 * This class ensures that every API response follows a consistent structure,
 * making it easier for clients to parse and handle responses uniformly.
 *
 * Standard structure includes:
 * - statusCode: HTTP-like status indicator for application-level responses
 * - message: human-readable description of the result
 * - data: flexible key-value container for response payload
 *
 * Benefits:
 * - Consistent response format across the entire system
 * - Easier frontend integration and error handling
 * - Extensible structure without modifying existing endpoints
 */
public class ApiResponse {

    // Application-level status code (not always HTTP status)
    private int statusCode;

    // Human-readable message describing the result
    private String message;

    // Flexible container for response payload data
    private Map<String, Object> data;

    /**
     * Default constructor initializes a successful response structure.
     * Data map is initialized to avoid null checks elsewhere.
     */
    public ApiResponse() {
        this.data = new HashMap<>();
        this.statusCode = 200;
    }

    /**
     * Constructor for responses without data payload.
     *
     * @param statusCode application-level status code
     * @param message response message
     */
    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new HashMap<>();
    }

    /**
     * Constructor for full response including data payload.
     *
     * @param statusCode application-level status code
     * @param message response message
     * @param data response payload
     */
    public ApiResponse(int statusCode, String message, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.message = message;

        // Ensure data map is never null to avoid NullPointerException
        this.data = data != null ? data : new HashMap<>();
    }

    // Getter and setter methods for status code
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    // Getter and setter for response message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and setter for response data
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Adds a single key-value pair to the response data.
     *
     * @param key data field name
     * @param value data value
     */
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
}