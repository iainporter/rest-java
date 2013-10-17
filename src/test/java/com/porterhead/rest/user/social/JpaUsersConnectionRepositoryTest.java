package com.porterhead.rest.user.social;

import com.porterhead.rest.user.UserService;
import com.porterhead.rest.user.UserServiceImpl;
import com.porterhead.rest.user.api.AuthenticatedUserToken;
import com.porterhead.rest.user.api.CreateUserRequest;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.user.api.PasswordRequest;
import com.porterhead.rest.user.builder.ExternalUserBuilder;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.SocialUser;
import com.porterhead.rest.user.domain.User;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: porter
 * Date: 21/05/2012
 * Time: 18:35
 */
public class JpaUsersConnectionRepositoryTest extends AbstractSocialTst {

    private JpaUsersConnectionRepository usersConnectionRepository;
    private Validator validator;


    public void setUpRepository() {
       usersConnectionRepository = new JpaUsersConnectionRepository(
                socialUserRepository, userRepository, connectionFactoryLocator, textEncryptor);
         when(userRepository.save(any(User.class))).thenAnswer(new Answer<User>() {
            public User answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                user.setUuid(((User) args[0]).getUuid().toString());
                return (User) args[0];
            }
        });
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void firstTimeConnected() {
        UserService userService = new UserServiceImpl(usersConnectionRepository, validator);
        ((UserServiceImpl)userService).setUserRepository(userRepository);
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        assertThat(userIds.size(), is(1));
        assertThat(userIds.get(0), is(user.getUuid().toString()));
        verify(userRepository).save(any(User.class));
        verify(socialUserRepository).save(any(SocialUser.class));
    }

    @Test
    public void alreadyRegistered() {
        when(socialUserRepository.findByProviderIdAndProviderUserId(PROVIDER_ID, PROVIDER_USER_ID)).thenReturn(socialUsers);
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        assertThat(userIds.size(), is(1));
        assertThat(userIds.get(0), is(user.getUuid().toString()));
        verify(userRepository, never()).save(any(User.class));
        verify(socialUserRepository, never()).save(any(SocialUser.class));

    }

    @Test
    public void validSocialLogin() {
        UserService userService = new UserServiceImpl(usersConnectionRepository, validator);
        ((UserServiceImpl)userService).setUserRepository(userRepository);
        UserProfileBuilder builder = new UserProfileBuilder();
        UserProfile profile = builder.setFirstName("Tom").setLastName("Tucker").setEmail("tt@example.com").setUsername("ttucker").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        AuthenticatedUserToken token = userService.socialLogin(connection);
        ExternalUser user = userService.getUser(new ExternalUser(token.getUserId()), token.getUserId());
        assertThat(user, is(notNullValue()));
        assertThat(user.getEmailAddress(), is("tt@example.com"));
        assertThat(user.getFirstName(), is("Tom"));
        assertThat(user.getLastName(), is("Tucker"));
        assertThat(user.isVerified(), is(true));
        assertThat(user.getRole().equalsIgnoreCase(Role.authenticated.toString()), is(true));
    }

     @Test
    public void updateFromSocialLogin() {
        UserService userService = new UserServiceImpl(usersConnectionRepository, validator);
        ((UserServiceImpl)userService).setUserRepository(userRepository);
        UserProfileBuilder builder = new UserProfileBuilder();
        UserProfile profile = builder.setFirstName("Tom").setLastName("Tucker").setEmail("tt@example.com").setUsername("ttucker").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        userService.socialLogin(connection);
        //login again and update
        profile = builder.setFirstName("Foo").setLastName("Bar").setEmail("foobar@example.com").setUsername("foobar").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        AuthenticatedUserToken token = userService.socialLogin(connection);
        ExternalUser user = userService.getUser(new ExternalUser(token.getUserId()), token.getUserId());
        assertThat(user, is(notNullValue()));
        assertThat(user.getEmailAddress(), is("foobar@example.com"));
        assertThat(user.getFirstName(), is("Foo"));
        assertThat(user.getLastName(), is("Bar"));
        assertThat(user.isVerified(), is(true));
        assertThat(user.getRole().equalsIgnoreCase(Role.authenticated.toString()), is(true));
    }

     /**
     * Test to ensure that the social login is linked to the previously enrolled email address
     *
     */
    @Test
    public void loginWithEmailAddressThenSocialLogin() {
        //set up services
        UserService userService = new UserServiceImpl(usersConnectionRepository, validator);
        ((UserServiceImpl) userService).setUserRepository(userRepository);
        //create email account
        CreateUserRequest request = getCreateUserRequest(RandomStringUtils.randomAlphabetic(8) + "@example.com");
        AuthenticatedUserToken token = userService.createUser(request, Role.authenticated);

        UserProfileBuilder builder = new UserProfileBuilder();
        UserProfile profile = builder.setFirstName(user.getFirstName()).setLastName(user.getLastName()).setEmail(user.getEmailAddress()).setUsername("jsmith.12").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        when(userRepository.findByEmailAddress(any(String.class))).thenReturn(new User(UUID.fromString(token.getUserId())));
        when(userRepository.findByUuid(any(String.class))).thenReturn(new User(UUID.fromString(token.getUserId())));
        AuthenticatedUserToken loginToken = userService.socialLogin(connection);
        ExternalUser user = userService.getUser(new ExternalUser(token.getUserId()), token.getUserId());
        assertThat(token.getUserId(), is(loginToken.getUserId()));
    }

    private CreateUserRequest getCreateUserRequest(String emailAddress) {
        CreateUserRequest request = new CreateUserRequest();
        ExternalUser user = ExternalUserBuilder.create().withFirstName("John")
                .withLastName("Smith")
                .withEmailAddress(emailAddress)
                .build();
        request.setUser(user);
        request.setPassword(new PasswordRequest("password"));
        return request;
    }
}
