package com.incept5.rest.api;

import com.incept5.rest.util.StringUtil;

import javax.xml.bind.annotation.XmlRootElement;

import static com.incept5.rest.util.StringUtil.maxLength;
import static com.incept5.rest.util.StringUtil.minLength;


/**
 *
 * @author: Iain Porter
 */
@XmlRootElement
public class LoginRequest {

    private String username;

    private String password;

    public LoginRequest(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean validate() {
        try {
            StringUtil.validEmail(username);
            minLength(password, 8);
            maxLength(password, 35);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}
