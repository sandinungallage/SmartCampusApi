# Smart Campus Sensor & Room Management API

**Coursework Assignment:** Client-Server Architectures (5COSC022W)  
**Due Date:** 24th April 2026, 13:00  
**Technology Stack:** Apache Tomcat 9 | Java 8+ |  
**Framework Version:** JAX-RS 2.1 (javax.ws.rs) with Jersey 2.35  

---

## **Project Overview**

This RESTful API is designed to manage university campus infrastructure, focusing on room management and sensor deployment. It provides tools for campus facilities managers and automated systems to monitor and manage sensors (e.g., temperature, humidity, CO2, lighting) deployed across campus rooms.

### **Key Features**
- **Room Management:** Create, retrieve, update, and delete rooms with referential integrity.
- **Sensor Management:** Add, filter, and manage sensors with type-based search capabilities.
- **Historical Data:** Track time-series data for sensor readings.
- **Error Handling:** Comprehensive error handling with custom exception mappers.
- **Logging:** Request and response logging for better observability.
- **State Management:** Enforce sensor states (ACTIVE, MAINTENANCE, OFFLINE).

---

## **Setup & Build Instructions**

### **Prerequisites**
- Java 8 or higher
- Maven 3.6.0 or higher
- Git (for version control)
- Apache Tomcat 9 (for deployment)

### **1. Clone the Repository**
```bash
# Navigate to your projects directory
cd your_projects_folder

# Clone the repository
git clone https://github.com/yourusername/SmartCampusAPI.git
cd SmartCampusAPI
```

### **2. Build the Project**
```powershell
# Clean and build with Maven
mvn clean package

# Expected output: BUILD SUCCESS
```

### **3. Run the Server**
```powershell
# Locate the generated WAR file
target/SmartCampusAPI.war

# Copy the WAR file to Tomcat webapps directory
<tomcat-installation-folder>/webapps/

# Start Tomcat server
<tomcat-installation-folder>/bin/startup.sh   # Mac/Linux
<tomcat-installation-folder>\bin\startup.bat  # Windows

# Server will be available at:
http://localhost:8080/SmartCampusAPI/api/v1

```

### **4. Verify Server is Running**
```powershell
# Test discovery endpoint
curl -X GET "http://localhost:8080/api/v1"

# Should return 200 OK with API metadata
```

---

## **API Endpoints Overview**

### **Base URL**
```
http://localhost:8080/api/v1
```

### **1. Discovery Endpoint**

Retrieve API metadata and available resources.

```bash
# Get API metadata and available resources
curl -X GET "http://localhost:8080/api/v1" \
  -H "Accept: application/json"
```

---

### **2. Room Management Endpoints**

#### **List All Rooms**

Retrieve a list of all rooms.

```bash
curl -X GET "http://localhost:8080/api/v1/rooms" \
  -H "Accept: application/json"
```

**Response:** 
```json
{
  "count": 3,
  "items": [
    {"id": "ENG-201", "name": "Engineering Lab", "capacity": 30, "sensorIds": ["HUM-101", "LIGHT-101"]},
    {"id": "SCI-104", "name": "Science Hall", "capacity": 100, "sensorIds": ["TEMP-201"]},
    {"id": "ADM-501", "name": "Admin Meeting", "capacity": 20, "sensorIds": ["SOUND-101"]}
  ],
  "timestamp": 1714000000000
}
```

#### **Get Room Details**

Retrieve details of a specific room.

```bash
curl -X GET "http://localhost:8080/api/v1/rooms/ENG-201" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "room": {"id": "ENG-201", "name": "Engineering Lab", "capacity": 30, "sensorIds": ["HUM-101", "LIGHT-101"]},
  "metadata": {"activeSensors": 2, "totalSensors": 2}
}
```

#### **Create New Room**

Create a new room with the specified details.

```bash
curl -X POST "http://localhost:8080/api/v1/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "MED-302",
    "name": "Medical Science Lab",
    "capacity": 40
  }'
```

**Response:** HTTP 201 Created

#### **Delete Room**

Delete a room by its ID.

```bash
curl -X DELETE "http://localhost:8080/api/v1/rooms/MED-302" \
  -H "Accept: application/json"
```

**Error Response (Room has active sensors):** HTTP 409 Conflict
```json
{
  "code": 409,
  "error": "ConflictingState",
  "message": "Room 'ENG-201' contains one or more active sensors and cannot be deleted...",
  "affectedRoom": "ENG-201"
}
```

---

### **3. Sensor Management Endpoints**

#### **List All Sensors**

Retrieve a list of all sensors.

```bash
curl -X GET "http://localhost:8080/api/v1/sensors" \
  -H "Accept: application/json"
```

#### **Filter Sensors by Type**

Retrieve sensors filtered by their type.

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "count": 1,
  "appliedFilter": "type=Temperature",
  "items": [
    {"id": "TEMP-201", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.5, "roomId": "SCI-104"}
  ]
}
```

#### **Register New Sensor**
```bash
curl -X POST "http://localhost:8080/api/v1/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-102",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 420.0,
    "roomId": "ENG-201"
  }'
```

**Response:** HTTP 201 Created

**Error (Invalid Room Reference):** HTTP 422 Unprocessable Entity
```json
{
  "code": 422,
  "error": "InvalidReference",
  "message": "The referenced Room with identifier 'INVALID-ROOM' could not be found in the system.",
  "recordType": "Room",
  "recordIdentifier": "INVALID-ROOM"
}
```

#### **Get Sensor Details**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors/TEMP-201" \
  -H "Accept: application/json"
```

---

### **4. Sensor Reading Endpoints (Sub-Resources)**

#### **Get Sensor Reading History**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors/TEMP-201/readings" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "sensorId": "TEMP-201",
  "sensorType": "Temperature",
  "recordCount": 5,
  "readings": [
    {"id": "read-001", "timestamp": 1714000000000, "value": 21.5},
    {"id": "read-002", "timestamp": 1714001000000, "value": 21.7}
  ],
  "latestReading": {"value": 21.7, "timestamp": 1714001000000}
}
```

#### **Record New Sensor Reading**
```bash
curl -X POST "http://localhost:8080/api/v1/sensors/TEMP-201/readings" \
  -H "Content-Type: application/json" \
  -d '{"value": 22.1}'
```

**Response:** HTTP 201 Created

**Error (Sensor in MAINTENANCE):** HTTP 403 Forbidden
```json
{
  "code": 403,
  "error": "MissingPermission",
  "message": "Sensor 'SOUND-101' is in 'MAINTENANCE' state and cannot process incoming readings...",
  "sensorIdentifier": "SOUND-101",
  "sensorState": "MAINTENANCE",
  "permittedStates": ["ACTIVE"]
}
```

---

## **HTTP Status Codes**

| Code | Meaning | Scenario |
|------|---------|----------|
| 200 | OK | Successful GET/DELETE operations |
| 201 | Created | Resource successfully created. |
| 400 | Bad Request | Invalid input (missing required fields) |
| 403 | Forbidden | Sensor unavailable (not ACTIVE) |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Room has active sensors (cannot delete) |
| 422 | Unprocessable Entity | Invalid reference in JSON (room ID doesn't exist) |
| 500 | Internal Server Error | Unexpected server-side error |

---

## **Architecture & Design Decisions**

### **Part 1: Service Architecture & Setup**

**Q1: JAX-RS Resource Lifecycle & Thread Safety**

JAX-RS resources are **REQUEST-SCOPED** by default:
- **New instance per request:** Container creates fresh Resource instance for each HTTP request
- **No shared state between requests:** Eliminates thread-safety concerns for individual resource instances
- **Shared singleton DataStore:** All request-scoped resources access one centralized DataStore
- **Thread safety strategy:** DataStore uses ConcurrentHashMap instead of HashMap
  - ConcurrentHashMap provides atomic operations
  - Eliminates need for explicit synchronization
  - Multiple threads can read/write safely
- **Why this matters:** Without concurrent collections, two simultaneous requests could corrupt data

**Example scenario:**
```
Request 1: Reading room list
Request 2: Deleting sensor from room X
Without ConcurrentHashMap: Race condition could cause data inconsistency
With ConcurrentHashMap: Both operations are atomic, data stays consistent
```

---

### **Part 2: Room Management**

**Q2: HATEOAS & Hypermedia Benefits**

HATEOAS (Hypermedia As The Engine Of Application State) provides links within responses.

**Benefits vs Static Documentation:**
- ✅ **Self-documenting:** Clients discover resources dynamically
- ✅ **Reduced coupling:** No hardcoded URLs in client code
- ✅ **Evolution-friendly:** API can add endpoints without breaking clients
- ✅ **Navigation:**Clients traverse resource relationships seamlessly
- ❌ **Static docs:** External documentation must be updated separately

**Example:**
```json
{
  "resources": {
    "roomsCollection": "/api/v1/rooms",
    "sensorsCollection": "/api/v1/sensors"
  }
}
```

Client can read response and discover available endpoints programmatically.

---

**Q3: ID-only vs Full-Object Return**

**Scenario:** POST /rooms creates room 123

**Option A: Return ID only**
```json
{"id": "ROOM-123"}
```
- Pros: Minimal bandwidth
- Cons: Client must fetch full record separately (extra request)

**Option B: Return full object (CHOSEN)**
```json
{
  "id": "ROOM-123",
  "name": "Lab",
  "capacity": 30,
  "sensorIds": []
}
```
- Pros: Client has complete data immediately, follows RESTful conventions
- Cons: Slightly larger payload

**Decision:** Full object (saves round-trip, RESTful convention)

---

**Q4: DELETE Idempotency**

DELETE operations are **inherently idempotent:**

**First DELETE request:**
```
DELETE /rooms/ENG-201
Response: 200 OK (room deleted)
Server state: room doesn't exist
```

**Second identical DELETE request:**
```
DELETE /rooms/ENG-201
Response: 404 Not Found (already deleted)
Server state: room doesn't exist
```

**Why idempotent?** Final server state is identical: room is deleted. Multiple identical requests produce same outcome.

---

### **Part 3: Sensor Operations & Filtering**

**Q5: @Consumes(APPLICATION_JSON) Validation**

JAX-RS automatically validates Content-Type before method execution:

| Scenario | Content-Type | Result |
|----------|--------------|--------|
| Correct | application/json | ✅ Processed normally |
| Wrong format | text/plain | ❌ HTTP 415 Unsupported Media Type |
| Wrong format | application/xml | ❌ HTTP 415 Unsupported Media Type |

**Technical reason:** Servlet container inspects Content-Type header. If mismatched, JAX-RS rejects before your code runs.

---

**Q6: QueryParam vs PathParam for Filtering**

**@QueryParam Approach (CHOSEN):**
```
GET /sensors?type=Temperature&status=ACTIVE
```
- ✅ Flexible - multiple optional filters
- ✅ Standard REST convention
- ✅ Scalable - add more filters without changing structure

**@PathParam Approach:**
```
GET /sensors/type/Temperature/status/ACTIVE
```
- ❌ URI explosion - too many path segments
- ❌ Less flexible - hard to add optional filters
- ❌ Violates REST conventions for filtering

---

### **Part 4: Sub-Resources**

**Q7: Sub-Resource Locator Pattern Benefits**

Pattern: SensorResource returns SensorReadingResource for nested paths

**Architectural advantages:**
1. **Complexity Management:** Reading logic separated from sensor logic
2. **Testability:** Can unit test SensorReadingResource independently
3. **Maintainability:** Code organization follows domain structure
4. **Scalability:** Large APIs remain manageable
5. **Reusability:** Reading logic can be composed with other resources

**Compared to monolithic approach:**
```
❌ Bad: 200+ lines in one SensorResource class
✅ Good: 100 lines SensorResource + 80 lines SensorReadingResource
```

---

### **Part 5: Error Handling & Logging**

**Q8: HTTP 422 vs 404**

**409 Conflict:** Room has active sensors, cannot delete
- State prevents requested operation
- Retry after removing constraints

**422 Unprocessable Entity:** Room ID in JSON doesn't exist
- Client sent valid JSON with invalid reference
- More semantically accurate than 404

**404 Not Found:** Resource endpoint doesn't exist (e.g., GET /invalid)
- Entire path is wrong
- Different from invalid reference inside valid JSON

---

**Q9: Stack Trace Security Risks**

**What attackers learn from stack traces:**
- Internal class names and packages reveal architecture
- Library versions (jackson-2.15.2) identify vulnerable libs
- Algorithm details and business logic flow
- Database query structure
- File paths disclose deployment architecture

**Mitigation:**
```java
// ❌ Bad: Exposes everything
response.sendError(500, ex.toString());

// ✅ Good: Generic message, log internally
return Response.status(500).entity(genericMessage).build();
LOGGER.log(Level.SEVERE, "Full stack trace", ex);
```

---

**Q10: Filters vs Manual Logging**

**Why filters >> Logger.info() in every method:**

1. **DRY Principle:** Log format defined once, applied everywhere
2. **Consistency:** Uniform logging across all 100+ endpoints
3. **Maintenance:** Update logging without touching business logic
4. **Performance:** Automatic request duration measurement
5. **Traceability:** Correlate requests with responses
6. **Separation of concerns:** Business logic stays clean

**Example:**
```java
// ❌ Bad: Manual logging duplicated
@GET
public void getRoom() {
    logger.info("GET /rooms");  // Duplicated across 50 resources
    // business logic
}

// ✅ Good: Single filter for all
@Provider
public class LoggingFilter implements ContainerRequestFilter {
    // Automatically logs every request to every endpoint
}
```

---

## **Testing Instructions**

### **Testing Instructions**
```Manual Testing
Use the provided curl commands to manually test the API endpoints
```

### **Manual Endpoint Testing**

Use the provided curl commands to manually test the API endpoints.

### **Error Scenario Testing**

Test each error condition:
- 400: POST room without ID
- 404: GET room that doesn't exist
- 409: DELETE room with active sensors
- 422: POST sensor with non-existent room
- 403: POST reading to sensor in MAINTENANCE
- 500: Global error handler

---

## **Project Structure**

```
SmartCampusAPI/
├── src/main/java/com/smartcampus/
│   ├── SmartCampusApplication.java        
│   │   # JAX-RS application entry point (registers resources & config)
│   │
│   ├── config/
│   │   └── JacksonConfiguration.java      
│   │       # Configures JSON serialization/deserialization using Jackson
│   │
│   ├── exception/
│   │   ├── RoomNotEmptyException.java     
│   │   │   # Thrown when attempting to delete a room that still has sensors
│   │   ├── SensorUnavailableException.java
│   │   │   # Thrown when a sensor is not found or inactive
│   │   └── GlobalExceptionMapper.java     
│   │       # Converts exceptions into proper HTTP responses
│   │
│   ├── filter/
│   │   └── LoggingFilter.java             
│   │       # Logs all incoming requests and outgoing responses (cross-cutting concern)
│   │
│   ├── model/
│   │   ├── Room.java                      
│   │   │   # Represents a physical room in the campus
│   │   ├── Sensor.java                    
│   │   │   # Represents a sensor device (temperature, humidity, etc.)
│   │   └── SensorReading.java             
│   │       # Represents sensor data readings (timestamp, value)
│   │
│   ├── repository/
│   │   └── DataStore.java                 
│   │       # In-memory data storage (simulates a database using collections)
│   │
│   └── resource/
│       ├── RoomResource.java              
│       │   # REST endpoints for room management (/rooms)
│       ├── SensorResource.java            
│       │   # REST endpoints for sensor management (/sensors)
│       └── SensorReadingResource.java     
│           # REST endpoints for sensor readings (/readings)
│
├── src/main/webapp/WEB-INF/web.xml        
│   # Servlet configuration for Apache Tomcat 9 (Jersey setup using javax.ws.rs)
│
├── pom.xml                                
│   # Maven configuration (Jersey 2.x, javax.ws.rs, Tomcat 9 compatible dependencies)
│
└── README.md                              
    # Project documentation (setup, API usage, endpoints)
```

---

## **Technologies Used**

- **Framework:** JAX-RS (Jakarta REST API)
- **Implementation:** Jersey 2.35
- **JSON Serialization:** Jackson 2.12.5
- **Dependency Injection:** HK2
- **Server:** Apache Tomcat 9
- **Build Tool:** Maven 3.6.3
- **Language:** Java 8+
- **Data Storage:** In-memory ConcurrentHashMap (no database)

---

## **Compliance Notes**

 **Coursework Requirements Met:**
- JAX-RS only (no Spring Boot)
- No database (HashMap/ArrayList used)
- Public GitHub repository
- README.md with instructions
- Sample curl commands provided
- Request/Response logging implemented
- All error codes properly handled (409, 422, 403, 500)
- Custom exception mappers implemented
- Sub-resource locator pattern using SensorReadingResource
- Thread-safe data structures (ConcurrentHashMap)
- HATEOAS principles applied

---

## **Support & Questions**

For any questions or issues, please refer to the documentation

---

**Last Updated:** April 2026  
**API Version:** 1.0.0  
**Status:** Coursework Prototype
