package com.porterhead.rest.user.resource;

import com.porterhead.rest.resource.BaseResourceTst;
import com.porterhead.rest.resource.ConsumerSimpleSecurityFilter;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.exception.AuthenticationException;
import com.porterhead.rest.user.exception.DuplicateUserException;
import com.porterhead.rest.exception.ValidationException;
import com.porterhead.rest.user.api.*;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.context.ContextLoaderListener;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: porter
 * Date: 05/05/2012
 * Time: 09:45
 */
public class UserResourceTest extends BaseResourceTst {

    public UserResourceTest() {
            super(new WebAppDescriptor.Builder()
                    .contextPath("spring")
                    .contextParam("contextConfigLocation", "classpath:integration-test-context.xml")
                    .contextParam("spring.profiles.active", "dev")
                    .servletClass(SpringServlet.class)
                    .contextListenerClass(ContextLoaderListener.class)
                    .initParam(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, ConsumerSimpleSecurityFilter.class.getName())
                    .build());
        }

    @Test
    public void signUp() {
        when(userService.createUser(any(CreateUserRequest.class), any(Role.class))).thenReturn(
                new AuthenticatedUserToken(TEST_USER.getUuid().toString(), ACTIVE_SESSION.getToken()));
        WebResource webResource = resource();
        CreateUserRequest request = createSignupRequest();
        ClientResponse response = webResource.path("user").entity(request, APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(201));
        AuthenticatedUserToken token = response.getEntity(AuthenticatedUserToken.class);
        assertThat(token.getToken(), is(not(nullValue())));
        assertThat(token.getUserId(), is(not(nullValue())));
    }

    @Test
    public void validationErrorOnCreateUser() {
        when(userService.createUser(any(CreateUserRequest.class), any(Role.class))).thenThrow(new ValidationException());
        CreateUserRequest request = createSignupRequest();
        ClientResponse response = super.resource().path("user").entity(request, APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void duplicateUserOnCreateUser() {
        when(userService.createUser(any(CreateUserRequest.class), any(Role.class))).thenThrow(new DuplicateUserException());
        CreateUserRequest request = createSignupRequest();
        ClientResponse response = super.resource().path("user").entity(request, APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(409));
    }

    @Test
    public void login() {
        when(userService.login(any(LoginRequest.class))).thenReturn(
                new AuthenticatedUserToken(TEST_USER.getUuid().toString(), ACTIVE_SESSION.getToken()));
        ClientResponse response = super.resource().path("user/login").entity(createLoginRequest(), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        AuthenticatedUserToken token = response.getEntity(AuthenticatedUserToken.class);
        assertThat(token.getToken(), is(not(nullValue())));
        assertThat(token.getUserId(), is(not(nullValue())));
    }

    @Test
    public void socialLogin() {
        OAuth2ConnectionFactory connectionFactory = mock(OAuth2ConnectionFactory.class);
        Connection<Facebook> connection = mock(Connection.class);
        when(connectionFactoryLocator.getConnectionFactory(any(String.class))).thenReturn(connectionFactory);
        when(connectionFactory.createConnection(any(AccessGrant.class))).thenReturn(connection);
        when(userService.socialLogin(connection)).thenReturn(
                new AuthenticatedUserToken(TEST_USER.getUuid().toString(), ACTIVE_SESSION.getToken()));
        OAuth2Request request = new OAuth2Request();
        request.setAccessToken("123");
        ClientResponse response = super.resource().path("user/login/facebook").entity(request, APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        AuthenticatedUserToken token = response.getEntity(AuthenticatedUserToken.class);
        assertThat(token.getToken(), is(not(nullValue())));
        assertThat(token.getUserId(), is(not(nullValue())));
    }

    @Test
    public void validationErrorOnLogin() {
        when(userService.login(any(LoginRequest.class))).thenThrow(new ValidationException());
        ClientResponse response = super.resource().path("user/login").entity(createLoginRequest(), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void authenticationErrorOnLogin() {
        when(userService.login(any(LoginRequest.class))).thenThrow(new AuthenticationException());
        ClientResponse response = super.resource().path("user/login").entity(createLoginRequest(), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(401));
    }

    @Test
    public void getUser() {
        when(userService.getUser(any(ExternalUser.class), any(String.class))).thenReturn(new ExternalUser(TEST_USER, ACTIVE_SESSION));
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        ExternalUser userResponse = response.getEntity(ExternalUser.class);
        assertThat(userResponse.getEmailAddress(), is(TEST_USER.getEmailAddress()));
        assertThat(userResponse.getFirstName(), is(TEST_USER.getFirstName()));
        assertThat(userResponse.getLastName(), is(TEST_USER.getLastName()));
    }


    @Test
    public void deleteUser() {
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).accept(APPLICATION_JSON).delete(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void updateUserWithNewEmailAddress() {
        when(userService.saveUser(any(String.class), any(UpdateUserRequest.class))).thenReturn(new ExternalUser(TEST_USER, ACTIVE_SESSION));
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).entity(createUpdateUserRequest("foobar@example.com"),
                APPLICATION_JSON).accept(APPLICATION_JSON).put(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        verify(verificationTokenService, times(1)).sendEmailVerificationToken(any(String.class));
    }

    @Test
    public void updateUserButNotEmailAddress() {
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).entity(createUpdateUserRequest(TEST_USER.getEmailAddress()),
                APPLICATION_JSON).accept(APPLICATION_JSON).put(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        verify(verificationTokenService, times(0)).sendEmailVerificationToken(any(String.class));
    }

    @Test
    public void userTriesToModifyAnotherUserProfile() {
        User user = new User();
        ClientResponse response = super.resource().path("user/" + user.getUuid().toString()).entity(createUpdateUserRequest("foobar@example.com"),
                APPLICATION_JSON).accept(APPLICATION_JSON).put(ClientResponse.class);
        assertThat(response.getStatus(), is(403));
    }


    @Test
    public void runtimeError() {
        when(userService.createUser(any(CreateUserRequest.class), any(Role.class))).thenThrow(new RuntimeException());
        ClientResponse response = super.resource().path("user").entity(createSignupRequest(), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(500));

    }


}
