package com.porterhead.rest.authorization;

import com.porterhead.rest.authorization.exception.InvalidAuthorizationHeaderException;
import com.porterhead.rest.user.api.ExternalUser;
import com.porterhead.rest.authorization.impl.SecurityContextImpl;
import com.porterhead.rest.user.domain.Role;
import com.porterhead.rest.user.domain.User;
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
        assertThat(context.isUserInRole(Role.administrator.name()), is(false));
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
        ExternalUser externalUser = null;
        SecurityContext context = new SecurityContextImpl(externalUser);
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
        ExternalUser externalUser = new ExternalUser(user);
        SecurityContext context = new SecurityContextImpl(externalUser);
        return context;
    }
}
