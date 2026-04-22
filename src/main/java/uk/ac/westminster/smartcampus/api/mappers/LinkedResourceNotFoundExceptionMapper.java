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
import uk.ac.westminster.smartcampus.api.exceptions.LinkedResourceNotFoundException;
import uk.ac.westminster.smartcampus.api.util.ApiResponseFactory;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public javax.ws.rs.core.Response toResponse(LinkedResourceNotFoundException exception) {
        return ApiResponseFactory.error(
                422,
                "LINKED_RESOURCE_NOT_FOUND",
                exception.getMessage()
        );
    }
}
