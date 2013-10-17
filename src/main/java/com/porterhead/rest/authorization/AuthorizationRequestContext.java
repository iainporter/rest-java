package com.porterhead.rest.authorization;

/**
 *
 * @version 1.0
 * @author: Iain Porter
 * @since 28/01/2013
 */
public class AuthorizationRequestContext {

    /**
     * The relative url of the request which starts at the root of the requested resource
     */
    private final String requestUrl;

    /**
     * The Http method (POST, GET, DELETE, PUT)
     */
    private final String httpMethod;

    /**
     * An Iso8061 formatted date timestamp
     */
    private final String requestDateString;

    /**
     * Client generated unique nonce value
     */
    private final String nonceToken;

    /**
     * The AuthorizationToken which should be in a format that the appropriate AuthorizationService can understand
     */
    private final String authorizationToken;

    public AuthorizationRequestContext(String requestUrl, String httpMethod, String requestDateString, String nonceToken, String hashedToken) {
        this.requestUrl = requestUrl;
        this.httpMethod = httpMethod;
        this.requestDateString = requestDateString;
        this.nonceToken = nonceToken;
        this.authorizationToken = hashedToken;
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

    public String getNonceToken() {
        return nonceToken;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
