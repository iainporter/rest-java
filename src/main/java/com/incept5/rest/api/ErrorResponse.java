package com.incept5.rest.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 19/10/2012
 */
@XmlRootElement
public class ErrorResponse {

    private String errorCode;
    private String consumerMessage;
    private String applicationMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getConsumerMessage() {
        return consumerMessage;
    }

    public void setConsumerMessage(String consumerMessage) {
        this.consumerMessage = consumerMessage;
    }

    public String getApplicationMessage() {
        return applicationMessage;
    }

    public void setApplicationMessage(String applicationMessage) {
        this.applicationMessage = applicationMessage;
    }
}
