package com.incept5.rest.authorization;

import com.incept5.rest.model.User;

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

    private final User user;

    private final Date createTime = new Date();

    private Date lastUpdated;

    private boolean authenticationFailure = false;

    public UserSession(User user) {
        this.user = user;
    }

    /**
     * Date that the last request by this User was made
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * The User for the session
     */
    public User getUser() {
        return user;
    }

    /**
     * Date that the session was created
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Did the current request fail authorization
     *
     * @return true if the request is not authorized
     */
    public boolean isAuthenticationFailure() {
        return authenticationFailure;
    }

    public void setAuthenticationFailure(boolean authenticationFailure) {
        this.authenticationFailure = authenticationFailure;
    }
}
