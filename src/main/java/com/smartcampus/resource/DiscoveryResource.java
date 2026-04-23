package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Discovery Resource
 * Entry point for API at GET /api/v1
 * Provides hypermedia links and API metadata (HATEOAS principle)
 *
 * This makes the API self-documenting, as clients can dynamically discover
 * available actions without relying on external documentation.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getApiDiscovery() {
        Map<String, Object> discovery = new HashMap<>();

        discovery.put("apiName", "Smart Campus Sensor & Room Management API");
        discovery.put("version", "1.0.0");
        discovery.put("description", "RESTful API for managing campus rooms and sensors");

        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Smart Campus Team");
        contact.put("email", "smartcampus@university.edu");
        discovery.put("contact", contact);

        // HATEOAS: Hypermedia links for resource discovery
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("sensorReadings", "/api/v1/sensors/{sensorId}/readings");
        discovery.put("resources", resources);

        Map<String, String> documentation = new HashMap<>();
        documentation.put("statusCodes",
                "200: Success, 201: Created, 400: Bad Request, 403: Forbidden, 404: Not Found, 409: Conflict, 422: Unprocessable Entity, 500: Internal Server Error");
        documentation.put("hateoas", "All resources include hypermedia links for navigation and related operations");
        discovery.put("documentation", documentation);

        return Response
                .ok(discovery)
                .header("X-API-Version", "1.0.0")
                .build();
    }
}