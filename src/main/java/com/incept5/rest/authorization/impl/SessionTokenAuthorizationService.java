package com.incept5.rest.authorization.impl;

import com.incept5.rest.authorization.AuthorizationRequestContext;
import com.incept5.rest.authorization.AuthorizationService;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.domain.SessionToken;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.exception.AuthorizationException;
import com.incept5.rest.user.repository.UserRepository;

import java.util.Date;

/**
 *
 * Simple authorization service that requires a session token in the Authorization header
 * This is then matched to a user
 *
 * @version 1.0
 * @author: Iain Porter
 * @since 29/01/2013
 */
public class SessionTokenAuthorizationService implements AuthorizationService {

    /**
     * directly access user objects
     */
    private final UserRepository userRepository;

    public SessionTokenAuthorizationService(UserRepository repository) {
        this.userRepository = repository;
    }

    public ExternalUser authorize(AuthorizationRequestContext securityContext) {
        String token = securityContext.getAuthorizationToken();
        ExternalUser externalUser = null;
        if(token == null) {
            return externalUser;
        }
        User user =  userRepository.findBySession(token);
        if(user == null) {
            throw new AuthorizationException("Session token not valid");
        }
        for (SessionToken sessionToken : user.getSessions()) {
            if (sessionToken.getToken().equals(token)) {
                sessionToken.setLastUpdated(new Date());
                userRepository.save(user);
                externalUser = new ExternalUser(user);
            }
        }
        return externalUser;
    }
}
