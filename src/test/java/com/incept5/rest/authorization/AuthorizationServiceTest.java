package com.incept5.rest.authorization;

import com.incept5.rest.api.ExternalUser;
import com.incept5.rest.model.User;
import com.incept5.rest.repository.UserRepository;
import com.incept5.rest.service.UserService;
import com.incept5.rest.util.DateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: porter
 * Date: 15/03/2012
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class AuthorizationServiceTest {

    private static final String SESSION_TOKEN = "123456789-abcd-efg-hijk";

    private static final User USER = new User();

    @Autowired
    private AuthorizationService authorziationService;

    @Configuration
    static class ContextConfiguration {

        @Bean
        public UserRepository userRepository() {
            UserRepository repo = mock(UserRepository.class);
            when(repo.findByUuid(eq(USER.getUuid().toString()))).thenReturn(USER);
            return repo;
        }

        @Bean
        public AuthorizationService authorizationService() {
            AuthorizationService svc = new AuthorizationService(mock(UserService.class));
            return svc;
        }

    }

    @Test
    public void authorizeUser() throws Exception {
        String dateString = DateUtil.getCurrentDateAsIso8061String();
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(USER.getSessions().first().getToken() + ":hash123,123,POST," + dateString)));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "hash123,123", dateString));
        assertThat(isAuthorized, is(true));
    }

    @Test
    public void invalidUnencodedRequest() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(SESSION_TOKEN + ":hash123,123")));
        User user = new User();
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "hash123,1234"));
        assertThat(isAuthorized, is(false));
    }

    @Test
    public void invalidSessionToken() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256("INVALID-SESSION-TOKEN:abcdef")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), hashedToken, "abcdef"));
        assertThat(isAuthorized, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUser() {
        String hashedToken = new String(Base64.encodeBase64(DigestUtils.sha256(SESSION_TOKEN + ":abcdef")));
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(null, hashedToken,  "abcdef"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSessionToken() {
        boolean isAuthorized = authorziationService.isAuthorized(getAuthorizationRequest(new ExternalUser(USER), null,  "abcdef"));
    }

    private AuthorizationRequest getAuthorizationRequest(ExternalUser user, String hashedToken, String requestString) {
        return getAuthorizationRequest(user, hashedToken, requestString, DateUtil.getCurrentDateAsIso8061String());
    }

    private AuthorizationRequest getAuthorizationRequest(ExternalUser user, String hashedToken, String requestString, String dateString) {
        AuthorizationRequest authRequest = new AuthorizationRequest(user, requestString, "POST", dateString,
                hashedToken);
        return authRequest;
    }
}
