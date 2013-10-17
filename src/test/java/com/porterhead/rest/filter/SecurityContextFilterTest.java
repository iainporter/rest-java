package com.porterhead.rest.filter;

import com.porterhead.rest.config.ApplicationConfig;
import com.porterhead.rest.user.UserRepository;
import com.porterhead.rest.user.UserService;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.exception.AuthorizationException;
import com.sun.jersey.spi.container.ContainerRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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


public class SecurityContextFilterTest {

    private SecurityContextFilter filter;
    private UserRepository userRepository;
    private UserService userService;
    private ContainerRequest containerRequest;
    private ApplicationConfig applicationConfig;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        containerRequest = mock(ContainerRequest.class);
        applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(30);
        when(applicationConfig.requireSignedRequests()).thenReturn(true);
        filter = new SecurityContextFilter(userRepository, userService, applicationConfig);

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
        user.addSessionToken();
        final ExternalUser externalUser = new ExternalUser(user);
        String dateString = new DateTime().toString(ISODateTimeFormat.dateTimeNoMillis());
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(user.getSessions().first().getToken() + ":user/555,POST," + dateString + ",123")));
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_AUTHORIZATION)).thenReturn(externalUser.getId() + ":" + hashedToken);
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_DATE)).thenReturn(dateString);
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_NONCE)).thenReturn("123");
        when(containerRequest.getPath()).thenReturn("user/555");
        when(containerRequest.getMethod()).thenReturn("POST");
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
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
        when(containerRequest.getHeaderValue(SecurityContextFilter.HEADER_NONCE)).thenReturn("123");
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
