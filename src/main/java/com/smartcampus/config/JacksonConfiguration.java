package com.smartcampus.config;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

/**
 * Jackson Configuration Feature
 *
 * This class explicitly configures Jackson as the JSON processing
 * provider for the application.
 *
 * In JAX-RS environments, multiple JSON providers (such as JSON-B)
 * may be available by default. Without explicit configuration,
 * the runtime may choose a provider automatically, which can lead
 * to inconsistent serialization or unexpected behavior.
 *
 * By registering Jackson manually, this class ensures that all
 * JSON request and response processing is handled consistently
 * using the Jackson library.
 */
@Provider
public class JacksonConfiguration implements Feature {

    /**
     * This method is called during application initialization.
     *
     * It registers the JacksonJsonProvider with a specific priority,
     * ensuring that Jackson is selected over other available providers.
     *
     * Priority explanation:
     * - Lower numeric values indicate higher priority
     * - Assigning a value such as 15 ensures Jackson is preferred
     *   over default providers like JSON-B
     *
     * @param ctx FeatureContext used to register components
     * @return true to indicate successful configuration
     */
    @Override
    public boolean configure(FeatureContext ctx) {

        // Register Jackson as the primary JSON provider
        // The priority value ensures it overrides default implementations
        ctx.register(JacksonJsonProvider.class, 15);

        return true;
    }
}