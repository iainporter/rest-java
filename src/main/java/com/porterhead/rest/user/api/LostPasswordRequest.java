package com.porterhead.rest.user.api;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 26/09/2012
 */
@XmlRootElement
public class LostPasswordRequest {

    @NotNull
    private String emailAddress;

    public LostPasswordRequest() {}

    public LostPasswordRequest(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
