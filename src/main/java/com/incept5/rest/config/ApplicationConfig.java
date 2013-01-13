package com.incept5.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * User: porter
 * Date: 17/05/2012
 * Time: 19:07
 */
@Configuration
@PropertySource({"classpath:/properties/app.properties"})
public class ApplicationConfig {

    private final static String HOSTNAME_PROPERTY = "hostNameUrl";

    private final static String SESSION_EXPIRY_DURATION = "session.timeToLive.inMinutes";
    private final static String TOKEN_EMAIL_REGISTRATION_DURATION = "token.emailRegistration.timeToLive.inMinutes";
    private final static String TOKEN_EMAIL_VERIFICATION_DURATION = "token.emailVerification.timeToLive.inMinutes";
    private final static String TOKEN_LOST_PASSWORD_DURATION = "token.lostPassword.timeToLive.inMinutes";
    private final static String EMAIL_SERVICES_FROM_ADDRESS = "email.services.fromAddress";
    private final static String EMAIL_SERVICES_REPLYTO_ADDRESS = "email.services.replyTo";
    private final static String EMAIL_SERVICES_VERIFICATION_EMAIL_SUBJECT_TEXT = "email.services.emailVerificationSubjectText";
    private final static String EMAIL_SERVICES_REGISTRATION_EMAIL_SUBJECT_TEXT = "email.services.emailRegistrationSubjectText";
    private final static String EMAIL_SERVICES_LOST_PASSWORD_SUBJECT_TEXT = "email.services.lostPasswordSubjectText";


    @Autowired
    protected Environment environment;

    public String getHostNameUrl() {
        return environment.getProperty(HOSTNAME_PROPERTY);
    }

    public String getFacebookClientId() {
        return environment.getProperty("facebook.clientId");
    }

    public String getFacebookClientSecret() {
        return environment.getProperty("facebook.clientSecret");
    }

    public int getSessionExpiryTimeInMinutes() {
        return Integer.parseInt(environment.getProperty(SESSION_EXPIRY_DURATION));
    }

    public int getEmailRegistrationTokenExpiryTimeInMinutes() {
        return Integer.parseInt(environment.getProperty(TOKEN_EMAIL_REGISTRATION_DURATION));
    }

    public int getEmailVerificationTokenExpiryTimeInMinutes() {
        return Integer.parseInt(environment.getProperty(TOKEN_EMAIL_VERIFICATION_DURATION));
    }

    public int getLostPasswordTokenExpiryTimeInMinutes() {
        return Integer.parseInt(environment.getProperty(TOKEN_LOST_PASSWORD_DURATION));
    }

    public String getEmailVerificationSubjectText() {
        return environment.getProperty(EMAIL_SERVICES_VERIFICATION_EMAIL_SUBJECT_TEXT);
    }

    public String getEmailRegistrationSubjectText() {
        return environment.getProperty(EMAIL_SERVICES_REGISTRATION_EMAIL_SUBJECT_TEXT);
    }

    public String getLostPasswordSubjectText() {
        return environment.getProperty(EMAIL_SERVICES_LOST_PASSWORD_SUBJECT_TEXT);
    }

    public String getEmailFromAddress() {
        return environment.getProperty(EMAIL_SERVICES_FROM_ADDRESS);
    }

    public String getEmailReplyToAddress() {
        return environment.getProperty(EMAIL_SERVICES_REPLYTO_ADDRESS);
    }
}
