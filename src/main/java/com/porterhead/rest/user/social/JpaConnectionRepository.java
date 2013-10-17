package com.porterhead.rest.user.social;

import com.porterhead.rest.user.SocialUserRepository;
import com.porterhead.rest.user.UserRepository;
import com.porterhead.rest.user.domain.SocialUser;
import com.porterhead.rest.user.domain.SocialUserBuilder;
import com.porterhead.rest.user.domain.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * User: porter
 * Date: 15/05/2012
 * Time: 16:13
 */
public class JpaConnectionRepository implements ConnectionRepository {

    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final User user;
    private final ConnectionFactoryLocator connectionFactoryLocator;
	private final TextEncryptor textEncryptor;

    public JpaConnectionRepository(SocialUserRepository socialUserRepository, UserRepository userRepository, User user, ConnectionFactoryLocator locator, TextEncryptor encryptor) {
        this.socialUserRepository = socialUserRepository;
        this.userRepository = userRepository;
        this.user = user;
        this.connectionFactoryLocator = locator;
        this.textEncryptor = encryptor;
    }

	public MultiValueMap<String, Connection<?>> findAllConnections() {
		List<Connection<?>> resultList = connectionMapper.mapEntities(socialUserRepository.findAllByUser(user));

		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
		}
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	public List<Connection<?>> findConnections(String providerId) {
		return connectionMapper.mapEntities(socialUserRepository.findByUserAndProviderId(user, providerId));
	}

	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}

		List<Connection<?>> resultList = connectionMapper.mapEntities(socialUserRepository.findByUserAndProviderUserId(user, providerUsers));

		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<Connection<?>>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;
	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {
			return connectionMapper.mapEntity(socialUserRepository.findByUserAndProviderIdAndProviderUserId(user, connectionKey.getProviderId(), connectionKey.getProviderUserId()));
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Transactional
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();
             //TODO: currently only support 1 connection per user per provider (rank = 1)
            int rank = 1;
            //create a SocialUser and call save
            SocialUser socialUser = SocialUserBuilder.create().withUser(user).withProviderId(data.getProviderId())
                    .withProviderUserId(data.getProviderUserId()).withRank(rank).withDisplayName(data.getDisplayName())
                    .withProfileUrl(data.getProfileUrl()).withImageUrl(data.getImageUrl()).withAccessToken(encrypt(data.getAccessToken()))
                            .withSecret(encrypt(data.getSecret())).withRefreshToken(encrypt(data.getRefreshToken()))
                            .withExpireTime(data.getExpireTime()).build();
			socialUserRepository.save(socialUser);
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}

	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();

		SocialUser socialUser = socialUserRepository.findByUserAndProviderIdAndProviderUserId(user, data.getProviderId(), data.getProviderUserId());
		if(socialUser != null){
			socialUser.setDisplayName(data.getDisplayName());
			socialUser.setProfileUrl(data.getProfileUrl());
			socialUser.setImageUrl(data.getImageUrl());
			socialUser.setAccessToken(encrypt(data.getAccessToken()));
			socialUser.setSecret(encrypt(data.getSecret()));
			socialUser.setRefreshToken(encrypt(data.getRefreshToken()));
			socialUser.setExpireTime(data.getExpireTime());

			socialUser = socialUserRepository.save(socialUser);
		}
	}

	public void removeConnections(String providerId) {
        List<SocialUser> users = socialUserRepository.findByUserAndProviderId(user, providerId);
		socialUserRepository.delete(users);
	}

	public void removeConnection(ConnectionKey connectionKey) {
        SocialUser socialUser = socialUserRepository.findByUserAndProviderIdAndProviderUserId(user, connectionKey.getProviderId(), connectionKey.getProviderUserId());
        socialUserRepository.delete(socialUser);
	}

	private Connection<?> findPrimaryConnection(String providerId) {
		List<Connection<?>> connections = connectionMapper.mapEntities(socialUserRepository.findByUserAndProviderId(user, providerId));
		if (connections.size() > 0) {
			return connections.get(0);
		} else {
			return null;
		}
	}

	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();

	private final class ServiceProviderConnectionMapper  {

		public List<Connection<?>> mapEntities(List<SocialUser> socialUsers){
			List<Connection<?>> result = new ArrayList<Connection<?>>();
			for(SocialUser user : socialUsers){
				result.add(mapEntity(user));
			}
			return result;
		}

		public Connection<?> mapEntity(SocialUser socialUser){
			ConnectionData connectionData = mapConnectionData(socialUser);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}

		private ConnectionData mapConnectionData(SocialUser socialUser){
			return new ConnectionData(socialUser.getProviderId(), socialUser.getProviderUserId(), socialUser.getDisplayName(), socialUser.getProfileUrl(), socialUser.getImageUrl(),
					decrypt(socialUser.getAccessToken()), decrypt(socialUser.getSecret()), decrypt(socialUser.getRefreshToken()), expireTime(socialUser.getExpireTime()));
		}

		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}

		private Long expireTime(Long expireTime) {
			return expireTime == null || expireTime == 0 ? null : expireTime;
		}

	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}
}
