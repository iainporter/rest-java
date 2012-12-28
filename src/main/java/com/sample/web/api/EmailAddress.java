package com.sample.web.api;

import javax.xml.bind.annotation.XmlRootElement;

import static com.sample.web.util.StringUtil.minLength;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 03/11/2012
 */
@XmlRootElement
public class EmailAddress {

    private String emailAddress;

    public EmailAddress() {
    }

    public EmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean validate() {
        try {
            minLength(emailAddress, 4);
            if (!emailAddress.contains("@")) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
