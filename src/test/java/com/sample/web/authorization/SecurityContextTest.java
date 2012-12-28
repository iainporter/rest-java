package com.sample.web.authorization;

import com.sample.web.authorization.impl.SecurityContextImpl;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import com.sample.web.service.exception.InvalidAuthorizationHeaderException;
import org.junit.Test;

import javax.ws.rs.core.SecurityContext;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: porter
 * Date: 18/03/2012
 * Time: 11:46
 */
public class SecurityContextTest {

    @Test
    public void validRole() {
        SecurityContext context = createSecurityContext(Role.authenticated);
        assertThat(context.isUserInRole(Role.authenticated.name()), is(true));
    }

    @Test
    public void invalidRole() {
        SecurityContext context = createSecurityContext(Role.authenticated);
        assertThat(context.isUserInRole(Role.merchant.name()), is(false));
    }

    @Test
    public void allowAnonymousRole() {
        SecurityContext context = createSecurityContext(Role.authenticated);
        assertThat(context.isUserInRole("anonymous"), is(true));
    }

    @Test
    public void caseDoesNotMatter() {
        SecurityContext context = createSecurityContext(Role.authenticated);
        assertThat(context.isUserInRole(Role.authenticated.name().toUpperCase()), is(true));
    }

    @Test(expected = InvalidAuthorizationHeaderException.class)
    public void authenticationFailure() {
        User user = new User();
        user.setRole(Role.authenticated);
        UserSession session = new UserSession(user);
        session.setAuthenticationFailure(true);
        SecurityContext context = new SecurityContextImpl(session);
        context.isUserInRole(Role.authenticated.name());
    }

    @Test(expected = InvalidAuthorizationHeaderException.class)
    public void nullSession() {
        SecurityContext context = new SecurityContextImpl(null);
        context.isUserInRole(Role.authenticated.name());
    }


    private SecurityContext createSecurityContext(Role role) {
        User user = new User();
        user.setRole(role);
        UserSession session = new UserSession(user);
        SecurityContext context = new SecurityContextImpl(session);
        return context;
    }
}
