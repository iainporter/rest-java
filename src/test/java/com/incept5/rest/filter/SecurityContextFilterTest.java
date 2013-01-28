package com.incept5.rest.filter;

import com.incept5.rest.authorization.AuthorizationRequest;
import com.incept5.rest.authorization.AuthorizationService;
import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.exception.AuthorizationException;
import com.incept5.rest.user.repository.UserRepository;
import com.sun.jersey.spi.container.ContainerRequest;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.SecurityContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 26/01/2013
 */
public class SecurityContextFilterTest {

    private SecurityContextFilter filter;
    private UserRepository userRepository;
    private AuthorizationService authorizationService;
    private ContainerRequest containerRequest;
    private ApplicationConfig applicationConfig;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        authorizationService = mock(AuthorizationService.class);
        containerRequest = mock(ContainerRequest.class);
        applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(30);
        filter = new SecurityContextFilter(userRepository, authorizationService, applicationConfig);

    }

    @Test
    public void noHeadersInRequest() {
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_AUTHORIZATION)).thenReturn(null);
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_AUTHORIZATION)).thenReturn(null);
        containerRequest = filter.filter(containerRequest);
        assertThat(containerRequest.getUserPrincipal(), is(nullValue()));
    }

    @Test
    public void validAuthHeaders() {
        setUpValidRequest();
        containerRequest = filter.filter(containerRequest);
    }

    private void setUpValidRequest() {
        User user = new User();
        final ExternalUser externalUser = new ExternalUser(user);
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_AUTHORIZATION)).thenReturn(externalUser.getId() + ":123");
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_DATE)).thenReturn(new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis()));
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_NONCE)).thenReturn("123");
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        when(authorizationService.isAuthorized(any(AuthorizationRequest.class))).thenReturn(true);
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(30);
        doAnswer(new Answer() {

            public Object answer(InvocationOnMock invocation) throws Throwable {
                SecurityContext context = (SecurityContext) invocation.getArguments()[0];
                ExternalUser user = (ExternalUser) context.getUserPrincipal();
                assertThat(user.getId(), is(externalUser.getId()));
                return null;
            }
        }).when(containerRequest).setSecurityContext(any(SecurityContext.class));
    }

    @Test (expected = AuthorizationException.class)
    public void dateHeaderIsOutOfRange() {
        User user = new User();
        final ExternalUser externalUser = new ExternalUser(user);
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_AUTHORIZATION)).thenReturn(externalUser.getId() + ":123");
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_DATE)).thenReturn(new DateTime().minusMinutes(10).toString(ISODateTimeFormat.dateTimeNoMillis()));
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(5);
        containerRequest = filter.filter(containerRequest);
    }

    @Test (expected = AuthorizationException.class)
    public void duplicateNonce() throws Exception {
        setUpValidRequest();
        filter.filter(containerRequest);
        Thread.sleep(20); //tolerance limit
        //submit same request again with same nonce value
        filter.filter(containerRequest);
    }



}
