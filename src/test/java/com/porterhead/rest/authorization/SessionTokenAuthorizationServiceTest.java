package com.porterhead.rest.authorization;

import com.porterhead.rest.authorization.impl.SessionTokenAuthorizationService;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.user.exception.AuthorizationException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 29/01/2013
 */
public class SessionTokenAuthorizationServiceTest extends BaseAuthorizationTst {


    @Before
    public void setUp() {
        super.setUp();
        authorizationService = new SessionTokenAuthorizationService(userRepository);
    }

    @Test
    public void authorizeUser() throws Exception {
        String sessionToken = "123456789";
        ExternalUser user = authorizationService.authorize(getAuthorizationRequest(SESSION_TOKEN, "user/123", null));
        assertThat(user.getId(), is(USER.getUuid().toString()));
    }

    @Test (expected = AuthorizationException.class)
    public void invalidSessionToken() {
        authorizationService.authorize(getAuthorizationRequest("abc", "abcdef", "123"));
    }

    @Test
    public void noSessionToken() {
        ExternalUser user = authorizationService.authorize(getAuthorizationRequest(null, "abcdef", null));
        assertThat(user, is(Matchers.<Object>nullValue()));
    }
}
