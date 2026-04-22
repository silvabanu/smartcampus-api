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
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import uk.ac.westminster.smartcampus.api.exceptions.SensorUnavailableException;
import uk.ac.westminster.smartcampus.api.models.Sensor;
import uk.ac.westminster.smartcampus.api.models.SensorReading;
import uk.ac.westminster.smartcampus.api.storage.CampusStore;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final CampusStore store = CampusStore.getInstance();
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return ApiResponseFactory.error(404, "SENSOR_NOT_FOUND", "Sensor '" + sensorId + "' was not found.");
        }

        List<SensorReading> readings = store.getOrCreateReadings(sensorId);
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return ApiResponseFactory.error(404, "SENSOR_NOT_FOUND", "Sensor '" + sensorId + "' was not found.");
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }

        if (reading == null) {
            return ApiResponseFactory.error(400, "INVALID_READING_PAYLOAD", "Reading payload cannot be empty.");
        }

        if (isBlank(reading.getId())) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        store.getOrCreateReadings(sensorId).add(reading);
        sensor.setCurrentValue(reading.getValue());

        URI location = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(location).entity(reading).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
