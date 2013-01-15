package com.incept5.rest.user.api;

import com.incept5.rest.user.api.CreateUserRequest;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.api.PasswordRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 08/11/2012
 */
public class CreateUserRequestTest {

    @Test
    public void validRequest() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setFirstName("Bo");
        user.setLastName("Diddley");
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        assertTrue(createUserRequest.validate());
    }

    @Test
    public void invalidEmail() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "example.com");
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        assertFalse(createUserRequest.validate());
    }

    @Test
    public void nullUser() {
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(null, passwordRequest);
        assertFalse(createUserRequest.validate());
    }

    @Test
    public void firstNameTooLong() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setFirstName(RandomStringUtils.random(101));
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        assertFalse(createUserRequest.validate());
    }

    @Test
    public void lastNameTooLong() {
        ExternalUser user = new ExternalUser();
        user.setEmailAddress(RandomStringUtils.random(8) + "@example.com");
        user.setLastName(RandomStringUtils.random(101));
        PasswordRequest passwordRequest = new PasswordRequest(RandomStringUtils.random(10));
        CreateUserRequest createUserRequest = new CreateUserRequest(user, passwordRequest);
        assertFalse(createUserRequest.validate());
    }


}

