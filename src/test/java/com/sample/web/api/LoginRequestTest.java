package com.sample.web.api;

import com.sample.web.api.LoginRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 09/11/2012
 */
public class LoginRequestTest {

    @Test
    public void validRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.random(8) + "@example.com");
        request.setPassword(RandomStringUtils.random(8));
        assertTrue(request.validate());
    }

    @Test
    public void spaceInEmailAddress() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.random(8) + " @example.com");
        request.setPassword(RandomStringUtils.random(8));
        assertFalse(request.validate());
    }

    @Test
    public void invalidEmailAddress() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.random(8));
        request.setPassword(RandomStringUtils.random(8));
        assertFalse(request.validate());
    }

    @Test
    public void invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername(RandomStringUtils.random(8) + "@example.com");
        request.setPassword(RandomStringUtils.random(7));
        assertFalse(request.validate());
    }
}
