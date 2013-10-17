package com.porterhead.rest.exception;

import com.porterhead.rest.api.ErrorResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 19/10/2012
 */
public abstract class BaseWebApplicationException extends WebApplicationException {

    private final int status;
    private final String errorMessage;
    private final String errorCode;
    private final String developerMessage;

    public BaseWebApplicationException(int httpStatus, String errorCode, String errorMessage, String developerMessage) {
        this.status = httpStatus;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.developerMessage = developerMessage;
    }


    @Override
    public Response getResponse() {
        return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE).entity(getErrorResponse()).build();
    }

    public ErrorResponse getErrorResponse() {
        ErrorResponse response = new ErrorResponse();
        response.setErrorCode(errorCode);
        response.setApplicationMessage(developerMessage);
        response.setConsumerMessage(errorMessage);
        return response;
    }

}
