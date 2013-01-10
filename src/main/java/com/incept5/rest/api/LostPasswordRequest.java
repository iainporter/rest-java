package com.incept5.rest.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 26/09/2012
 */
@XmlRootElement
public class LostPasswordRequest {

    private String emailAddress;

    public LostPasswordRequest() {}

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
