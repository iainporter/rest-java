package com.porterhead.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 21/09/2012
 */
@Configuration
@Profile(value="production")
@PropertySource({"classpath:/properties/production-app.properties"})
public class ApplicationProductionConfig {

    @Autowired
    Environment environment;

      @Bean
        public TextEncryptor textEncryptor() {
            return Encryptors.queryableText(environment.getProperty("security.encryptPassword"),
                    environment.getProperty("security.encryptSalt"));
        }
}
