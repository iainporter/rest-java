package com.porterhead.rest.authorization.impl;

import com.porterhead.rest.authorization.AuthorizationRequestContext;
import com.porterhead.rest.authorization.AuthorizationService;
import com.porterhead.rest.user.UserRepository;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.user.domain.AuthorizationToken;
import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.exception.AuthorizationException;

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
        AuthorizationToken authorizationToken = user.getAuthorizationToken();
            if (authorizationToken.getToken().equals(token)) {
                externalUser = new ExternalUser(user);
            }
        return externalUser;
    }
}
