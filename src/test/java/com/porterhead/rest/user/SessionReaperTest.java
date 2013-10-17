package com.porterhead.rest.user;

import com.porterhead.rest.config.ApplicationConfig;
import com.porterhead.rest.user.api.AuthenticatedUserToken;
import com.porterhead.rest.user.api.CreateUserRequest;
import com.porterhead.rest.user.api.LoginRequest;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 26/01/2013
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/root-context.xml")
@ActiveProfiles(profiles = "dev")
@Transactional
public class SessionReaperTest extends BaseServiceTest {

    private SessionReaper sessionReaper;
    ApplicationConfig config = mock(ApplicationConfig.class);

    @Before
    public void setUp() {
        sessionReaper = new SessionReaper();
        sessionReaper.setConfig(config);
        sessionReaper.setUserService(userService);
    }

    @Test
    public void sessionsStillActive() {
        when(config.getSessionExpiryTimeInMinutes()).thenReturn(1);
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken userToken = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        userService.login(loginRequest);
        userService.login(loginRequest);
        sessionReaper.cleanUpExpiredSessions();
        User user = userRepository.findByUuid(userToken.getUserId());
        assertThat(user.getSessions().size(), is(3));
    }

    @Test
    public void sessionHasBeenReaped() {
        when(config.getSessionExpiryTimeInMinutes()).thenReturn(-1);
        CreateUserRequest request = getDefaultCreateUserRequest();
        AuthenticatedUserToken userToken = userService.createUser(request, Role.authenticated);
        sessionReaper.cleanUpExpiredSessions();
        User user = userRepository.findByUuid(userToken.getUserId());
        assertThat(user.getSessions().size(), is(0));
    }

}
