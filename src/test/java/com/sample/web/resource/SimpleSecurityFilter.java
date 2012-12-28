package com.sample.web.resource;

import com.sample.web.authorization.UserSession;
import com.sample.web.authorization.impl.SecurityContextImpl;
import com.sample.web.model.User;
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
