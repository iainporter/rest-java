package com.porterhead.rest.user.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: Iain Porter
 */
@XmlRootElement
public class AuthenticatedUserToken {

    private String userId;
    private String token;

    public AuthenticatedUserToken(){}

    public AuthenticatedUserToken(String userId, String sessionToken) {
        this.userId = userId;
        this.token = sessionToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
