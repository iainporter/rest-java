package com.incept5.rest.user.service.impl;

import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.exception.UserNotFoundException;
import com.incept5.rest.user.repository.UserRepository;
import com.incept5.rest.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@c24.biz
 * @since 14/01/2013
 */
public class BaseUserServiceImpl {

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
