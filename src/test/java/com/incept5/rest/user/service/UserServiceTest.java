package com.incept5.rest.user.service;

import com.incept5.rest.authorization.UserSession;
import com.incept5.rest.service.BaseServiceTest;
import com.incept5.rest.user.builder.*;
import com.incept5.rest.user.domain.Role;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.service.exception.*;
import com.incept5.rest.user.api.*;
import com.incept5.rest.user.exception.AuthenticationException;
import com.incept5.rest.user.exception.AuthorizationException;
import com.incept5.rest.user.exception.DuplicateUserException;
import com.incept5.rest.user.exception.UserNotFoundException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Iain Porter
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/root-context.xml")
@ActiveProfiles(profiles = "dev")
@Transactional
public class UserServiceTest  extends BaseServiceTest {


    @Test
    public void createValidUser() throws Exception {
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        assertOnCreatedUser(user);
    }

    @Test
    public void createDefaultUser() throws Exception {
        ExternalUser user = userService.createUser(Role.authenticated);
        assertOnCreatedUser(user);

    }

    @Test(expected = DuplicateUserException.class)
    public void duplicateUser() throws Exception {
        CreateUserRequest request = getDefaultCreateUserRequest();
        userService.createUser(request, Role.authenticated);
        //do again with same request
        userService.createUser(request, Role.authenticated);

    }

    @Test(expected = ValidationException.class)
    public void nullPasswordRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUser(getUser());
        request.setPassword(new PasswordRequest());
        userService.createUser(request, Role.authenticated);
    }

    @Test(expected = ValidationException.class)
    public void badNameRequest() {
        CreateUserRequest request = new CreateUserRequest();
        ExternalUser user = getUser();
        user.setFirstName(RandomStringUtils.random(101));
        request.setUser(user);
        request.setPassword(new PasswordRequest());
        userService.createUser(request, Role.authenticated);
    }

    @Test(expected = ValidationException.class)
    public void nullEmailAndUsernameRequest() {
        CreateUserRequest request = new CreateUserRequest();
        ExternalUser user = ExternalUserBuilder.create().withFirstName("John")
                .withLastName("Smith")
                .build();
        request.setUser(user);
        request.setPassword(new PasswordRequest("password"));
        userService.createUser(request, Role.authenticated);
    }

    @Test
    public void validLoginWithEmailAddress() throws Exception {
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().get(0).getSessionToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        ExternalUser loggedInUser = userService.login(loginRequest);
        assertThat(loggedInUser.getId().toString(), is(createdUser.getId().toString()));
        assertThat(loggedInUser.getSessions().get(0), is(notNullValue()));
        //check that a new token was issued
        assertThat(loggedInUser.getSessions().get(0).getSessionToken(), is(not(sessionToken)));
        assertThat(loggedInUser.isVerified(), is(false));

    }

    @Test
    public void multipleLoginsGetDifferentSessionToken() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().get(0).getSessionToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getSessions().get(0).getSessionToken();
        String session2 =  userService.login(loginRequest).getSessions().get(0).getSessionToken();

        assertThat(session1, is(not(session2)));
    }

    @Test(expected = ValidationException.class)
    public void invalidLoginRequestNullPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername(createRandomEmailAddress());
        userService.login(request);
    }

    @Test(expected = ValidationException.class)
    public void invalidLoginRequestNullEmailAddress() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password");
        userService.login(request);
    }

    @Test(expected = ValidationException.class)
    public void invalidLoginRequestEmailHasSpace() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password");
        request.setUsername("my username");
        userService.login(request);
    }

    @Test(expected = ValidationException.class)
    public void invalidLoginRequestEmailMalformed() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password");
        request.setUsername("NOTaValidEmailAddress");
        userService.login(request);
    }

    @Test(expected = DuplicateUserException.class)
    public void emailAddressAlreadyExists() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        userService.createUser(request, Role.authenticated);
        userService.createUser(request, Role.authenticated);
    }

    @Test(expected = AuthenticationException.class)
    public void invalidPassword() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser user = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword("qwerty123");
        userService.login(loginRequest);

    }

    @Test(expected = AuthenticationException.class)
    public void userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password");
        request.setUsername(createRandomEmailAddress());
        userService.login(request);
    }


    @Test (expected = AuthorizationException.class)
    public void userNotAuthorizedToDelete() {
        ExternalUser userOne = createUserWithRandomUserName(Role.authenticated);
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        userService.deleteUser(userOne, user.getId().toString());
    }


    @Test
    public void getValidUser() {
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        ExternalUser foundUser = userService.getUser(user, user.getId().toString());
        assertThat(foundUser.getId(), is(user.getId()));
    }

    @Test (expected = UserNotFoundException.class)
    public void getUserNotFound() {
        userService.getUser(new ExternalUser(), "123");
    }

    @Test
    public void getUserByEmailAddress() {
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        ExternalUser foundUser = userService.getUser(user, user.getEmailAddress());
        assertThat(foundUser.getId(), is(user.getId()));
    }

    @Test
    public void updateUser() {
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("foo");
        request.setLastName("bar");
        request.setEmailAddress("foobar@example.com");
        userService.saveUser(user.getId(), request);
        User loadedUser = userRepository.findByUuid(user.getId());
        assertThat(loadedUser.getFirstName(), is("foo"));
        assertThat(loadedUser.getLastName(), is("bar"));
        assertThat(loadedUser.getEmailAddress(), is("foobar@example.com"));
    }

    @Test (expected = ValidationException.class)
    public void updateUserWithInvalidEmailAddress() {
        ExternalUser user = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmailAddress("NotAValidEmailAddress");
        userService.saveUser(user.getId().toString(), request);
    }

    @Test
    public void getMostRecentSession() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().get(0).getSessionToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getSessions().get(0).getSessionToken();
        String session2 = userService.login(loginRequest).getSessions().get(0).getSessionToken();
        ExternalUser updatedUser = userService.getUser(createdUser, createdUser.getId());
        assertThat(updatedUser.getSessions().size(), is(3));
        assertThat(updatedUser.getActiveSession(), is(nullValue()));
        assertThat(updatedUser.getSessions().get(0).getSessionToken(), is(session2));  //most recently updated session

    }

    @Test
    public void saveActiveSession() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        UserSession sessionToken1 = createdUser.getSessions().get(0);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        UserSession sessionToken2 = userService.login(loginRequest).getSessions().get(0);
        createdUser.setActiveSession(sessionToken1);
        userService.saveUserSession(createdUser);
        ExternalUser updatedUser = userService.getUser(createdUser, createdUser.getId());
        //most recently used token
        assertThat(updatedUser.getSessions().get(0).getSessionToken(), is(sessionToken1.getSessionToken()));
    }

    @Test
    public void cleanUpExpiredSessions() {

        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().get(0).getSessionToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getSessions().get(0).getSessionToken();
        String session2 =  userService.login(loginRequest).getSessions().get(0).getSessionToken();
        userService.deleteExpiredSessions(-1);
        ExternalUser externalUser = userService.getUser(createdUser, createdUser.getId());
        assertThat(externalUser.getSessions().size(), is(0));
    }


    private void assertOnCreatedUser(ExternalUser user) throws Exception {
        assertThat(user, is(notNullValue()));
        User foundUser = userRepository.findByUuid(user.getId().toString());
        assertThat(foundUser, is(notNullValue()));
        assertThat(foundUser.getSessions().last().getToken(), is(notNullValue()));
        assertThat(foundUser.getSessions().last().getToken(), is(user.getSessions().get(user.getSessions().size() - 1).getSessionToken()));
        assertThat(foundUser.hasRole(Role.anonymous), is(false));
        assertThat(foundUser.hasRole(Role.authenticated), is(true));
        assertThat(foundUser.isVerified(), is(false));
    }

}
