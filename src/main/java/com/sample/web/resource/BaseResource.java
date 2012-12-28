package com.sample.web.resource;

import com.sample.web.api.*;
import com.sample.web.config.ApplicationConfig;
import com.sample.web.gateway.EmailServicesGateway;
import com.sample.web.model.*;
import com.sample.web.service.*;
import com.sample.web.service.exception.AuthorizationException;
import com.sample.web.api.CreateUserRequest;
import com.sample.web.config.ApplicationConfig;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
