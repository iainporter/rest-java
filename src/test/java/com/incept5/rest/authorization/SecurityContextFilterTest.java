package com.incept5.rest.authorization;

import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.filter.SecurityContextFilter;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.repository.UserRepository;
import com.incept5.rest.util.DateUtil;
import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author: Iain Porter
 */
public class SecurityContextFilterTest {

    UserRepository userRepository;
    AuthorizationService authorizationService;
    ContainerRequest containerRequest;
    ApplicationConfig applicationConfig;

    @Before
    public void setUpMocks() {
        userRepository = mock(UserRepository.class);
        authorizationService = mock(AuthorizationService.class);
        containerRequest = mock(ContainerRequest.class);
        applicationConfig = mock(ApplicationConfig.class);
    }

    @Test
    public void validAuthorization() {
        User user = new User();
        UserRepository userRepository = mock(UserRepository.class);
        when(containerRequest.getHeaderValue("Authorization")).thenReturn(user.getUuid().toString() + ":foo");
        when(containerRequest.getHeaderValue("x-rest-incept5-date")).thenReturn(DateUtil.getCurrentDateAsIso8061String());
        when(containerRequest.getPath()).thenReturn("foo");
        when(containerRequest.getMethod()).thenReturn("GET");
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(5);
        AuthorizationRequest authRequest = new AuthorizationRequest(new ExternalUser(user), containerRequest.getPath(), containerRequest.getMethod(), DateUtil.getCurrentDateAsIso8061String(), "foo", "123");
        when(authorizationService.isAuthorized(any(AuthorizationRequest.class))).thenReturn(true);
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        SecurityContextFilter filter = new SecurityContextFilter(userRepository, authorizationService, applicationConfig);
        filter.filter(containerRequest);
    }
}
