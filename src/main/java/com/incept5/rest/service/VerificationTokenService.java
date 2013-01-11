package com.incept5.rest.service;

import com.incept5.rest.model.VerificationToken;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 10/09/2012
 */
public interface VerificationTokenService {

    public VerificationToken sendEmailVerificationToken(String userId);

    public VerificationToken sendEmailRegistrationToken(String userId);

    public VerificationToken sendLostPasswordToken(String emailAddress);

    public VerificationToken verify(String base64EncodedToken);

    public VerificationToken generateEmailVerificationToken(String emailAddress);

    public VerificationToken resetPassword(String base64EncodedToken, String password);
}
