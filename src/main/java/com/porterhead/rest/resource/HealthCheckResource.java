package com.porterhead.rest.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: porter
 * Date: 11/04/2012
 * Time: 15:21
 */
@Path("/healthcheck")
@Component
@Produces({MediaType.TEXT_PLAIN})
@PropertySource("classpath:properties/app.properties")
public class HealthCheckResource {

    @Autowired
    Environment env;

    @PermitAll
    @GET
    public Response ping() {
        return Response.ok().entity("Running version " + env.getProperty("application.version")).build();
    }

}
