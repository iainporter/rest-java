package com.incept5.rest.user.service;

import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.service.BaseServiceTest;
import com.incept5.rest.user.api.CreateUserRequest;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.api.LoginRequest;
import com.incept5.rest.user.domain.Role;
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
 * @author: Iain Porter iain.porter@incept5.com
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
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUser().getEmailAddress());
        loginRequest.setPassword(request.getPassword().getPassword());
        userService.login(loginRequest).getSessions().get(0).getSessionToken();
        userService.login(loginRequest).getSessions().get(0).getSessionToken();
        sessionReaper.cleanUpExpiredSessions();
        ExternalUser externalUser = userService.getUser(createdUser, createdUser.getId());
        assertThat(externalUser.getSessions().size(), is(3));
    }

    @Test
    public void sessionHasBeenReaped() {
        when(config.getSessionExpiryTimeInMinutes()).thenReturn(-1);
        CreateUserRequest request = getDefaultCreateUserRequest();
        ExternalUser createdUser = userService.createUser(request, Role.authenticated);
        sessionReaper.cleanUpExpiredSessions();
        ExternalUser externalUser = userService.getUser(createdUser, createdUser.getId());
        assertThat(externalUser.getSessions().size(), is(0));
    }

}
