package com.incept5.rest.user.api;

import javax.xml.bind.annotation.XmlRootElement;

import static org.springframework.util.Assert.notNull;

/**
 * @author: Iain Porter
 */
@XmlRootElement
public class CreateUserRequest {

    private ExternalUser user;
    private PasswordRequest password;


    public CreateUserRequest() {
    }

    public CreateUserRequest(final ExternalUser user, final PasswordRequest password) {
        this.user = user;
        this.password = password;
    }


    public ExternalUser getUser() {
        return user;
    }

    public void setUser(ExternalUser user) {
        this.user = user;
    }

    public PasswordRequest getPassword() {
        return password;
    }

    public void setPassword(PasswordRequest password) {
        this.password = password;
    }

    public boolean validate() {
        try {
            password.validate();
            notNull(user);
            return user.validate();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
