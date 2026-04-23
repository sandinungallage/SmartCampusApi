package com.smartcampus;

import com.smartcampus.config.JacksonConfiguration;
import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.mapper.*;
import com.smartcampus.resource.*;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Smart Campus Application - JAX-RS Entry Point
 *
 * This class serves as the central configuration point for the REST API.
 * It is responsible for registering all resources, providers, and filters
 * used by the application.
 *
 * The @ApplicationPath("/api/v1") annotation defines the base URI for
 * all exposed endpoints, enabling versioning of the API.
 *
 * Lifecycle behavior:
 * - Resource classes in JAX-RS are request-scoped by default
 * - A new instance is created for each incoming HTTP request
 * - This avoids shared mutable state and simplifies concurrency handling
 *
 * Shared data is managed separately using a singleton DataStore,
 * which ensures consistency across requests.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    /**
     * Registers all components used by the JAX-RS runtime.
     *
     * These include:
     * - Resource classes (API endpoints)
     * - Configuration classes (e.g., JSON handling)
     * - Exception mappers (custom error handling)
     * - Filters (logging and request processing)
     *
     * @return a set of classes to be managed by the JAX-RS framework
     */
    @Override
    public Set<Class<?>> getClasses() {

        // Container for all registered components
        Set<Class<?>> components = new HashSet<>();

        // JSON configuration for request/response serialization
        // Ensures consistent mapping between Java objects and JSON
        components.add(JacksonConfiguration.class);

        // Resource classes that define API endpoints
        components.add(DiscoveryResource.class);
        components.add(RoomResource.class);
        components.add(SensorResource.class);

        // Exception mappers convert Java exceptions into structured HTTP responses
        // This ensures consistent and meaningful error handling across the API
        components.add(RoomNotEmptyMapper.class);
        components.add(LinkedResourceMapper.class);
        components.add(SensorUnavailableMapper.class);
        components.add(GlobalExceptionMapper.class);

        // Filters provide cross-cutting functionality such as logging
        // LoggingFilter can be used to track incoming requests and outgoing responses
        components.add(LoggingFilter.class);

        return components;
    }
}