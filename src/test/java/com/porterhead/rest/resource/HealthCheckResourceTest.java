package com.porterhead.rest.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: porter
 * Date: 08/05/2012
 * Time: 09:29
 */
public class HealthCheckResourceTest extends BaseResourceTst {

    public HealthCheckResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextPath("spring")
                .servletClass(SpringServlet.class)
                .contextParam("contextConfigLocation", "classpath:integration-test-context.xml")
                .contextParam("spring.profiles.active", "dev")
                .contextListenerClass(ContextLoaderListener.class)
                .build());
    }

    @Test
    public void check() {
        ClientResponse response = super.resource().path("/healthcheck").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getEntity(String.class), is("Running version " + environment.getProperty("application.version")));
    }

}
