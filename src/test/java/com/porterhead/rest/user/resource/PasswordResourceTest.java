package com.porterhead.rest.user.resource;

import com.porterhead.rest.resource.BaseResourceTst;
import com.porterhead.rest.resource.ConsumerSimpleSecurityFilter;
import com.porterhead.rest.user.api.LostPasswordRequest;
import com.porterhead.rest.user.api.PasswordRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;

import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 24/10/2012
 */
public class PasswordResourceTest extends BaseResourceTst {

    public PasswordResourceTest() {
            super(new WebAppDescriptor.Builder()
                    .contextPath("spring")
                    .contextParam("contextConfigLocation", "classpath:integration-test-context.xml")
                    .contextParam("spring.profiles.active", "dev")
                    .servletClass(SpringServlet.class)
                    .contextListenerClass(ContextLoaderListener.class)
                    .initParam(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, ConsumerSimpleSecurityFilter.class.getName())
                    .build());
        }

    @Test
    public void sendEmailToken() {
        ClientResponse response = super.resource().path("password/tokens").entity(createLostPasswordRequest(TEST_USER.getEmailAddress()), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void resetPassword() {
       ClientResponse response = super.resource().path("password/tokens/" + UUID.randomUUID().toString()).entity(createPasswordRequest("abcd1234"), APPLICATION_JSON).accept(APPLICATION_JSON).post(ClientResponse.class);
       assertThat(response.getStatus(), is(200));
    }

    private LostPasswordRequest createLostPasswordRequest(String emailAddress) {
        LostPasswordRequest request = new LostPasswordRequest();
        request.setEmailAddress(emailAddress);
        return request;
    }

    private PasswordRequest createPasswordRequest(String password) {
        PasswordRequest request = new PasswordRequest();
        request.setPassword(password);
        return request;
    }

}
