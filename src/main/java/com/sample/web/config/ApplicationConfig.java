package com.sample.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * User: porter
 * Date: 17/05/2012
 * Time: 19:07
 */
@Configuration
@PropertySource({"classpath:/properties/app.properties"})
public class ApplicationConfig {

    private final static String HOSTNAME_PROPERTY = "hostNameUrl";

    @Autowired
    protected Environment environment;

    public String getHostNameUrl() {
        return environment.getProperty(HOSTNAME_PROPERTY);
    }

    public String getFacebookClientId() {
        return environment.getProperty("facebook.clientId");
    }

    public String getFacebookClientSecret() {
        return environment.getProperty("facebook.clientSecret");
    }

}
