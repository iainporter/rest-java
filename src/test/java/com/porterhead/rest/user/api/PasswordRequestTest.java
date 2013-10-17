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
 * @since 30/09/2012
 */
public class PasswordRequestTest extends ValidationTst {

    @Test
    public void validPassword() {
        PasswordRequest request = new PasswordRequest("password");
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(0));
    }

    public void passwordTooShort() {
        PasswordRequest request = new PasswordRequest(RandomStringUtils.randomAlphanumeric(7));
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(1));
    }

    public void passwordTooLong() {
        PasswordRequest request = new PasswordRequest(RandomStringUtils.randomAlphanumeric(36));
        Set<ConstraintViolation<PasswordRequest>> constraints = validator.validate(request);
        assertThat(constraints.size(), is(1));
    }
}
