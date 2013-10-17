package com.porterhead.rest.resource;

import com.porterhead.rest.user.UserService;
import com.porterhead.rest.user.VerificationTokenService;
import com.porterhead.rest.user.api.LoginRequest;
import com.porterhead.rest.user.api.PasswordRequest;
import com.porterhead.rest.user.builder.ExternalUserBuilder;
import com.porterhead.rest.gateway.EmailServicesGateway;
import com.porterhead.rest.mock.AppMockConfiguration;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.SessionToken;
import com.porterhead.rest.user.api.CreateUserRequest;
import com.porterhead.rest.user.api.UpdateUserRequest;
import com.porterhead.rest.user.domain.User;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.test.context.ActiveProfiles;

/**
 * User: porter
 * Date: 04/05/2012
 * Time: 19:09
 */
@ActiveProfiles(profiles = "dev")
public class BaseResourceTst extends JerseyTest {

    public BaseResourceTst(WebAppDescriptor descriptor) {
        super(descriptor);
    }

    protected static User TEST_USER;
    protected static String FIRST_NAME = "Test";
    protected static String LAST_NAME = "User";
    protected static String EMAIL_ADDRESS = "test@example.com";
    protected static String USERNAME = "testuser";
    protected static String PASSWORD = "password";
    protected static PasswordRequest PASSWORD_REQUEST;

    {
        TEST_USER = new User();
        TEST_USER.setFirstName(FIRST_NAME);
        TEST_USER.setLastName(LAST_NAME);
        TEST_USER.setEmailAddress(EMAIL_ADDRESS);
        TEST_USER.setRole(Role.authenticated);
    }

    {
        PASSWORD_REQUEST = new PasswordRequest();
        PASSWORD_REQUEST.setPassword(PASSWORD);
    }

    protected static SessionToken ACTIVE_SESSION;
    {
        TEST_USER.addSessionToken();
        ACTIVE_SESSION = TEST_USER.getSessions().first();
    }

    protected static ApplicationContext appCtx;


    public static class ApplicationContextAccess implements ApplicationContextAware {
        public void setApplicationContext(ApplicationContext ctx) {
            appCtx = ctx;
        }
    }

    protected UserService userService;
    protected VerificationTokenService verificationTokenService;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected EmailServicesGateway emailServicesGateway;


    protected Environment environment;

    /**
     * Relies on component scanning of mock services from {@link com.porterhead.rest.mock.AppMockConfiguration}
     */
    @Before
    public void setUpMocks() {
        AppMockConfiguration config = appCtx.getBean(AppMockConfiguration.class);
        userService = config.userService();
        verificationTokenService = config.verificationTokenService();
        connectionFactoryLocator = (ConnectionFactoryLocator) appCtx.getBean("connectionFactoryLocator");
        environment = config.environment();
        emailServicesGateway = config.emailServicesGateway();
    }

    protected CreateUserRequest createSignupRequest() {
        return new CreateUserRequest(ExternalUserBuilder.create().withEmailAddress(TEST_USER.getEmailAddress())
                .withFirstName(TEST_USER.getFirstName()).withLastName(TEST_USER.getLastName()).build(), PASSWORD_REQUEST);
    }


    protected LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USER.getEmailAddress());
        request.setPassword(PASSWORD);
        return request;
    }

    protected UpdateUserRequest createUpdateUserRequest(String emailAddress) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmailAddress(emailAddress);
        return request;
    }

}
