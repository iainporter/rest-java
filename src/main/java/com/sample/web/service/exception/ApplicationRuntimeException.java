package com.sample.web.service.exception;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 26/10/2012
 */
public class ApplicationRuntimeException extends BaseWebApplicationException {

    public ApplicationRuntimeException(String applicationMessage) {
        super(500, "50002", "Internal System error", applicationMessage);
    }
}
