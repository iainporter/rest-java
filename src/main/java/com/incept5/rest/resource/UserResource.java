package com.incept5.rest.resource;

import com.incept5.rest.api.*;
import com.incept5.rest.model.Role;
import com.incept5.rest.service.exception.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;

/**
 * User: porter
 * Date: 12/03/2012
 * Time: 18:57
 */
@Path("/user")
@Component
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class UserResource extends BaseResource {

    private ConnectionFactoryLocator connectionFactoryLocator;

    public UserResource(){}

    @Autowired
    public UserResource(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }


    @PermitAll
    @POST
    public Response signupUser(CreateUserRequest request) {
        return signUpUser(request, Role.authenticated);
    }

    @RolesAllowed("admin")
    @Path("{userId}")
    @DELETE
    public Response deleteUser(@Context SecurityContext sc, @PathParam("userId") String userId) {
        ExternalUser userMakingRequest = (ExternalUser)sc.getUserPrincipal();
        userService.deleteUser(userMakingRequest, userId);
        return Response.ok().build();
    }

    @PermitAll
    @Path("login")
    @POST
    public Response login(LoginRequest request) {
        ExternalUser user = userService.login(request);
        return getLoginResponse(user);
    }

    @PermitAll
    @Path("login/{providerId}")
    @POST
    public Response socialLogin(@PathParam("providerId") String providerId, OAuth2Request request) {
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
        Connection<?> connection = connectionFactory.createConnection(new AccessGrant(request.getAccessToken()));
        ExternalUser user = userService.socialLogin(connection);
        return getLoginResponse(user);
    }

    @RolesAllowed({"authenticated"})
    @Path("{userId}")
    @GET
    public Response getUser(@Context SecurityContext sc, @PathParam("userId") String userId) {
        ExternalUser userMakingRequest = (ExternalUser)sc.getUserPrincipal();
        ExternalUser user =  userService.getUser(userMakingRequest, userId);
        return Response.ok().entity(user).build();
    }

    @RolesAllowed({"authenticated"})
    @Path("{userId}")
    @PUT
    public Response updateUser(@Context SecurityContext sc, @PathParam("userId") String userId, UpdateUserRequest request) {
        ExternalUser userMakingRequest = (ExternalUser)sc.getUserPrincipal();
        if(!userMakingRequest.getId().equals(userId)) {
            throw new AuthorizationException("User not authorized to modify this profile");
        }
        boolean sendVerificationToken = StringUtils.hasLength(request.getEmailAddress()) &&
                !request.getEmailAddress().equals(userMakingRequest.getEmailAddress());
        ExternalUser savedUser = userService.saveUser(userId, request);
        if(sendVerificationToken) {
            verificationTokenService.sendEmailVerificationToken(savedUser.getId());
        }
        return Response.ok().build();
    }

    private Response getLoginResponse(ExternalUser user) {
        URI location = uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build();
        AuthenticatedUserToken response = new AuthenticatedUserToken(user);
        return Response.ok().entity(response).contentLocation(location).build();
    }




}
