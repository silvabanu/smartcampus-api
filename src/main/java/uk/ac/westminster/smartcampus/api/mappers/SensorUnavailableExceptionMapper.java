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
import uk.ac.westminster.smartcampus.api.exceptions.SensorUnavailableException;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public javax.ws.rs.core.Response toResponse(SensorUnavailableException exception) {
        return ApiResponseFactory.error(
                403,
                "SENSOR_UNAVAILABLE",
                exception.getMessage()
        );
    }
}
