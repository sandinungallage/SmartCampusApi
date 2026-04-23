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
 * Smart Campus JAX-RS Application Configuration
 *
 * Entry point for REST API
 * Registers all resources, exception mappers, filters
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // ===== JACKSON CONFIGURATION (PRIORITY) =====
        classes.add(JacksonConfiguration.class);

        // ===== RESOURCES =====
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        classes.add(TestResource.class);

        // ===== EXCEPTION MAPPERS =====
        classes.add(JsonParseExceptionMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(RoomNotEmptyMapper.class);
        classes.add(LinkedResourceMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(GlobalExceptionMapper.class);

        // ===== FILTERS (Cross-cutting Concerns) =====
        classes.add(LoggingFilter.class);

        return classes;
    }
}