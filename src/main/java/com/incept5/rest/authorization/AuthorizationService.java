package com.incept5.rest.authorization;

import com.incept5.rest.api.ExternalUser;
import com.incept5.rest.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Service to Authorize User requests
 *
 * @author: Iain Porter
 */
@Service("authorizationService")
public class AuthorizationService {

    Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private UserService userService;

    @Autowired
    public AuthorizationService(UserService userService) {
         this.userService = userService;
    }

    /**
     * Authorize a hashed token against a request string
     * The hashed token will be comprised of:
     * the User's session token + the relative request Url + the Http Verb + the Date as ISO 8061 String
     * <code>
     *     Example:
     *      9fbc6f9a-af1b-4767-a492-c8462fd2a4d9:user/2e2ce9e8-798e-42b6-9326-fd2e56aef7aa,GET,2012-06-30T12:00:00+01:00
     *
     * </code>
     *
     * This will be SHA-256 hashed and then Base64 encoded to produce:
     *
     * <code>
     *     UgphA7bAUVcH/RGhVZlNKBTykp0TtcUiPuG0xc71P3o=
     * </code>
     *
     * @param authorizationRequest the request containing all the details needed to authorize the request
     *
     * @return true if the token is authorized
     */
    public boolean isAuthorized(AuthorizationRequest authorizationRequest) {
        Assert.notNull(authorizationRequest.getUser());
        Assert.notNull(authorizationRequest.getHashedToken());
        String unEncodedString =  composeUnEncodedRequest(authorizationRequest);
        List<UserSession> sessionTokens = authorizationRequest.getUser().getSessions();
        String userTokenHash = null;
        for(UserSession token: sessionTokens) {
            userTokenHash = encodeAuthToken(token.getSessionToken(), unEncodedString);
            if(authorizationRequest.getHashedToken().equals(userTokenHash)) {
                authorizationRequest.getUser().setActiveSession(token);
                persistUser(authorizationRequest.getUser());
                return true;
            }
        }
        log.error("Hash check failed for hashed token: {} for the following request: {} for user: {}",
                    new Object[]{authorizationRequest.getHashedToken(), unEncodedString, authorizationRequest.getUser().getId()} );
        return false;
    }

    private void persistUser(ExternalUser user) {
        userService.saveUserSession(user);
    }

    /**
     * Encode the token by prefixing it with the User's Session Token
     *
     * @param token
     * @return encoded token
     */
    private String encodeAuthToken(String token, String unencodedRequest) {
        byte[] digest = DigestUtils.sha256(token + ":" + unencodedRequest);
        return new String(Base64.encodeBase64(digest));

    }

    private String composeUnEncodedRequest(AuthorizationRequest authRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(authRequest.getRequestUrl());
        sb.append(',');
        sb.append(authRequest.getHttpMethod().toUpperCase());
        sb.append(',');
        sb.append(authRequest.getRequestDateString());
        return sb.toString();

    }
}
