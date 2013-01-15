package com.incept5.rest.user.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter
 * @since 28/12/2012
 */
@Entity
@Table(name="rest_session_token")
public class SessionToken extends AbstractPersistable<Long> implements Comparable<SessionToken>{

    @Column(length=36)
    private String token;

    private Date timeCreated;

    private Date lastUpdated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public SessionToken() {}

    public SessionToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.timeCreated = new Date();
        this.lastUpdated = new Date();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public int compareTo(SessionToken userSession) {
        return this.lastUpdated.compareTo(userSession.getLastUpdated());
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
