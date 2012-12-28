package com.sample.web.authorization;

import com.sample.web.model.User;

/**
 * @author: Iain Porter
 */
public class AuthorizationRequest {

    private final User user;
    private final String requestUrl;
    private final String httpMethod;
    private final String requestDateString;
    private final String hashedToken;


    public AuthorizationRequest(User user, String requestUrl, String httpMethod, String requestDateString, String hashedToken) {
        this.user = user;
        this.requestUrl = requestUrl;
        this.httpMethod = httpMethod;
        this.requestDateString = requestDateString;
        this.hashedToken = hashedToken;
    }

    public User getUser() {
        return user;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestDateString() {
        return requestDateString;
    }

    public String getHashedToken() {
        return hashedToken;
    }
}
