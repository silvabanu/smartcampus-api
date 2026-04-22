/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.resources;

/**
 *
 * @author banuka
 */
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import uk.ac.westminster.smartcampus.api.models.Room;
import uk.ac.westminster.smartcampus.api.models.Sensor;
import uk.ac.westminster.smartcampus.api.models.SensorReading;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private final RoomResource roomResource = new RoomResource();
    private final SensorResource sensorResource = new SensorResource();

    @GET
    public Response discover(@Context UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", base + "/rooms");
        resources.put("sensors", base + "/sensors");
        resources.put("sensorReadingsTemplate", base + "/sensors/{sensorId}/readings");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", "Smart Campus Sensor & Room Management API");
        payload.put("version", "v1");
        payload.put("adminContact", "facilities-api-support@westminster.ac.uk");
        payload.put("resources", resources);

        return Response.ok(payload).build();
    }

    @GET
    @Path("/rooms")
    public Response getRooms() {
        return roomResource.getRooms();
    }

    @POST
    @Path("/rooms")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        return roomResource.createRoom(room, uriInfo);
    }

    @GET
    @Path("/rooms/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        return roomResource.getRoomById(roomId);
    }

    @DELETE
    @Path("/rooms/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        return roomResource.deleteRoom(roomId);
    }

    @GET
    @Path("/sensors")
    public Response getSensors(@javax.ws.rs.QueryParam("type") String type) {
        return sensorResource.getSensors(type);
    }

    @POST
    @Path("/sensors")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        return sensorResource.createSensor(sensor, uriInfo);
    }

    @GET
    @Path("/sensors/{sensorId}/readings")
    public Response getReadings(@PathParam("sensorId") String sensorId) {
        return sensorResource.getSensorReadingsResource(sensorId).getReadings();
    }

    @POST
    @Path("/sensors/{sensorId}/readings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReading(@PathParam("sensorId") String sensorId, SensorReading reading, @Context UriInfo uriInfo) {
        return sensorResource.getSensorReadingsResource(sensorId).createReading(reading, uriInfo);
    }
}
