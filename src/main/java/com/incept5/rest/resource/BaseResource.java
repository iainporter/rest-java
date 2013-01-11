package com.incept5.rest.resource;

import com.incept5.rest.api.AuthenticatedUserToken;
import com.incept5.rest.api.CreateUserRequest;
import com.incept5.rest.api.ExternalUser;
import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.gateway.EmailServicesGateway;
import com.incept5.rest.model.Role;
import com.incept5.rest.service.UserService;
import com.incept5.rest.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * User: porter
 * Date: 03/04/2012
 * Time: 13:22
 */
public class BaseResource {

    @Autowired
    protected UserService userService;

    @Autowired
    protected VerificationTokenService verificationTokenService;

    @Autowired
    protected EmailServicesGateway emailServicesGateway;

    @Context
    protected UriInfo uriInfo;

    @Autowired
    ApplicationConfig config;


    protected Response signUpUser(CreateUserRequest request, Role role) {
        ExternalUser user = userService.createUser(request, role);
        verificationTokenService.sendEmailRegistrationToken(user.getId());
        URI location = uriInfo.getAbsolutePathBuilder().path(user.getId()).build();
        return Response.created(location).entity(new AuthenticatedUserToken(user)).build();
    }

}
