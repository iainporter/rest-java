package com.incept5.rest.user.resource;

import com.incept5.rest.resource.BaseResource;
import com.incept5.rest.user.api.LostPasswordRequest;
import com.incept5.rest.user.api.PasswordRequest;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 28/09/2012
 */
@Path("password")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PasswordResource extends BaseResource {

    @PermitAll
    @Path("tokens")
    @POST
    public Response sendEmailToken(LostPasswordRequest request) {
        verificationTokenService.sendLostPasswordToken(request.getEmailAddress());
        return Response.ok().build();
    }

    @PermitAll
    @Path("tokens/{token}")
    @POST
    public Response resetPassword(@PathParam("token") String base64EncodedToken, PasswordRequest request) {
        request.validate();
        verificationTokenService.resetPassword(base64EncodedToken, request.getPassword());
        return Response.ok().build();
    }
}
