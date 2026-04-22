/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.mappers;

/**
 *
 * @author banuka
 */
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public javax.ws.rs.core.Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) exception;
            int status = webApplicationException.getResponse() == null
                    ? 500
                    : webApplicationException.getResponse().getStatus();

            if (status == 404) {
                return ApiResponseFactory.error(404, "NOT_FOUND", "Requested resource was not found.");
            }

            return ApiResponseFactory.error(status, "REQUEST_ERROR", "The request could not be processed.");
        }

        return ApiResponseFactory.error(
                500,
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error. Please contact API support."
        );
    }
}
