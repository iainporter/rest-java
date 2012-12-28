package com.sample.web.resource;

import com.sample.web.api.*;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import com.sample.web.service.exception.AuthenticationException;
import com.sample.web.service.exception.DuplicateUserException;
import com.sample.web.service.exception.ValidationException;
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
        when(userService.createUser(any(CreateUserRequest.class), any(Role.class))).thenReturn(TEST_USER);
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
        when(userService.login(any(LoginRequest.class))).thenReturn(TEST_USER);
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
        when(userService.socialLogin(connection)).thenReturn(TEST_USER);
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
        when(userService.getUser(TEST_USER, TEST_USER.getUuid().toString())).thenReturn(TEST_USER);
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).accept(APPLICATION_JSON).get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        GetUserResponse userResponse = response.getEntity(GetUserResponse.class);
        assertThat(userResponse.getUser().getEmailAddress(), is(TEST_USER.getEmailAddress()));
        assertThat(userResponse.getUser().getFirstName(), is(TEST_USER.getFirstName()));
        assertThat(userResponse.getUser().getLastName(), is(TEST_USER.getLastName()));
    }


    @Test
    public void deleteUser() {
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).accept(APPLICATION_JSON).delete(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void updateUserWithNewEmailAddress() {
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).entity(createUpdateUserRequest("foobar@example.com"),
                APPLICATION_JSON).accept(APPLICATION_JSON).put(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        verify(verificationTokenService, times(1)).sendEmailVerificationToken(any(User.class));
    }

    @Test
    public void updateUserButNotEmailAddress() {
        ClientResponse response = super.resource().path("user/" + TEST_USER.getUuid().toString()).entity(createUpdateUserRequest(TEST_USER.getEmailAddress()),
                APPLICATION_JSON).accept(APPLICATION_JSON).put(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        verify(verificationTokenService, times(0)).sendEmailVerificationToken(any(User.class));
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
