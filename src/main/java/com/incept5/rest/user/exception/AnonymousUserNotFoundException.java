package com.incept5.rest.user.exception;

import com.incept5.rest.exception.BaseWebApplicationException;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 15/10/2012
 */
public class AnonymousUserNotFoundException extends BaseWebApplicationException {

    public AnonymousUserNotFoundException() {
        super(404, "40405", "Anonymous User Not Found", "No user could be found matching the code. " +
                "Either that user does not exist or has already been claimed.");
    }
}
