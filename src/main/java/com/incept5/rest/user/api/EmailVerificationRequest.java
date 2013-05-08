package com.incept5.rest.user.api;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 15/09/2012
 */
@XmlRootElement
public class EmailVerificationRequest {

    private String emailAddress;

    public EmailVerificationRequest() {}

    public EmailVerificationRequest(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @NotNull
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
