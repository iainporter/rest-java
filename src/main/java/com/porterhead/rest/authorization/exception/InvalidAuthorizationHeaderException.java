package com.porterhead.rest.authorization.exception;

import com.porterhead.rest.exception.BaseWebApplicationException;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 20/10/2012
 */
public class InvalidAuthorizationHeaderException extends BaseWebApplicationException {


    public static final String DEVELOPER_MESSAGE = "Authorization failed. This could be due to missing properties in the header or" +
            " the Authorization header may have been incorrectly hashed";

    public InvalidAuthorizationHeaderException() {
        super(401, "40101", "Authorization failed", DEVELOPER_MESSAGE);
    }

}
