package com.sample.web.social;

import com.sample.web.api.CreateUserRequest;
import com.sample.web.api.ExternalUser;
import com.sample.web.api.PasswordRequest;
import com.sample.web.builder.ExternalUserBuilder;
import com.sample.web.model.Role;
import com.sample.web.model.SocialUser;
import com.sample.web.model.User;
import com.sample.web.service.UserService;
import com.sample.web.service.impl.UserServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

import java.util.List;

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
    }

    @Test
    public void firstTimeConnected() {
        UserService userService = new UserServiceImpl(usersConnectionRepository);
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
        UserService userService = new UserServiceImpl(usersConnectionRepository);
        ((UserServiceImpl)userService).setUserRepository(userRepository);
        UserProfileBuilder builder = new UserProfileBuilder();
        UserProfile profile = builder.setFirstName("Tom").setLastName("Tucker").setEmail("tt@example.com").setUsername("ttucker").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        User user = userService.socialLogin(connection);
        assertThat(user, is(notNullValue()));
        assertThat(user.getEmailAddress(), is("tt@example.com"));
        assertThat(user.getFirstName(), is("Tom"));
        assertThat(user.getLastName(), is("Tucker"));
        assertThat(user.isVerified(), is(true));
        assertThat(user.hasRole(Role.authenticated), is(true));
    }

     /**
     * Test to ensure that the social login is linked to the previously enrolled email address
     *
     */
    @Test
    public void loginWithEmailAddressThenSocialLogin() {
        //set up services
        UserService userService = new UserServiceImpl(usersConnectionRepository);
        ((UserServiceImpl) userService).setUserRepository(userRepository);
        //create email account
        CreateUserRequest request = getCreateUserRequest(RandomStringUtils.randomAlphabetic(8) + "@example.com");
        User createdUser = userService.createUser(request, Role.authenticated);

        UserProfileBuilder builder = new UserProfileBuilder();
        UserProfile profile = builder.setFirstName(user.getFirstName()).setLastName(user.getLastName()).setEmail(user.getEmailAddress()).setUsername("jsmith.12").build();
        when(connection.fetchUserProfile()).thenReturn(profile);
        when(userRepository.findByEmailAddress(any(String.class))).thenReturn(createdUser);
        when(userRepository.findByUuid(any(String.class))).thenReturn(createdUser);
        User socialLoginUser = userService.socialLogin(connection);
        assertThat(createdUser.getUuid(), is(socialLoginUser.getUuid()));
        assertThat(socialLoginUser.isVerified(), is(true));
    }
//
//    @Test(expected = UserAlreadyExists.class)
//    public void userHasSocialProfile() {
//        User user = new User();
//        user.setEmailAddress("test@example.com");
//        when(userRepository.findByUsername(any(String.class))).thenReturn(null);
//        when(userRepository.findByEmailAddress(any(String.class))).thenReturn(user);
//        when(socialUserRepository.findAllByUser(user)).thenReturn(socialUsers);
//        UserService userService = new UserServiceImpl(usersConnectionRepository);
//        ((UserServiceImpl) userService).setUserRepository(userRepository);
//
//        userService.createUser(getCreateUserRequest("test@example.com"), Role.authenticated);
//    }

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
