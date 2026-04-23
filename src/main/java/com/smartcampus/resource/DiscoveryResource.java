package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Root discovery endpoint for the Smart Campus API.
 *
 * This resource acts as the entry point of the API, allowing clients
 * to understand available services and navigate the system dynamically.
 *
 * It follows the HATEOAS (Hypermedia as the Engine of Application State) principle,
 * where responses include navigable links instead of requiring clients
 * to hardcode endpoint URLs.
 *
 * Benefits:
 * - Improves API discoverability
 * - Reduces tight coupling between client and server
 * - Supports future extensibility without breaking clients
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    /**
     * Provides API metadata and navigation links.
     *
     * This endpoint returns general information about the system,
     * including available resources, support details, and API contract rules.
     *
     * It serves as a guide for clients to understand how to interact
     * with the API.
     *
     * @return HTTP response containing API metadata and navigation structure
     */
    @GET
    public Response discoverApi() {

        // Root response container
        Map<String, Object> metadata = new HashMap<>();

        // General API information
        metadata.put("serviceName", "Smart Campus Infrastructure API");
        metadata.put("apiVersion", "1.0.0");
        metadata.put("buildDate", "2025-04-01");
        metadata.put("description", "Manages campus facilities, rooms and sensor infrastructure");

        // Support contact information
        Map<String, String> contact = new HashMap<>();
        contact.put("department", "Facilities Management");
        contact.put("supportEmail", "facilities@university.ac.uk");
        metadata.put("supportContact", contact);

        // API navigation links (entry points to core resources)
        Map<String, String> navigation = new HashMap<>();
        navigation.put("roomsCollection", "/api/v1/rooms");
        navigation.put("sensorsCollection", "/api/v1/sensors");

        // Template endpoint requiring sensor ID substitution
        navigation.put("sensorReadingsTemplate", "/api/v1/sensors/{sensorId}/readings");

        metadata.put("navigationLinks", navigation);

        // API contract definition and usage rules
        Map<String, Object> contract = new HashMap<>();

        contract.put(
            "standardStatusCodes",
            "200 OK | 201 Created | 204 No Content | 400 Bad Request | 403 Forbidden | 404 Not Found | 409 Conflict | 422 Unprocessable Entity | 500 Internal Server Error"
        );

        contract.put("requestContentType", "application/json");
        contract.put("responseContentType", "application/json");

        // Indicates hypermedia-driven API design
        contract.put("hateoas", "Responses include links for navigating related resources");

        metadata.put("contractDetails", contract);

        // Build HTTP response with metadata and headers
        return Response
                .ok(metadata)
                .header("X-API-Version", "1.0.0") // API version identifier
                .header("X-API-Timestamp", System.currentTimeMillis()) // server response timestamp
                .build();
    }
}