package com.incept5.rest.filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.incept5.rest.authorization.AuthorizationRequest;
import com.incept5.rest.authorization.AuthorizationService;
import com.incept5.rest.authorization.impl.SecurityContextImpl;
import com.incept5.rest.config.ApplicationConfig;
import com.incept5.rest.user.api.ExternalUser;
import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.exception.AuthorizationException;
import com.incept5.rest.user.repository.UserRepository;
import com.incept5.rest.util.DateUtil;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A Servlet filter class for authorizing requests.
 *
 * Any Resource that requires a role must have a header property of the following format:
 * <p/>
 * <code>
 * Authorization: <uuid of user>:<session token hash>
 * </code>
 * <p/>
 * The session token hash is comprised of the session token + : +  the relative url + , + the Http method + , + Date + , + nonce
 * This string is then Sha-256 encoded and then Base64 encoded
 * <p/>
 * An example:
 * <code>
 * Example:
 * 9fbc6f9a-af1b-4767-a492-c8462fd2a4d9:user/2e2ce9e8-798e-42b6-9326-fd2e56aef7aa/cards,POST,2012-06-30T12:00:00+01:00,34e321a7c4
 * <p/>
 * </code>
 * <p/>
 * This will be SHA-256 hashed and then Base64 encoded to produce:
 * <p/>
 * <code>
 * HR/3DJp8RCGo50Wu+/3cr7ibdoNXKg1eYMt3HO5QoP4=
 * </code>
 * <p/>
 * Authorization: 2e2ce9e8-798e-42b6-9326-fd2e56aef7aa:HR/3DJp8RCGo50Wu+/3cr7ibdoNXKg1eYMt3HO5QoP4=
 *
 * The role of this filter class is to set a {@link javax.ws.rs.core.SecurityContext} in the {@link com.sun.jersey.spi.container.ContainerRequest}
 *
 * @see {@link com.incept5.rest.authorization.impl.SecurityContextImpl}
 *
 * @author: Iain Porter
 */
@Component
@Provider
public class SecurityContextFilter implements ResourceFilter, ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityContextFilter.class);

    private static final int NONCE_CHECK_TOLERANCE_IN_MILLIS = 20;

    private static final int NONCE_CACHE_SIZE = 10000;

    protected static final String HEADER_AUTHORIZATION = "Authorization";

    protected static final String HEADER_DATE = "x-java-rest-date";

    protected static final String HEADER_NONCE = "nonce";

    private final UserRepository userRepository;

    private final AuthorizationService authorizationService;

    private LoadingCache<String, Nonce> nonceCache;

    ApplicationConfig config;

    @Autowired
    public SecurityContextFilter(UserRepository userRepository, AuthorizationService authorizationService, ApplicationConfig config) {
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.config = config;
        initNonceCache();
    }

    private void initNonceCache() {
       nonceCache = CacheBuilder.newBuilder()
       .maximumSize(NONCE_CACHE_SIZE)
       .expireAfterWrite(config.getSessionDateOffsetInMinutes(), TimeUnit.MINUTES)
       .build(
           new CacheLoader<String, Nonce>() {
             public Nonce load(String key) throws Exception {
               return new Nonce(new DateTime(), key);
             }
           });

    }

    /**
     * If there is an Authorisation header in the request extract the session token and retrieve the user
     *
     * Delegate to the AuthorizationService to validate the request
     *
     * If the request has a valid session token and the user is validated then a user object will be added to the security context
     *
     * Any Resource Controllers can assume the user has been validated and can merely authorize based on the role
     *
     * Resources with @PermitAll annotation do not require an Authorization header but will still be filtered
     *
     * @param request the ContainerRequest to filter
     *
     * @return the ContainerRequest with a SecurityContext added
     */
    public ContainerRequest filter(ContainerRequest request) {
        //find the Authorization header.
        String authToken = request.getHeaderValue(HEADER_AUTHORIZATION);
        String requestDateString = request.getHeaderValue(HEADER_DATE);
        String nonce = request.getHeaderValue(HEADER_NONCE);
        ExternalUser externalUser = null;
        if (authToken != null && requestDateString != null && nonce != null) {
            //make sure date and nonce is valid
            validateRequestDate(requestDateString);
            validateNonce(nonce);
            String[] token = authToken.split(":");
            if (token.length == 2) {
                String userId = token[0];
                String hashedToken = token[1];
                User user = null;
                user = userRepository.findByUuid(userId);
                if (user != null) {
                    externalUser = new ExternalUser(user);
                    AuthorizationRequest authRequest = new AuthorizationRequest(externalUser, request.getPath(), request.getMethod(),
                            requestDateString, hashedToken, nonce);
                    authorizationService.isAuthorized(authRequest);
                }
            }
        }
        request.setSecurityContext(new SecurityContextImpl(externalUser));
        return request;
    }

    private void validateRequestDate(String requestDateString) {
        Date date = DateUtil.getDateFromIso8061DateString(requestDateString);
        DateTime now = new DateTime();
        DateTime offset = new DateTime(date);
        if(!(offset.isAfter(now.minusMinutes(config.getSessionDateOffsetInMinutes())) &&
                offset.isBefore(now.plusMinutes(config.getSessionDateOffsetInMinutes())))) {
              LOG.error("Date in header is out of range: {}", requestDateString);
              throw new AuthorizationException("Date in header is out of range: " + requestDateString);
        }
    }

    private void validateNonce(String nonceValue) {
        try {
            Nonce nonce = nonceCache.getUnchecked(nonceValue);
            Duration tolerance = new Duration(nonce.timestamp, new DateTime());
            Long size = nonceCache.size();
            if(tolerance.isLongerThan(Duration.millis(NONCE_CHECK_TOLERANCE_IN_MILLIS))) {
                LOG.error("Nonce value was not unique: {}", nonceValue);
                throw new AuthorizationException("Nonce value is not unique");
            }
        } catch(Exception e) {
            LOG.error("Error getting nonce from cache", e);
            throw new AuthorizationException("Error getting nonce");
        }
    }


    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    @Autowired
    public void setConfig(ApplicationConfig config) {
        this.config = config;
    }

    private static class Nonce {
        private DateTime timestamp;
        private String nonceValue;

        Nonce(DateTime time, String nonce) {
            this.timestamp = time;
            this.nonceValue = nonce;
        }
    }
}
