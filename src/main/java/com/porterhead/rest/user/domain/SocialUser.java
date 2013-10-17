package com.porterhead.rest.user.domain;

import com.porterhead.rest.model.BaseEntity;

import javax.persistence.*;

/**
 * User: porter
 * Date: 15/05/2012
 * Time: 13:57
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "providerId", "providerUserId"}),
        @UniqueConstraint(columnNames = {"userId", "providerId", "rank"})})
public class SocialUser extends BaseEntity {

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "userId", nullable = false, updatable = false)
    private User user;

    private String providerId;

    private String providerUserId;

    private int rank;

    private String displayName;

    private String profileUrl;

    private String imageUrl;

    @Column(length = 500)
    private String accessToken;

    private String secret;

    private String refreshToken;

    private Long expireTime;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
