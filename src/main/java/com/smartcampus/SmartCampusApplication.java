package com.smartcampus;

import com.smartcampus.config.JacksonConfiguration;
import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.mapper.*;
import com.smartcampus.resource.*;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Smart Campus JAX-RS Application Configuration.
 * 
 * This class serves as the entry point for the REST API application.
 * It defines the base API path and registers all components required
 * for the JAX-RS runtime.
 * 
 * Responsibilities:
 * - Defines API base path (/api/v1)
 * - Registers REST resources (controllers)
 * - Registers exception mappers (global error handling)
 * - Registers filters (cross-cutting concerns like logging)
 * - Enables JSON configuration via Jackson
 * 
 * This ensures a centralized and explicit configuration of the entire API.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    /**
     * Registers all JAX-RS components used by the application.
     *
     * @return a set of classes to be managed by the JAX-RS runtime
     */
    @Override
    public Set<Class<?>> getClasses() {

        // Set used to store all registered application components
        Set<Class<?>> classes = new HashSet<>();

        // ===== JACKSON CONFIGURATION =====
        // Enables JSON serialization/deserialization support across the API
        classes.add(JacksonConfiguration.class);

        // ===== REST RESOURCES =====
        // Core API endpoints exposed to clients
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        classes.add(TestResource.class);

        // ===== EXCEPTION MAPPERS =====
        // Centralized error handling for consistent API responses
        classes.add(JsonParseExceptionMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(RoomNotEmptyMapper.class);
        classes.add(LinkedResourceMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(GlobalExceptionMapper.class);

        // ===== FILTERS (Cross-Cutting Concerns) =====
        // Applied to all requests/responses for logging and observability
        classes.add(LoggingFilter.class);

        return classes;
    }
}