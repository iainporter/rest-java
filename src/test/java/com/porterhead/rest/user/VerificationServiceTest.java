package com.porterhead.rest.user;

import com.porterhead.rest.config.ApplicationConfig;
import com.porterhead.rest.gateway.EmailServicesGateway;
import com.porterhead.rest.user.api.LostPasswordRequest;
import com.porterhead.rest.user.api.PasswordRequest;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.domain.VerificationToken;
import com.porterhead.rest.user.exception.AlreadyVerifiedException;
import com.porterhead.rest.user.exception.TokenHasExpiredException;
import com.porterhead.rest.user.exception.TokenNotFoundException;
import com.porterhead.rest.user.exception.UserNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 12/09/2012
 */
public class VerificationServiceTest {

    private EmailServicesGateway emailServicesGateway;
    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private List<String> tokens;
    private VerificationTokenService verificationTokenService;
    private Validator validator;

    @Before
    public void setUp() {
        tokens = new ArrayList<String>();

        emailServicesGateway = new EmailServicesGateway() {
            public void sendVerificationToken(EmailServiceTokenModel model) {
                tokens.add(model.getToken());

            }
        };
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        userRepository = mock(UserRepository.class);
        tokenRepository = mock(VerificationTokenRepository.class);
        ApplicationConfig config = mock(ApplicationConfig.class);
        verificationTokenService = new VerificationTokenServiceImpl(userRepository, tokenRepository,
                emailServicesGateway, validator);
        ((VerificationTokenServiceImpl)verificationTokenService).setConfig(config);
        when(config.getHostNameUrl()).thenReturn(new String("http://localhost:8080"));
        when(config.getLostPasswordTokenExpiryTimeInMinutes()).thenReturn(120);
        when(config.getEmailVerificationTokenExpiryTimeInMinutes()).thenReturn(120);
        when(config.getEmailRegistrationTokenExpiryTimeInMinutes()).thenReturn(120);
    }


    @Test
    public void sendLostPasswordToken() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(user.getActiveLostPasswordToken(), is(token));
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationToken.VerificationTokenType.lostPassword));

    }

    @Test
    public void sendLostPasswordTokenAgain() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token1 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        VerificationToken token2 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        assertThat(token1, is(token2));
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(tokens.size(), is(2));  //gateway called twice

    }

    @Test
    public void resetPassword() throws Exception {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        VerificationToken verifiedToken = verificationTokenService.resetPassword(encodedToken, new PasswordRequest("newpassword"));
        assertThat(verifiedToken.isVerified(), is(true));
        assertThat(user.getHashedPassword(), is(user.hashPassword("newpassword")));
        assertThat(user.getVerificationTokens().get(0).isVerified(), is(true));
        //user should also be verified
        assertThat(user.isVerified(), is(true));
    }

    @Test
    public void resetPasswordGetNewToken() {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        VerificationToken verifiedToken = verificationTokenService.resetPassword(encodedToken, new PasswordRequest("newpassword"));
        VerificationToken token2 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        assertThat(token2.getToken(), is(not(token.getToken())));
    }

    @Test
    public void sendEmailToken() {
        User user = generateTestUser();
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailVerificationToken(user.getUuid().toString());
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationToken.VerificationTokenType.emailVerification));
    }

    @Test
    public void sendRegistrationToken() {
        User user = generateTestUser();
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailRegistrationToken(user.getUuid().toString());
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationToken.VerificationTokenType.emailRegistration));
    }

    @Test
    public void verifyValidToken() {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByUuid(user.getUuid().toString())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailVerificationToken(user.getUuid().toString());
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        VerificationToken verifiedToken = verificationTokenService.verify(encodedToken);
        assertThat(verifiedToken.isVerified(), is(true));
        assertThat(user.isVerified(), is(true));
        assertThat(user.getVerificationTokens().get(0).isVerified(), is(true));
    }

    @Test (expected = TokenHasExpiredException.class)
    public void tokenHasExpired() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUser()).thenReturn(user);
        when(token.hasExpired()).thenReturn(true);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test (expected = AlreadyVerifiedException.class)
    public void tokenAlreadyVerified() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUser()).thenReturn(user);
        when(token.hasExpired()).thenReturn(false);
        when(token.isVerified()).thenReturn(true);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test (expected = AlreadyVerifiedException.class)
    public void userAlreadyVerified() {
        User user = generateTestUser();
        user.setVerified(true);
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUser()).thenReturn(user);
        when(token.hasExpired()).thenReturn(false);
        when(token.isVerified()).thenReturn(false);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test (expected = TokenNotFoundException.class)
    public void tokenNotFound() {
        VerificationToken token = new VerificationToken(new User(), VerificationToken.VerificationTokenType.emailVerification, 120);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(null);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test
    public void generateEmailToken() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken token = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        UUID.fromString(sentToken);
        assertThat(sentToken, is(token.getToken()));
    }

    private User generateTestUser() {
        User user = new User();
        user.setEmailAddress("test@example.com");
        return user;
    }

    @Test
    public void generateEmailTokenAlreadyActive() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken token = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        //request it again
        verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        assertThat(user.getVerificationTokens().size(), is(1));
        assertThat(tokens.size(), is(2)); //gateway invoked twice
    }

    @Test
    public void generateEmailTokenAfterExpired() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.hasExpired()).thenReturn(true);
        when(token.getTokenType()).thenReturn(VerificationToken.VerificationTokenType.emailVerification);
        user.addVerificationToken(token);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken generatedToken = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        assertThat(user.getVerificationTokens().size(), is(2));
        assertThat(tokens.size(), is(1)); //gateway invoked once, as first token was manually added
    }

    @Test (expected = UserNotFoundException.class)
    public void emailAddressNotFound() {
        verificationTokenService.generateEmailVerificationToken("test@example.com");
    }

    @Test (expected = AlreadyVerifiedException.class)
    public void generateEmailTokenAlreadyVerified() {
        User user = new User();
        user.setEmailAddress("test@example.com");
        user.setVerified(true);
        VerificationToken token = mock(VerificationToken.class);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
    }

}
