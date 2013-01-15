package com.incept5.rest.user.exception;

import com.incept5.rest.service.exception.BaseWebApplicationException;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 14/09/2012
 */
public class AlreadyVerifiedException extends BaseWebApplicationException {

    public AlreadyVerifiedException() {
        super(409, "40905", "Already verified", "The token has already been verified");
    }
}
