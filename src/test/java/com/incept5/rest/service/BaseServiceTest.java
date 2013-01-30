package com.incept5.rest.service;


import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.user.api.AuthenticatedUserToken;
import com.incept5.rest.user.api.CreateUserRequest;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.api.PasswordRequest;
import com.incept5.rest.user.builder.ExternalUserBuilder;
import com.incept5.rest.user.domain.Role;
import com.incept5.rest.user.repository.UserRepository;
import com.incept5.rest.user.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: porter
 * Date: 04/04/2012
 * Time: 14:21
 */
public class BaseServiceTest {

    @Autowired
    public UserService userService;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ApplicationConfig applicationConfig;


    protected AuthenticatedUserToken createUserWithRandomUserName(Role role) {
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
}
