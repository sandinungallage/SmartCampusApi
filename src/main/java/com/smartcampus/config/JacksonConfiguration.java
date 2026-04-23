package com.smartcampus.config;

// Notice this import changed from jakarta.rs.json to jaxrs.json
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class JacksonConfiguration implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        context.register(JacksonJsonProvider.class, 20);
        return true;
    }
}