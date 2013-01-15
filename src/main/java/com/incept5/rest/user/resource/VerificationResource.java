package com.incept5.rest.user.resource;

import com.incept5.rest.resource.BaseResource;
import com.incept5.rest.user.api.EmailVerificationRequest;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 14/09/2012
 */
@Path("verify")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class VerificationResource extends BaseResource {

    @PermitAll
    @Path("tokens/{token}")
    @POST
    public Response verifyToken(@PathParam("token") String token) {
        verificationTokenService.verify(token);
        return Response.ok().build();
    }

    @PermitAll
    @Path("tokens")
    @POST
    public Response sendEmailToken(EmailVerificationRequest request) {
        verificationTokenService.generateEmailVerificationToken(request.getEmailAddress());
        return Response.ok().build();
    }
}
