/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.mappers;

/**
 *
 * @author banuka
 */
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import uk.ac.westminster.smartcampus.api.exceptions.RoomNotEmptyException;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public javax.ws.rs.core.Response toResponse(RoomNotEmptyException exception) {
        return ApiResponseFactory.error(
                409,
                "ROOM_NOT_EMPTY",
                exception.getMessage()
        );
    }
}
