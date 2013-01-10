package com.incept5.rest.resource;

import com.incept5.rest.api.*;
import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.gateway.EmailServicesGateway;
import com.incept5.rest.service.*;
import com.incept5.rest.service.exception.AuthorizationException;
import com.incept5.rest.api.CreateUserRequest;
import com.incept5.rest.model.Role;
import com.incept5.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
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
        User user = userService.createUser(request, role);
        verificationTokenService.sendEmailRegistrationToken(user);
        URI location = uriInfo.getAbsolutePathBuilder().path(user.getUuid().toString()).build();
        return Response.created(location).entity(new AuthenticatedUserToken(user)).build();
    }

    protected User getUserThatMatchesSecurityContext(SecurityContext sc, String userId) {
        User userMakingRequest = (User) sc.getUserPrincipal();
        if(!userMakingRequest.getUuid().toString().equals(userId)) {
           throw new AuthorizationException("User not authorized to make that request");
        }
        return userMakingRequest;
    }

}
