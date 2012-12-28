package com.sample.web.api;

import com.sample.web.model.User;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: Iain Porter
 */
@XmlRootElement
public class AuthenticatedUserToken {

    private String userId;
    private String token;

    public AuthenticatedUserToken(){}

    public AuthenticatedUserToken(User user) {
        this.userId = user.getUuid().toString();
        this.token = user.getSessionToken();
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
