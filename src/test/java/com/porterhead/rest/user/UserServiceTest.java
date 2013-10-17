package com.porterhead.rest.user;

import com.porterhead.rest.exception.ValidationException;
import com.porterhead.rest.user.api.*;
import com.porterhead.rest.user.builder.ExternalUserBuilder;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.SessionToken;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.exception.AuthenticationException;
import com.porterhead.rest.user.exception.AuthorizationException;
import com.porterhead.rest.user.exception.DuplicateUserException;
import com.porterhead.rest.user.exception.UserNotFoundException;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        AuthenticatedUserToken userToken = createUserWithRandomUserName(Role.authenticated);
        assertThat(userToken.getUserId(), is(not(Matchers.<Object>nullValue())));
        assertThat(userToken.getToken(), is(not(Matchers.<Object>nullValue())));
    }

    @Test
    public void createDefaultUser() throws Exception {
        AuthenticatedUserToken userToken = userService.createUser(Role.authenticated);
        assertThat(userToken.getUserId(), is(not(Matchers.<Object>nullValue())));
        assertThat(userToken.getToken(), is(not(Matchers.<Object>nullValue())));

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
        user.setFirstName(RandomStringUtils.randomAlphanumeric(101));
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
        AuthenticatedUserToken createdUserToken = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        AuthenticatedUserToken loginUserToken = userService.login(loginRequest);
        assertThat(loginUserToken.getUserId(), is(createdUserToken.getUserId()));
        User user = userRepository.findByUuid(loginUserToken.getUserId());
        //check that a new token was issued
        assertThat(user.getSessions().first().getToken(), is(not(createdUserToken.getToken())));
        assertThat(user.getSessions().first().getToken(), is(loginUserToken.getToken()));
        assertThat(user.isVerified(), is(false));

    }

    @Test
    public void multipleLoginsGetDifferentSessionToken() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken createdUserToken = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getToken();
        String session2 =  userService.login(loginRequest).getToken();

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

    @Test(expected = DuplicateUserException.class)
    public void emailAddressAlreadyExists() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        userService.createUser(request, Role.authenticated);
        userService.createUser(request, Role.authenticated);
    }

    @Test(expected = AuthenticationException.class)
    public void invalidPassword() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        userService.createUser(request, Role.authenticated);
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
        AuthenticatedUserToken token1 = createUserWithRandomUserName(Role.authenticated);
        AuthenticatedUserToken token2 = createUserWithRandomUserName(Role.authenticated);
        ExternalUser user = userService.getUser(new ExternalUser(token1.getUserId()), token1.getUserId());
        userService.deleteUser(user, token2.getUserId());
    }


    @Test
    public void getValidUser() {
        AuthenticatedUserToken token = createUserWithRandomUserName(Role.authenticated);
        ExternalUser user = new ExternalUser(token.getUserId());
        ExternalUser foundUser = userService.getUser(user, user.getId().toString());
        assertThat(foundUser.getId(), is(user.getId()));
    }

    @Test (expected = UserNotFoundException.class)
    public void getUserNotFound() {
        userService.getUser(new ExternalUser(), "123");
    }

    @Test
    public void getUserByEmailAddress() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken token = userService.createUser(request, Role.authenticated);
        ExternalUser user = new ExternalUser(token.getUserId());
        ExternalUser foundUser = userService.getUser(user, request.getUser().getEmailAddress());
        assertThat(foundUser.getId(), is(user.getId()));
    }

    @Test
    public void updateUser() {
        AuthenticatedUserToken token = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("foo");
        request.setLastName("bar");
        request.setEmailAddress("foobar@example.com");
        userService.saveUser(token.getUserId(), request);
        User loadedUser = userRepository.findByUuid(token.getUserId());
        assertThat(loadedUser.getFirstName(), is("foo"));
        assertThat(loadedUser.getLastName(), is("bar"));
        assertThat(loadedUser.getEmailAddress(), is("foobar@example.com"));
    }

    @Test (expected = ValidationException.class)
    public void updateUserWithInvalidEmailAddress() {
        AuthenticatedUserToken token = createUserWithRandomUserName(Role.authenticated);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmailAddress("NotAValidEmailAddress");
        userService.saveUser(token.getUserId().toString(), request);
    }

    @Test
    public void getMostRecentSession() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken token = userService.createUser(request, Role.authenticated);
        String sessionToken = token.getToken();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        String session1 = userService.login(loginRequest).getToken();
        String session2 = userService.login(loginRequest).getToken();
        User user = userRepository.findByUuid(token.getUserId());
        assertThat(user.getSessions().size(), is(3));
        assertThat(user.getSessions().first().getToken(), is(session2));  //most recently updated session

    }

    @Test
    public void saveActiveSession() {
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken token = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        AuthenticatedUserToken loginToken = userService.login(loginRequest);
        User user = userRepository.findByUuid(token.getUserId());
        SessionToken oldestToken = user.getSessions().last();
        oldestToken.setLastUpdated(new Date());
        userRepository.save(user);
        user = userRepository.findByUuid(token.getUserId());
        //most recently used token is now the login token
        assertThat(user.getSessions().first().getToken(), is(oldestToken.getToken()));
    }

    @Test
    public void cleanUpExpiredSessions() {

        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken token = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        userService.login(loginRequest);
        userService.login(loginRequest);
        userService.deleteExpiredSessions(-1);
        User user = userRepository.findByUuid(token.getUserId());
        assertThat(user.getSessions().size(), is(0));
    }

}
