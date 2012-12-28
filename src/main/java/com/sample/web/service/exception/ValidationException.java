package com.sample.web.service.exception;

/**
 * User: porter
 * Date: 03/05/2012
 * Time: 21:43
 */
public class ValidationException extends BaseWebApplicationException {

    public ValidationException() {
        super(400, "40001", "Validation Error", "The data passed in the request was invalid. Please check and resubmit");
    }

}
