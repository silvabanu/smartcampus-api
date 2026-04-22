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
import uk.ac.westminster.smartcampus.api.exceptions.RoomNotEmptyException;
import uk.ac.westminster.smartcampus.api.models.Room;
import uk.ac.westminster.smartcampus.api.storage.CampusStore;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final CampusStore store = CampusStore.getInstance();

    @GET
    public Response getRooms() {
        List<Room> rooms = new ArrayList<>(store.getRooms().values());
        return Response.ok(rooms).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || isBlank(room.getId()) || isBlank(room.getName())) {
            return ApiResponseFactory.error(
                    400,
                    "INVALID_ROOM_PAYLOAD",
                    "Room payload must include non-empty id and name."
            );
        }

        room.setSensorIds(new ArrayList<String>());
        Room existing = store.getRooms().putIfAbsent(room.getId(), room);
        if (existing != null) {
            return ApiResponseFactory.error(
                    409,
                    "ROOM_ALREADY_EXISTS",
                    "Room with id '" + room.getId() + "' already exists."
            );
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return ApiResponseFactory.error(404, "ROOM_NOT_FOUND", "Room '" + roomId + "' was not found.");
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return ApiResponseFactory.error(404, "ROOM_NOT_FOUND", "Room '" + roomId + "' was not found.");
        }

        synchronized (room) {
            List<String> sensorIds = room.getSensorIds();
            if (sensorIds != null && !sensorIds.isEmpty()) {
                throw new RoomNotEmptyException(roomId);
            }

            boolean hasLinkedSensors = store.getSensors().values().stream()
                    .anyMatch(sensor -> roomId.equals(sensor.getRoomId()));
            if (hasLinkedSensors) {
                throw new RoomNotEmptyException(roomId);
            }

            store.getRooms().remove(roomId, room);
        }
        return Response.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
