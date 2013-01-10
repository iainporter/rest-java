package com.incept5.rest.service;

import com.incept5.rest.api.CreateUserRequest;
import com.incept5.rest.api.LoginRequest;
import com.incept5.rest.api.UpdateUserRequest;
import com.incept5.rest.model.Role;
import com.incept5.rest.model.User;
import org.springframework.social.connect.Connection;

/**
 * @author: Iain Porter
 *
 * Service to manage users
 */
public interface UserService {


    /**
     * Create a new User with the given role
     *
     * @param request
     * @param role
     * @return User
     */
    public User createUser(CreateUserRequest request, Role role);


    /**
     * Create a Default User with a given role
     *
     * @param role
     * @return User
     */
    public User createUser(Role role);

    /**
     * Login a User
     *
     * @param request
     * @return AuthenticatedUserToken
     */
    public User login(LoginRequest request);

    /**
     * Log in a User using Connection details from an authorized request from the User's supported Social provider
     * encapsulated in the {@link org.springframework.social.connect.Connection} parameter
     *
     * @param connection containing the details of the authorized user account form the Social provider
     * @return the User account linked to the {@link com.incept5.rest.model.SocialUser} account
     */
    public User socialLogin(Connection<?> connection);

    /**
     * Get a User based on a unique identifier
     *
     * Identifiers supported are uuid, emailAddress and anonymousToken
     *
     * @param userIdentifier
     * @return  User
     */
    public User getUser(User requestingUser, String userIdentifier);

    /**
     * Delete user, only authenticated user accounts can be deleted
     *
     * @param userMakingRequest the user authorized to delete the user
     * @param userId the id of the user to delete
     */
    public void deleteUser(User userMakingRequest, String userId);

    /**
     * Save User
     *
     * @param userId
     * @param request
     */
    public User saveUser(String userId, UpdateUserRequest request);

}
