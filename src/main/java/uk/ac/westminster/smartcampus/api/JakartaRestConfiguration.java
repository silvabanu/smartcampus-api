/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api;

/**
 *
 * @author banuka
 */
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import uk.ac.westminster.smartcampus.api.filters.ApiLoggingFilter;
import uk.ac.westminster.smartcampus.api.mappers.LinkedResourceNotFoundExceptionMapper;
import uk.ac.westminster.smartcampus.api.mappers.RoomNotEmptyExceptionMapper;
import uk.ac.westminster.smartcampus.api.mappers.SensorUnavailableExceptionMapper;
import uk.ac.westminster.smartcampus.api.mappers.ThrowableExceptionMapper;
import uk.ac.westminster.smartcampus.api.resources.DiscoveryResource;
import uk.ac.westminster.smartcampus.api.resources.RoomResource;
import uk.ac.westminster.smartcampus.api.resources.SensorResource;

/**
 * Configures Jakarta RESTful Web Services for the application.
 */
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(ThrowableExceptionMapper.class);
        classes.add(ApiLoggingFilter.class);
        return classes;
    }
}
