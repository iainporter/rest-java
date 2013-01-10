package com.incept5.rest.service;


import com.incept5.rest.api.CreateUserRequest;
import com.incept5.rest.api.ExternalUser;
import com.incept5.rest.api.PasswordRequest;
import com.incept5.rest.builder.ExternalUserBuilder;
import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.model.Role;
import com.incept5.rest.model.User;
import com.incept5.rest.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * User: porter
 * Date: 04/04/2012
 * Time: 14:21
 */
public class BaseServiceTest {

    @Autowired
    protected UserService userService;

    @Autowired
    UserRepository userRepository;


    @Autowired
    ApplicationConfig applicationConfig;


    protected User createUserWithRandomUserName(Role role) {
        CreateUserRequest request = getDefaultCreateUserRequest();
        return userService.createUser(request, role);
    }

    protected CreateUserRequest getDefaultCreateUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUser(getUser());
        request.setPassword(new PasswordRequest("password"));
        return request;
    }

    protected ExternalUser getUser() {
        ExternalUser user = ExternalUserBuilder.create().withFirstName("John")
                .withLastName("Smith")
                .withEmailAddress(createRandomEmailAddress())
                .build();
        return user;
    }

    protected String createRandomEmailAddress() {
        return RandomStringUtils.randomAlphabetic(8) + "@example.com";
    }

    protected String buildSuccessfulReceiptResponse() throws Exception {
        File receiptResponseFile = locateClasspathResource("data/receipt_response.txt");
        return readFileAsString(receiptResponseFile);
    }

    protected String buildFailureReceiptResponse() {
       return " {\"status\":\"21002\", \"exception\":\"java.lang.NullPointerException\"} ";
    }

    protected String build21007ReceiptResponse() {
       return " {\"status\":\"21007\", \"exception\":\"Sandbox Receipt\"} ";
    }

    protected File locateClasspathResource(String resourceName) {
        try {
            return new ClassPathResource(resourceName).getFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static String readFileAsString(File file) throws java.io.IOException {
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        bufferedInputStream.read(buffer);
        return new String(buffer);
    }
}
