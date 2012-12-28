package com.sample.web.api;

import javax.xml.bind.annotation.XmlRootElement;

import static com.sample.web.util.StringUtil.maxLength;
import static com.sample.web.util.StringUtil.validEmail;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 05/10/2012
 */
@XmlRootElement
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String emailAddress;

    public UpdateUserRequest(){}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public boolean validate() {
        try {
            validEmail(emailAddress);
            if(firstName != null) {
                maxLength(firstName, 100);
            }
            if(lastName != null) {
                maxLength(lastName, 100);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
