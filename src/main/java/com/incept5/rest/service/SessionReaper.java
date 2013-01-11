package com.incept5.rest.service;

import com.incept5.rest.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 11/01/2013
 */
public class SessionReaper {

    Logger LOG = LoggerFactory.getLogger(SessionReaper.class);

    private UserService userService;

    ApplicationConfig config;

    public void cleanUpExpiredSessions() {
        Integer sessionCount = userService.deleteExpiredSessions(config.getSessionExpiryTimeInMinutes());
        LOG.debug("Session reaper has removed {} expired sessions", sessionCount);
    }

    @Autowired
    public void setConfig(ApplicationConfig config) {
        this.config = config;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
