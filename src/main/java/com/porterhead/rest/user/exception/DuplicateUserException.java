package com.porterhead.rest.user.exception;

import com.porterhead.rest.exception.BaseWebApplicationException;

/**
 * User: porter
 * Date: 12/03/2012
 * Time: 15:10
 */
public class DuplicateUserException extends BaseWebApplicationException {

    public DuplicateUserException() {
        super(409, "40901", "User already exists", "An attempt was made to create a user that already exists");
    }
}
