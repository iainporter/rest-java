package com.incept5.rest.user;

import com.incept5.rest.user.api.LostPasswordRequest;
import com.incept5.rest.user.api.PasswordRequest;
import com.incept5.rest.user.domain.VerificationToken;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 10/09/2012
 */
public interface VerificationTokenService {

    public VerificationToken sendEmailVerificationToken(String userId);

    public VerificationToken sendEmailRegistrationToken(String userId);

    public VerificationToken sendLostPasswordToken(LostPasswordRequest lostPasswordRequest);

    public VerificationToken verify(String base64EncodedToken);

    public VerificationToken generateEmailVerificationToken(String emailAddress);

    public VerificationToken resetPassword(String base64EncodedToken, PasswordRequest passwordRequest);
}
