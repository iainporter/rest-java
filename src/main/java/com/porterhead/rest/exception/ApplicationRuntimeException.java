package com.porterhead.rest.exception;


public class ApplicationRuntimeException extends BaseWebApplicationException {

    public ApplicationRuntimeException(String applicationMessage) {
        super(500, "50002", "Internal System error", applicationMessage);
    }
}
