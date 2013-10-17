package com.porterhead.rest.user.api;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: porter
 * Date: 18/05/2012
 * Time: 09:59
 */
@XmlRootElement
public class OAuth2Request {

    private String accessToken;

    @NotNull
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
