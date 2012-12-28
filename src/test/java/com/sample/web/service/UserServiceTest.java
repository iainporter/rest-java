package com.sample.web.service;

import com.sample.web.api.*;
import com.sample.web.builder.ExternalUserBuilder;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import com.sample.web.service.exception.*;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
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
        User user = createUserWithRandomUserName(Role.authenticated);
        assertOnCreatedUser(user);
    }

    @Test
    public void createDefaultUser() throws Exception {
        User user = userService.createUser(Role.authenticated);
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
    public void validLoginWithUsername() throws Exception {
        CreateUserRequest request = getDefaultCreateUserRequest();
        User createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().first().getToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        User loggedInUser = userService.login(loginRequest);
        assertThat(loggedInUser.getUuid().toString(), is(createdUser.getUuid().toString()));
        assertThat(loggedInUser.getSessions().first(), is(notNullValue()));
        //check that a new token was issued
        assertThat(loggedInUser.getSessions().first().getToken(), is(not(sessionToken)));
        assertThat(loggedInUser.getSessions().last().getToken(), is(sessionToken));
        assertThat(loggedInUser.isVerified(), is(false));
    }

    @Test
    public void validLoginWithEmailAddress() throws Exception {
        CreateUserRequest request = getDefaultCreateUserRequest();
        User createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().first().getToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        User loggedInUser = userService.login(loginRequest);
        assertThat(loggedInUser.getUuid().toString(), is(createdUser.getUuid().toString()));
        assertThat(loggedInUser.getSessions().first(), is(notNullValue()));
        //check that a new token was issued
        assertThat(loggedInUser.getSessions().first().getToken(), is(not(sessionToken)));
        assertThat(loggedInUser.isVerified(), is(false));

    }

    @Test
    public void multipleLoginsGetDifferentSessionToken() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        User createdUser = userService.createUser(request, Role.authenticated);
        String sessionToken = createdUser.getSessions().first().getToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getSessions().first().getToken();
        String session2 =  userService.login(loginRequest).getSessions().first().getToken();

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
        User user = userService.createUser(request, Role.authenticated);
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
        User userOne = createUserWithRandomUserName(Role.merchant);
        User user = createUserWithRandomUserName(Role.authenticated);
        userService.deleteUser(userOne, user.getUuid().toString());
    }


    @Test
    public void getValidUser() {
        User user = createUserWithRandomUserName(Role.authenticated);
        User foundUser = userService.getUser(user, user.getUuid().toString());
        assertThat(foundUser, is(user));
    }

    @Test (expected = UserNotFoundException.class)
    public void getUserNotFound() {
        userService.getUser(new User(), "123");
    }

    @Test
    public void getUserByEmailAddress() {
        User user = createUserWithRandomUserName(Role.authenticated);
        User foundUser = userService.getUser(user, user.getEmailAddress());
        assertThat(foundUser, is(user));
    }

    @Test
    public void updateUser() {
        User user = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("foo");
        request.setLastName("bar");
        request.setEmailAddress("foobar@example.com");
        userService.saveUser(user.getUuid().toString(), request);
        User loadedUser = userRepository.findByUuid(user.getUuid().toString());
        assertThat(user.getFirstName(), is("foo"));
        assertThat(user.getLastName(), is("bar"));
        assertThat(user.getEmailAddress(), is("foobar@example.com"));
    }

    @Test (expected = ValidationException.class)
    public void updateUserWithInvalidEmailAddress() {
        User user = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmailAddress("NotAValidEmailAddress");
        userService.saveUser(user.getUuid().toString(), request);
    }

    private void assertOnCreatedUser(User user) throws Exception {
        assertThat(user, is(notNullValue()));
        User foundUser = userRepository.findByUuid(user.getUuid().toString());
        assertThat(foundUser, is(notNullValue()));
        assertThat(foundUser.getSessions().last().getToken(), is(notNullValue()));
        assertThat(foundUser.getSessions().last(), is(user.getSessions().last()));
        assertThat(foundUser.hasRole(Role.anonymous), is(false));
        assertThat(foundUser.hasRole(Role.authenticated), is(true));
        assertThat(foundUser.isVerified(), is(false));
    }

}
