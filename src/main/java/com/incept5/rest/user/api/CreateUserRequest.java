package com.incept5.rest.user.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

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

    @NotNull
    @Valid
    public ExternalUser getUser() {
        return user;
    }

    public void setUser(ExternalUser user) {
        this.user = user;
    }

    @NotNull
    @Valid
    public PasswordRequest getPassword() {
        return password;
    }

    public void setPassword(PasswordRequest password) {
        this.password = password;
    }

}
