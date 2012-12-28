package com.sample.web.service.impl;


import com.sample.web.api.CreateUserRequest;
import com.sample.web.api.LoginRequest;
import com.sample.web.api.UpdateUserRequest;
import com.sample.web.model.Role;
import com.sample.web.model.User;
import com.sample.web.service.UserService;
import com.sample.web.service.exception.AuthenticationException;
import com.sample.web.service.exception.AuthorizationException;
import com.sample.web.service.exception.DuplicateUserException;
import com.sample.web.service.exception.ValidationException;
import com.sample.web.social.JpaUsersConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Service for managing User accounts and Cards
 *
 * @author: Iain Porter
 */
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl implements UserService {

    /**
     * For Social API handling
     */
    private final UsersConnectionRepository jpaUsersConnectionRepository;


    @Autowired
    public UserServiceImpl(UsersConnectionRepository usersConnectionRepository) {
        this.jpaUsersConnectionRepository = usersConnectionRepository;
        ((JpaUsersConnectionRepository)this.jpaUsersConnectionRepository).setUserService(this);
    }

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * {@inheritDoc}
     *
     * This method creates a User with the given Role. A check is made to see if the username already exists and a duplication
     * check is made on the email address if it is present in the request.
     * <P></P>
     * The password is hashed and a SessionToken generated for subsequent authorization of role-protected requests.
     *
     */
    @Transactional
    public User createUser(CreateUserRequest request, Role role) {
        if (!request.validate()) {
            throw new ValidationException();
        }
        User searchedForUser = userRepository.findByEmailAddress(request.getUser().getEmailAddress());
        if (searchedForUser != null) {
            throw new DuplicateUserException();
        }

        User savedUser = createNewUser(request, role);
        return savedUser;
    }

    @Transactional
    public User createUser(Role role) {
        User user = new User();
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    /**
     * {@inheritDoc}
     *
     *  Login supports authentication against a username or email attribute.
     *  If a User is retrieved that matches against one of those attributes the password in the request is hashed
     *  and compared to the persisted password for the User account.
     */
    @Transactional
    public User login(LoginRequest request) {
        if (!request.validate()) {
            throw new ValidationException();
        }
        User user = null;
        user = userRepository.findByEmailAddress(request.getUsername());
        if (user == null) {
            throw new AuthenticationException();
        }
        if (user.hashPassword(request.getPassword()).equals(user.getHashedPassword())) {
            user.addSessionToken();
            return user;
        } else {
            throw new AuthenticationException();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Associate a Connection with a User account. If one does not exist a new User is created and linked to the
     * {@link com.sample.web.model.SocialUser} represented in the Connection details.
     *
     * <P></P>
     *
     * A SessionToken is generated and any Profile data that can be collected from the Social account is propagated to the User object.
     *
     */
    @Transactional
    public User socialLogin(Connection<?> connection) {

        List<String> userUuids = jpaUsersConnectionRepository.findUserIdsWithConnection(connection);
        if(userUuids.size() == 0) {
            throw new AuthenticationException();
        }
        User user = userRepository.findByUuid(userUuids.get(0)); //take the first one if there are multiple userIds for this provider Connection
        if (user == null) {
            throw new AuthenticationException();
        }
        updateUserFromProfile(connection, user);
        return user;

    }

    /**
     * Allow user to get their own profile or a user with merchant role to get any profile
     *
     * @param requestingUser
     * @param userIdentifier
     * @return user
     */
    @Transactional
    public User getUser(User requestingUser, String userIdentifier) {
        Assert.notNull(requestingUser);
        Assert.notNull(userIdentifier);
        User user = ensureUserIsLoaded(userIdentifier);
        if(!requestingUser.getUuid().equals(user.getUuid()) && !requestingUser.hasRole(Role.merchant))  {
           throw new AuthorizationException("User not authorized to load profile");
        }
        return user;
    }


    @Transactional
    public void deleteUser(User userMakingRequest, String userId) {
        Assert.notNull(userMakingRequest);
        Assert.notNull(userId);
        User userToDelete = ensureUserIsLoaded(userId);
        if (userMakingRequest.hasRole(Role.admin) && (userToDelete.hasRole(Role.anonymous) || userToDelete.hasRole(Role.authenticated))) {
            userRepository.delete(userToDelete);
        } else {
            throw new AuthorizationException("User cannot be deleted. Only users with anonymous or authenticated role can be deleted.");
        }
    }

    @Transactional
    public User saveUser(String userId, UpdateUserRequest request) {
        if (!request.validate()) {
            throw new ValidationException();
        }
        User user = ensureUserIsLoaded(userId);
        if(request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if(request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if(request.getEmailAddress() != null) {
            if(!request.getEmailAddress().equals(user.getEmailAddress())) {
                user.setEmailAddress(request.getEmailAddress());
                user.setVerified(false);
            }
        }
        userRepository.save(user);
        return user;
    }


    private User createNewUser(CreateUserRequest request, Role role) {
        User userToSave = new User(request.getUser());
        userToSave.setHashedPassword(userToSave.hashPassword(request.getPassword().getPassword()));
        userToSave.setRole(role);
        return userRepository.save(userToSave);
    }

    private void updateUserFromProfile(Connection<?> connection, User user) {
        UserProfile profile = connection.fetchUserProfile();
        user.setEmailAddress(profile.getEmail());
        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());
        //users logging in from social network are already verified
        user.setVerified(true);
        if(user.hasRole(Role.anonymous)) {
            user.setRole(Role.authenticated);
        }
        userRepository.save(user);
    }

}
