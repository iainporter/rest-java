package com.sample.web.api;

import com.sample.web.service.exception.ValidationException;

import javax.xml.bind.annotation.XmlRootElement;

import static com.sample.web.util.StringUtil.maxLength;
import static com.sample.web.util.StringUtil.minLength;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 28/09/2012
 */
@XmlRootElement
public class PasswordRequest {

    private String password;

    public PasswordRequest() {}

    public PasswordRequest(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void validate() {
        try {
            minLength(password, 8);
            maxLength(password, 35);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException();
        }

    }
}
