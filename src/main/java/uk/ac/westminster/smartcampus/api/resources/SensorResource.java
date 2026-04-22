/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.resources;

/**
 *
 * @author banuka
 */
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import uk.ac.westminster.smartcampus.api.exceptions.LinkedResourceNotFoundException;
import uk.ac.westminster.smartcampus.api.models.Room;
import uk.ac.westminster.smartcampus.api.models.Sensor;
import uk.ac.westminster.smartcampus.api.storage.CampusStore;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final CampusStore store = CampusStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.trim().isEmpty()) {
            String normalizedType = type.trim();
            sensors = sensors.stream()
                    .filter(sensor -> normalizedType.equalsIgnoreCase(sensor.getType()))
                    .collect(Collectors.toList());
        }

        return Response.ok(sensors).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || isBlank(sensor.getId()) || isBlank(sensor.getType()) || isBlank(sensor.getRoomId())) {
            return ApiResponseFactory.error(
                    400,
                    "INVALID_SENSOR_PAYLOAD",
                    "Sensor payload must include non-empty id, type and roomId."
            );
        }

        Room room = store.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Room '" + sensor.getRoomId() + "' does not exist for sensor registration."
            );
        }

        if (isBlank(sensor.getStatus())) {
            sensor.setStatus("ACTIVE");
        }

        synchronized (room) {
            if (!store.getRooms().containsKey(room.getId())) {
                throw new LinkedResourceNotFoundException(
                        "Room '" + sensor.getRoomId() + "' does not exist for sensor registration."
                );
            }

            Sensor existing = store.getSensors().putIfAbsent(sensor.getId(), sensor);
            if (existing != null) {
                return ApiResponseFactory.error(
                        409,
                        "SENSOR_ALREADY_EXISTS",
                        "Sensor with id '" + sensor.getId() + "' already exists."
                );
            }

            if (!room.getSensorIds().contains(sensor.getId())) {
                room.getSensorIds().add(sensor.getId());
            }
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
