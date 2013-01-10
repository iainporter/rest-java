package com.incept5.rest.service;

import com.incept5.rest.service.data.EmailServiceTokenModel;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 13/09/2012
 */
public interface MailSenderService {

    public EmailServiceTokenModel sendVerificationEmail(EmailServiceTokenModel emailServiceTokenModel);

    public EmailServiceTokenModel sendRegistrationEmail(EmailServiceTokenModel emailServiceTokenModel);

    public EmailServiceTokenModel sendLostPasswordEmail(EmailServiceTokenModel emailServiceTokenModel);


}
