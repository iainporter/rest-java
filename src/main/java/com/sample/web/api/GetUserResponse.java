package com.sample.web.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 13/09/2012
 */
@XmlRootElement
public class GetUserResponse {

    private ExternalUser user;

    public GetUserResponse() {}

    public ExternalUser getUser() {
        return user;
    }

    public void setUser(ExternalUser user) {
        this.user = user;
    }
}
