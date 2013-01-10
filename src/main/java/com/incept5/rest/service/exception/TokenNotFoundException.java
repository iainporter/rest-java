package com.incept5.rest.service.exception;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 14/09/2012
 */
public class TokenNotFoundException extends BaseWebApplicationException {

    public TokenNotFoundException() {
        super(404, "40407", "Token Not Found", "No token could be found for that Id");
    }
}
