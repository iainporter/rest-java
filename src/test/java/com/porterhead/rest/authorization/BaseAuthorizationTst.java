package com.porterhead.rest.authorization;

import com.porterhead.rest.authorization.impl.SessionTokenAuthorizationService;
import com.porterhead.rest.config.ApplicationConfig;
import com.porterhead.rest.user.UserRepository;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.util.DateUtil;
import org.junit.Before;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 29/01/2013
 */
public class BaseAuthorizationTst {

    protected static String SESSION_TOKEN;
    protected static final User USER = new User();
    {
        USER.addSessionToken();
        SESSION_TOKEN = USER.getSessions().first().getToken();
    }

    protected AuthorizationService authorizationService;
    protected UserRepository userRepository;
    protected ApplicationConfig applicationConfig;

    @Before
    public void setUp() {
         userRepository = mock(UserRepository.class);
         when(userRepository.findByUuid(eq(USER.getUuid().toString()))).thenReturn(USER);
         when(userRepository.findBySession(eq(SESSION_TOKEN))).thenReturn(USER);
         applicationConfig = mock(ApplicationConfig.class);
         when(applicationConfig.getSessionDateOffsetInMinutes()).thenReturn(30);
         authorizationService = new SessionTokenAuthorizationService(userRepository);
    }

    protected AuthorizationRequestContext getAuthorizationRequest(String hashedToken, String requestString, String nonce) {
        return getAuthorizationRequest(hashedToken, requestString, DateUtil.getCurrentDateAsIso8061String(), nonce);
    }

    protected AuthorizationRequestContext getAuthorizationRequest(String hashedToken, String requestString, String dateString, String nonce) {
        AuthorizationRequestContext authRequest = new AuthorizationRequestContext(requestString, "POST", dateString, nonce, hashedToken);
        return authRequest;
    }
}
