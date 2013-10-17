package com.porterhead.rest.user.exception;

import com.porterhead.rest.exception.BaseWebApplicationException;

/**
 * User: porter
 * Date: 13/03/2012
 * Time: 08:58
 */
public class AuthenticationException extends BaseWebApplicationException {

    public AuthenticationException() {
        super(401, "40102", "Authentication Error", "Authentication Error. The username or password were incorrect");
    }


}
