package com.incept5.rest.service.impl;

import com.incept5.rest.model.User;
import com.incept5.rest.repository.UserRepository;
import com.incept5.rest.service.exception.UserNotFoundException;
import com.incept5.rest.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: porter
 * Date: 05/04/2012
 * Time: 17:22
 *
 */
public class BaseServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(BaseServiceImpl.class);

    protected UserRepository userRepository;

    protected User ensureUserIsLoaded(String userIdentifier) {
        User user = null;
        if (StringUtil.isValidUuid(userIdentifier)) {
            user = userRepository.findByUuid(userIdentifier);
        } else {
            user = userRepository.findByEmailAddress(userIdentifier);
        }
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
