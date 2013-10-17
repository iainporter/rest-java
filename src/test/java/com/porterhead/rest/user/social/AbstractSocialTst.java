package com.porterhead.rest.user.social;

import com.porterhead.rest.user.SocialUserRepository;
import com.porterhead.rest.user.UserRepository;
import com.porterhead.rest.user.domain.SocialUser;
import com.porterhead.rest.user.domain.User;
import org.junit.Before;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.social.facebook.api.Facebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: porter
 * Date: 22/05/2012
 * Time: 13:28
 */
public abstract class AbstractSocialTst {

    protected static final String PROVIDER_ID = "facebook";
    protected static final String PROVIDER_USER_ID = "123456";
    protected static final String ACCESS_TOKEN = "1234567890";

    protected Connection<Facebook> connection;
    protected SocialUserRepository socialUserRepository;
    protected UserRepository userRepository;
    protected User user;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected ConnectionFactory connectionFactory;
    protected TextEncryptor textEncryptor = Encryptors.noOpText();
    protected SocialUser socialUser;
    protected List<SocialUser> socialUsers;
    protected Set<String> providerIds = new HashSet<String>();

    @Before
    public final void setMocks() {
        mockUserRepository();
        mockSocialUsers();
        mockConnectionData();
        setUpRepository();

    }

    public abstract void setUpRepository();

    private void mockUserRepository() {
        user = new User();
        userRepository = mock(UserRepository.class);
        when(userRepository.findByUuid(any(String.class))).thenReturn(user);
    }

    private void mockSocialUsers() {
        socialUser = new SocialUser();
        socialUser.setUser(user);
        socialUser.setAccessToken(ACCESS_TOKEN);
        socialUser.setProviderId(PROVIDER_ID);
        socialUser.setProviderUserId(PROVIDER_USER_ID);
        socialUser.setDisplayName("Test User");
        socialUser.setRank(1);
        socialUsers = new ArrayList<SocialUser>();
        socialUsers.add(socialUser);
        socialUserRepository = mock(SocialUserRepository.class);

    }

    private void mockConnectionData() {
        providerIds.add(PROVIDER_ID);
        connectionFactoryLocator = mock(ConnectionFactoryLocator.class);
        connection = mock(Connection.class);
        connectionFactory = mock(ConnectionFactory.class);
        when(connection.getKey()).thenReturn(new ConnectionKey(PROVIDER_ID, PROVIDER_USER_ID));
        when(connectionFactoryLocator.registeredProviderIds()).thenReturn(providerIds);
        ConnectionData data = new ConnectionData(PROVIDER_ID, PROVIDER_USER_ID, "Test User", null, null, ACCESS_TOKEN, null, null, null);
        when(connection.createData()).thenReturn(data);
        when(connectionFactoryLocator.getConnectionFactory(any(String.class))).thenReturn(connectionFactory);
        when(connectionFactory.createConnection(any(ConnectionData.class))).thenReturn(connection);
    }

}
