package com.sample.web.authorization.impl;

import com.sample.web.authorization.UserSession;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import com.sample.web.service.exception.InvalidAuthorizationHeaderException;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Implementation of {@link javax.ws.rs.core.SecurityContext}
 *
 * User: porter
 * Date: 16/03/2012
 * Time: 16:13
 */
public class SecurityContextImpl implements SecurityContext {

    private final UserSession session;

    public SecurityContextImpl(UserSession session) {
        this.session = session;
    }

    public Principal getUserPrincipal() {
        User user = null;
        if(session != null) {
            user = session.getUser();
        }
        return user;
    }

    public boolean isUserInRole(String role) {
        if(role.equalsIgnoreCase(Role.anonymous.name())) {
             return true;
        }
        if(session == null || session.isAuthenticationFailure()) {
            throw new InvalidAuthorizationHeaderException();
        }
        return session.getUser().getRole().name().equalsIgnoreCase(role);
    }

    public boolean isSecure() {
        return false;
    }

    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
