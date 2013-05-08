package com.incept5.rest.user.api;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 08/11/2012
 */
public class CreateUserRequestTest extends ValidationTst {


    @Test
    public void validRequest() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setFirstName("Bo");
        user.setLastName("Diddley");
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(0));
    }

    @Test
    public void nullUser() {
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(null, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(1));
    }

    @Test
    public void firstNameTooLong() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setFirstName(RandomStringUtils.random(101));
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(1));
    }

    @Test
    public void lastNameTooLong() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setLastName(RandomStringUtils.random(101));
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        Set<ConstraintViolation<CreateUserRequest>> constraints = validator.validate(createUserRequest);
        assertThat(constraints.size(), is(1));
    }


}

