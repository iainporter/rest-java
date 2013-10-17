package com.porterhead.rest.user.api;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@porterhead.com
 * @since 09/11/2012
 */
public class LoginRequestTest extends ValidationTst {

    @Test
    public void validRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.randomAlphabetic(8) + "@example.com");
        request.setPassword(RandomStringUtils.randomAlphanumeric(8));
        Set<ConstraintViolation<LoginRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(0));
    }

    @Test
    public void invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.randomAlphanumeric(8) + "@example.com");
        request.setPassword(RandomStringUtils.randomAlphanumeric(7));
        Set<ConstraintViolation<LoginRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(1));
    }
}
