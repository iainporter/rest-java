package com.incept5.rest.resource;

import com.incept5.rest.authorization.UserSession;
import com.incept5.rest.authorization.impl.SecurityContextImpl;
import com.incept5.rest.model.User;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * User: porter
 * Date: 08/05/2012
 * Time: 09:01
 */
public abstract class SimpleSecurityFilter implements ContainerRequestFilter {

    public ContainerRequest filter(ContainerRequest request) {
        UserSession session = new UserSession(getUser());
        request.setSecurityContext(new SecurityContextImpl(session));
        return request;
    }

    abstract User getUser();
}
