package com.porterhead.rest.authorization;

import com.porterhead.rest.user.domain.SessionToken;

import javax.persistence.Cacheable;
import java.io.Serializable;
import java.util.Date;

/**
 * Cacheable Object that holds information on the User and their session status
 *
 *
 * @author: Iain Porter
 */
@Cacheable
public class UserSession implements Serializable {

    private Date createTime;

    private Date lastUpdated;

    private String sessionToken;

    private boolean authenticationFailure = false;

    public UserSession(){}

    public UserSession(SessionToken token) {
        this.createTime = token.getTimeCreated();
        this.lastUpdated = token.getLastUpdated();
        this.sessionToken = token.getToken();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isAuthenticationFailure() {
        return authenticationFailure;
    }

    public void setAuthenticationFailure(boolean authenticationFailure) {
        this.authenticationFailure = authenticationFailure;
    }
}
