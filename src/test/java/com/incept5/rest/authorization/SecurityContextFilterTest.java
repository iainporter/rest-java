package com.incept5.rest.authorization;

import com.incept5.rest.filter.SecurityContextFilter;
import com.incept5.rest.model.User;
import com.incept5.rest.repository.UserRepository;
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

    @Before
    public void setUpMocks() {
        userRepository = mock(UserRepository.class);
        authorizationService = mock(AuthorizationService.class);
        containerRequest = mock(ContainerRequest.class);
    }

    @Test
    public void validAuthorization() {
        User user = new User();
        UserRepository userRepository = mock(UserRepository.class);
        when(containerRequest.getHeaderValue("Authorization")).thenReturn(user.getUuid().toString() + ":foo");
        when(containerRequest.getHeaderValue("x-rest-incept5-date")).thenReturn(DateUtil.getCurrentDateAsIso8061String());
        when(containerRequest.getPath()).thenReturn("foo");
        when(containerRequest.getMethod()).thenReturn("GET");
        AuthorizationRequest authRequest = new AuthorizationRequest(user, containerRequest.getPath(), containerRequest.getMethod(), DateUtil.getCurrentDateAsIso8061String(), "foo");
        when(authorizationService.isAuthorized(any(AuthorizationRequest.class))).thenReturn(true);
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        SecurityContextFilter filter = new SecurityContextFilter(userRepository, authorizationService);
        filter.filter(containerRequest);
    }
}
