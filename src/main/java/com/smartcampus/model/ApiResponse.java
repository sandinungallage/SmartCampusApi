package com.smartcampus.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard API Response Wrapper
 * Provides consistent response structure across API
 */
public class ApiResponse {
    private int statusCode;
    private String message;
    private Map<String, Object> data;

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = new HashMap<>();
    }

    public ApiResponse(int statusCode, String message, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}