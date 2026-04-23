package com.smartcampus.config;

// JSON provider used for serializing and deserializing Java objects to/from JSON
// This import is from Jackson's JAX-RS integration module
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * Configuration class for enabling Jackson JSON support in the JAX-RS application.
 * 
 * This class registers the JacksonJsonProvider, which allows automatic
 * conversion between Java objects and JSON in REST API requests and responses.
 * 
 * The @Provider annotation ensures that this feature is discovered automatically
 * during JAX-RS application initialization.
 */
@Provider
public class JacksonConfiguration implements Feature {

    /**
     * Configures the JAX-RS runtime by registering custom providers.
     * 
     * In this case, it registers JacksonJsonProvider with a priority of 20,
     * which enables JSON processing for all REST endpoints in the application.
     *
     * @param context the FeatureContext used to register components
     * @return true to indicate that the feature was successfully configured
     */
    @Override
    public boolean configure(FeatureContext context) {

        // Register Jackson JSON provider with priority 20
        // This ensures JSON serialization/deserialization is handled by Jackson
        context.register(JacksonJsonProvider.class, 20);

        // Return true to activate this feature in the JAX-RS runtime
        return true;
    }
}