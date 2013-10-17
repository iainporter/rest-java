package com.porterhead.rest.user;

import com.porterhead.rest.user.domain.User;
import com.porterhead.rest.user.domain.VerificationToken;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 13/09/2012
 */
public class EmailServiceTokenModel implements Serializable {

    private final String emailAddress;
    private final String token;
    private final VerificationToken.VerificationTokenType tokenType;
    private final String hostNameUrl;


    public EmailServiceTokenModel(User user, VerificationToken token, String hostNameUrl)  {
        this.emailAddress = user.getEmailAddress();
        this.token = token.getToken();
        this.tokenType = token.getTokenType();
        this.hostNameUrl = hostNameUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEncodedToken() {
        return new String(Base64.encodeBase64(token.getBytes()));
    }

    public String getToken() {
        return token;
    }

    public VerificationToken.VerificationTokenType getTokenType() {
        return tokenType;
    }

    public String getHostNameUrl() {
        return hostNameUrl;
    }
}
