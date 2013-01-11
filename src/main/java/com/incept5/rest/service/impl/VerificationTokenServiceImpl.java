package com.incept5.rest.service.impl;

import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.gateway.EmailServicesGateway;
import com.incept5.rest.model.Role;
import com.incept5.rest.model.User;
import com.incept5.rest.model.VerificationToken;
import com.incept5.rest.repository.UserRepository;
import com.incept5.rest.repository.VerificationTokenRepository;
import com.incept5.rest.service.VerificationTokenService;
import com.incept5.rest.service.data.EmailServiceTokenModel;
import com.incept5.rest.service.exception.AlreadyVerifiedException;
import com.incept5.rest.service.exception.TokenHasExpiredException;
import com.incept5.rest.service.exception.TokenNotFoundException;
import com.incept5.rest.service.exception.UserNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 10/09/2012
 */
@Service("verificationTokenService")
public class VerificationTokenServiceImpl extends BaseServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    private final EmailServicesGateway emailServicesGateway;

    ApplicationConfig config;


    @Autowired
    public VerificationTokenServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository,
                                        EmailServicesGateway emailServicesGateway) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailServicesGateway = emailServicesGateway;
    }

    @Transactional
    public VerificationToken sendEmailVerificationToken(String userId) {
        User user = ensureUserIsLoaded(userId);
        return sendEmailVerificationToken(user);
    }

    private VerificationToken sendEmailVerificationToken(User user) {
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailVerification);
        user.addVerificationToken(token);
        userRepository.save(user);
        emailServicesGateway.sendVerificationToken(new EmailServiceTokenModel(user, token, getConfig().getHostNameUrl()));
        return token;
    }

    @Transactional
    public VerificationToken sendEmailRegistrationToken(String userId) {
        User user = ensureUserIsLoaded(userId);
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailRegistration);
        user.addVerificationToken(token);
        userRepository.save(user);
        emailServicesGateway.sendVerificationToken(new EmailServiceTokenModel(user, token, getConfig().getHostNameUrl()));
        return token;
    }

    @Transactional
    public VerificationToken sendLostPasswordToken(String emailAddress) {
        Assert.notNull(emailAddress);
        User user = userRepository.findByEmailAddress(emailAddress);
        if(user == null) {
            throw new UserNotFoundException();
        }
        VerificationToken token = user.getActiveLostPasswordToken();
        if(token == null) {
            token = new VerificationToken(user, VerificationToken.VerificationTokenType.lostPassword);
            user.addVerificationToken(token);
            userRepository.save(user);
        }
        emailServicesGateway.sendVerificationToken(new EmailServiceTokenModel(user, token, getConfig().getHostNameUrl()));
        return token;
    }

    @Transactional
    public VerificationToken verify(String base64EncodedToken) {
        VerificationToken token = loadToken(base64EncodedToken);
        if(token.isVerified() || token.getUser().isVerified()) {
            throw new AlreadyVerifiedException();
        }
        token.setVerified(true);
        token.getUser().setVerified(true);
        userRepository.save(token.getUser());
        return token;
    }

    @Transactional
    public VerificationToken generateEmailVerificationToken(String emailAddress) {
        Assert.notNull(emailAddress);
        User user = userRepository.findByEmailAddress(emailAddress);
        if(user == null) {
            throw new UserNotFoundException();
        }
        if(user.isVerified()) {
            throw new AlreadyVerifiedException();
        }
        //if token still active resend that
        VerificationToken token = user.getActiveEmailVerificationToken();
        if(token == null) {
             token = sendEmailVerificationToken(user);
        } else {
            emailServicesGateway.sendVerificationToken(new EmailServiceTokenModel(user, token, getConfig().getHostNameUrl()));
        }
        return token;
    }

    @Transactional
    public VerificationToken resetPassword(String base64EncodedToken, String password) {
        Assert.notNull(base64EncodedToken);
        Assert.notNull(password);
        VerificationToken token = loadToken(base64EncodedToken);
        if(token.isVerified()) {
            throw new AlreadyVerifiedException();
        }
        token.setVerified(true);
        User user = token.getUser();
        user.setHashedPassword(user.hashPassword(password));
        //set user to verified if not already and authenticated role
        user.setVerified(true);
        if(user.hasRole(Role.anonymous)) {
            user.setRole(Role.authenticated);
        }
        userRepository.save(user);
        return token;
    }

    private VerificationToken loadToken(String base64EncodedToken) {
        Assert.notNull(base64EncodedToken);
        String rawToken = new String(Base64.decodeBase64(base64EncodedToken));
        VerificationToken token = tokenRepository.findByToken(rawToken);
        if(token == null) {
            throw new TokenNotFoundException();
        }
        if(token.hasExpired()) {
             throw new TokenHasExpiredException();
        }
        return token;
    }

    @Autowired
    public void setConfig(ApplicationConfig config) {
        this.config = config;
    }

    public ApplicationConfig getConfig() {
        return this.config;
    }
}
