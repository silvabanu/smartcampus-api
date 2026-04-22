# Smart Campus Sensor & Room Management API

JAX-RS REST API for managing campus rooms, sensors, and sensor readings using in-memory collections (`ConcurrentHashMap`, `ArrayList`-style lists).  
Base path: `/api/v1`

## API Design Overview

- **Versioned entry point:** `@ApplicationPath("/api/v1")`
- **Primary collections:**
  - `GET /api/v1` (discovery)
  - `GET/POST /api/v1/rooms`
  - `GET/DELETE /api/v1/rooms/{roomId}`
  - `GET/POST /api/v1/sensors`
  - `GET/POST /api/v1/sensors/{sensorId}/readings` (sub-resource locator)
- **Business constraints:**
  - Room deletion blocked when room still has sensors (`409 Conflict`)
  - Sensor creation requires existing room (`422 Unprocessable Entity`)
  - Posting readings blocked if sensor status is `MAINTENANCE` (`403 Forbidden`)
  - Posting a reading updates parent sensor `currentValue`
- **Observability and safety:**
  - Request/response logging via JAX-RS filters
  - Custom exception mappers + global catch-all mapper to avoid leaking stack traces

## Build and Run

This project is packaged as a WAR and intended for a Jakarta EE 8-compatible servlet container/application server.

1. Build:
   ```bash
   mvn clean package
   ```
2. Deploy `target/smartcampus-api-1.0-SNAPSHOT.war` to your server (for example, Payara/GlassFish/TomEE).
3. Use the API at:
   ```text
   http://localhost:8080/smartcampus-api/api/v1
   ```

## Sample curl Commands

1. Discovery endpoint:
   ```bash
   curl -s http://localhost:8080/smartcampus-api/api/v1
   ```
2. Create a room:
   ```bash
   curl -s -X POST http://localhost:8080/smartcampus-api/api/v1/rooms \
     -H "Content-Type: application/json" \
     -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":120}'
   ```
3. List rooms:
   ```bash
   curl -s http://localhost:8080/smartcampus-api/api/v1/rooms
   ```
4. Register a sensor linked to a room:
   ```bash
   curl -s -X POST http://localhost:8080/smartcampus-api/api/v1/sensors \
     -H "Content-Type: application/json" \
     -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":415.2,"roomId":"LIB-301"}'
   ```
5. Filter sensors by type:
   ```bash
   curl -s "http://localhost:8080/smartcampus-api/api/v1/sensors?type=CO2"
   ```
6. Add a reading to a sensor:
   ```bash
   curl -s -X POST http://localhost:8080/smartcampus-api/api/v1/sensors/CO2-001/readings \
     -H "Content-Type: application/json" \
     -d '{"value":430.8}'
   ```
7. Get reading history:
   ```bash
   curl -s http://localhost:8080/smartcampus-api/api/v1/sensors/CO2-001/readings
   ```
8. Attempt to delete room with sensors (expected 409):
   ```bash
   curl -s -X DELETE http://localhost:8080/smartcampus-api/api/v1/rooms/LIB-301
   ```

## Report Answers (Conceptual Questions)

### Part 1

**Q: JAX-RS resource lifecycle (request-scoped vs singleton) and effect on in-memory structures?**  
By default, JAX-RS resource classes are **request-scoped** (a new instance per request) unless explicitly configured otherwise. Because resource instances are short-lived, mutable shared state must not be stored inside resource instance fields. Persistent in-memory state should be held in shared application-level structures (e.g., static singleton store) with thread-safe collections/synchronization to prevent race conditions and lost updates under concurrent requests.

**Q: Why is hypermedia (HATEOAS) important?**  
Hypermedia provides discoverable links in responses so clients can navigate the API dynamically instead of hardcoding endpoint knowledge. This reduces client coupling to URL layouts, improves evolvability, and makes integrations safer when routes or capabilities expand.

### Part 2

**Q: Return only room IDs vs full room objects?**  
Returning only IDs minimizes payload size and bandwidth but shifts work to clients, which then need additional calls to fetch room details. Returning full objects costs more bandwidth but reduces round trips and simplifies client rendering logic. Choice depends on payload size, network cost, and client interaction patterns.

**Q: Is DELETE idempotent here?**  
Yes. First successful deletion removes the room. Repeating the same DELETE does not recreate or further mutate the state; it simply returns not-found for an already removed resource. The resource state after one or many identical DELETE calls is the same.

### Part 3

**Q: What happens when `@Consumes(MediaType.APPLICATION_JSON)` is used and client sends another format?**  
JAX-RS performs content negotiation against `Content-Type`. If no compatible message body reader exists for the posted media type, the runtime rejects the request (typically `415 Unsupported Media Type`) before reaching method logic.

**Q: Why use query parameter for filtering (`/sensors?type=CO2`) instead of path (`/sensors/type/CO2`)?**  
Filtering/searching is a modifier on a collection query, not a distinct resource identity. Query parameters express optional criteria naturally, compose better with additional filters (`?type=CO2&status=ACTIVE`), and keep URI design consistent for collection retrieval.

### Part 4

**Q: Benefits of sub-resource locator pattern?**  
Sub-resource locators split nested concerns into focused classes, improving readability, maintainability, and testability. They keep parent resources small, localize context-specific logic (e.g., readings under one sensor), and avoid a monolithic controller with deeply nested route handling.

### Part 5

**Q: Why is 422 often better than 404 for missing linked resource in payload?**  
The request target endpoint exists and payload structure is syntactically valid, but semantic validation fails because a referenced dependency (roomId) is invalid. `422 Unprocessable Entity` communicates semantic payload failure more precisely than `404 Not Found`, which usually refers to the request URI resource not existing.

**Q: Cybersecurity risks of exposing stack traces?**  
Stack traces leak implementation details such as package/class names, library versions, file paths, SQL/JPA hints, and internal control flow. Attackers can use this intelligence for targeted exploitation, dependency CVE matching, endpoint probing, and reconnaissance of weak components.

**Q: Why use JAX-RS filters for logging instead of manual logging in each resource?**  
Filters implement cross-cutting concerns centrally and consistently. They reduce duplication, prevent missed logging in new endpoints, keep business methods clean, and enforce uniform request/response observability at a single integration point.

## Video Demonstration Checklist

- Show discovery endpoint and core CRUD flows.
- Demonstrate sensor filtering by query parameter.
- Demonstrate nested reading operations and `currentValue` update.
- Demonstrate each required error scenario (409, 422, 403, plus generic safety behavior).
- Keep camera/microphone active and narration clear.
