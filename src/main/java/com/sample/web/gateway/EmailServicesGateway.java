package com.sample.web.gateway;

import com.sample.web.service.data.EmailServiceTokenModel;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 11/09/2012
 */
public interface EmailServicesGateway {

    public void sendVerificationToken(EmailServiceTokenModel model);
}
