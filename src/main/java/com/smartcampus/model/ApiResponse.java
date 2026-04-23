package com.smartcampus.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard API Response Wrapper.
 * 
 * This class provides a consistent response structure for all API endpoints,
 * ensuring uniformity in success and error responses across the system.
 * 
 * Typical structure:
 * - statusCode: HTTP-like status code representing the result
 * - message: human-readable description of the response
 * - data: flexible map containing additional response payload
 * 
 * Benefits:
 * - Consistent API design
 * - Easier client-side parsing
 * - Extensible response format without breaking changes
 */
public class ApiResponse {

    // HTTP-like status code (e.g., 200, 400, 404, 500)
    private int statusCode;

    // Human-readable message describing the response
    private String message;

    // Flexible container for response payload data
    private Map<String, Object> data;

    /**
     * Constructor for responses without additional data.
     *
     * @param statusCode HTTP-like status code
     * @param message response message
     */
    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;

        // Initialize empty data map to avoid null checks
        this.data = new HashMap<>();
    }

    /**
     * Constructor for responses with data payload.
     *
     * @param statusCode HTTP-like status code
     * @param message response message
     * @param data additional response data
     */
    public ApiResponse(int statusCode, String message, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    /**
     * Returns the status code of the response.
     *
     * @return status code (e.g., 200, 404)
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code of the response.
     *
     * @param statusCode HTTP-like status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Returns the response message.
     *
     * @return human-readable message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message response description
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the response data payload.
     *
     * @return map containing response data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Sets the response data payload.
     *
     * @param data map containing response data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}