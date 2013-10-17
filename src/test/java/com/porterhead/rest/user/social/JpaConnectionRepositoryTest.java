package com.porterhead.rest.user.social;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * User: porter
 * Date: 22/05/2012
 * Time: 13:12
 */
public class JpaConnectionRepositoryTest extends AbstractSocialTst {

    private JpaConnectionRepository connectionRepository;

    @Before
    public void setUpRepository() {
        connectionRepository = new JpaConnectionRepository(socialUserRepository, userRepository, user, connectionFactoryLocator, textEncryptor);
    }

    @Test
    public void findAllConnections() {
       when(socialUserRepository.findAllByUser(user)).thenReturn(socialUsers);
       MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();
       assertThat(allConnections.size(), is(1));
    }


}
