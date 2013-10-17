package com.porterhead.rest.user.resource;

import com.porterhead.rest.user.VerificationTokenService;
import com.porterhead.rest.user.api.LostPasswordRequest;
import com.porterhead.rest.user.api.PasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 28/09/2012
 */
@Path("password")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PasswordResource {

    @Autowired
    protected VerificationTokenService verificationTokenService;

    @PermitAll
    @Path("tokens")
    @POST
    public Response sendEmailToken(LostPasswordRequest request) {
        verificationTokenService.sendLostPasswordToken(request);
        return Response.ok().build();
    }

    @PermitAll
    @Path("tokens/{token}")
    @POST
    public Response resetPassword(@PathParam("token") String base64EncodedToken, PasswordRequest request) {
        verificationTokenService.resetPassword(base64EncodedToken, request);
        return Response.ok().build();
    }
}
