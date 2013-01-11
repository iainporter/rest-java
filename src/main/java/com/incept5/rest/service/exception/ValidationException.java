package com.incept5.rest.service.exception;

/**
 * User: porter
 * Date: 03/05/2012
 * Time: 21:43
 */
public class ValidationException extends BaseWebApplicationException {

    public ValidationException() {
        super(400, "40001", "Validation Error", "The data passed in the request was invalid. Please check and resubmit");
    }

    public ValidationException(String message) {
        super(400, "40001", "Validation Error", message);
    }

}
