package com.porterhead.rest.authorization;

import com.porterhead.rest.authorization.impl.RequestSigningAuthorizationService;
import com.porterhead.rest.user.UserService;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.user.exception.AuthorizationException;
import com.porterhead.rest.util.DateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: porter
 * Date: 15/03/2012
 * Time: 11:41
 */
public class RequestSigningAuthorizationServiceTest extends BaseAuthorizationTst {

    private AuthorizationService authorizationService;
    private UserService userService;

    @Before
    public void setUp() {
         super.setUp();
         userService = mock(UserService.class);
         authorizationService = new RequestSigningAuthorizationService(userRepository, userService, applicationConfig);
    }

    @Test
    public void authorizeUser() throws Exception {
        String dateString = DateUtil.getCurrentDateAsIso8061String();
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":user/555,POST," + dateString + ",123")));
        ExternalUser user = authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "user/555", dateString, "123"));
        assertThat(user.getId(), is(USER.getUuid().toString()));
    }

    @Test (expected = AuthorizationException.class)
    public void invalidUnEncodedRequest() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(SESSION_TOKEN + ":hash123,123")));
        authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "hash123,1234", "123"));
    }

    @Test (expected = AuthorizationException.class)
    public void invalidSessionToken() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256("INVALID-SESSION-TOKEN:abcdef")));
        authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "abcdef", "123"));
    }

    @Test
    public void missingNonce() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256("INVALID-SESSION-TOKEN:abcdef")));
        ExternalUser user = authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "abcdef", null));
        assertThat(user, is(Matchers.<Object>nullValue()));
    }

    @Test (expected = AuthorizationException.class)
    public void wrongNonce() {
        String dateString = DateUtil.getCurrentDateAsIso8061String();
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":hash123,123,POST," + dateString + ",123")));
        authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "hash123,123", dateString, "567"));
    }

    @Test (expected = AuthorizationException.class)
    public void dateOutOfRange() {
        when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(5);
        String dateString = DateUtil.getDateDateAsIso8061String(new DateTime().minusMinutes(20));
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":hash123,123,POST," + dateString + ",123")));
        authorizationService.authorize(getAuthorizationRequest(USER.getUuid().toString() + ":" + hashedToken, "hash123,123", dateString, "567"));
    }

    @Test
    public void nullSessionToken() {
        ExternalUser user = authorizationService.authorize(getAuthorizationRequest(null,  "abcdef", "123"));
        assertThat(user, is(Matchers.<Object>nullValue()));
    }

}
