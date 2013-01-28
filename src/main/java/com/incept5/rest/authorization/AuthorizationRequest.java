package com.incept5.rest.authorization;

import com.incept5.rest.user.api.ExternalUser;

/**
 * @author: Iain Porter
 */
public class AuthorizationRequest {

    private final ExternalUser user;
    private final String requestUrl;
    private final String httpMethod;
    private final String requestDateString;
    private final String nonceToken;
    private final String hashedToken;


    public AuthorizationRequest(ExternalUser user, String requestUrl, String httpMethod, String requestDateString, String hashedToken, String nonce) {
        this.user = user;
        this.requestUrl = requestUrl;
        this.httpMethod = httpMethod;
        this.requestDateString = requestDateString;
        this.hashedToken = hashedToken;
        this.nonceToken = nonce;
    }

    public ExternalUser getUser() {
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

    public String getNonceToken() {
        return nonceToken;
    }
}
