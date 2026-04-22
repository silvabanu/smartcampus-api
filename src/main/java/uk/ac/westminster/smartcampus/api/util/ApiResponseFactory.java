/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.util;

/**
 *
 * @author banuka
 */
import javax.ws.rs.core.Response;
import uk.ac.westminster.smartcampus.api.payloads.ErrorResponse;

public final class ApiResponseFactory {

    private ApiResponseFactory() {
    }

    public static Response error(int status, String error, String message) {
        ErrorResponse payload = new ErrorResponse(
                status,
                error,
                message,
                System.currentTimeMillis()
        );
        return Response.status(status).entity(payload).build();
    }
}
