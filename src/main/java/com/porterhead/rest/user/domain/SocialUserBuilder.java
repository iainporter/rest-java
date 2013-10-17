package com.porterhead.rest.user.domain;

/**
 * User: porter
 * Date: 21/05/2012
 * Time: 08:12
 */
    public class SocialUserBuilder {

        SocialUser user;

        public static SocialUserBuilder create() {
            return new SocialUserBuilder();
        }

        public SocialUserBuilder() {
            user = new SocialUser();
        }

        public SocialUser build() {
            return user;
        }

        public SocialUserBuilder withUser(User user) {
            this.user.setUser(user);
            return this;
        }

        public SocialUserBuilder withProviderId(String id) {
            this.user.setProviderId(id);
            return this;
        }

        public SocialUserBuilder withProviderUserId(String id) {
            this.user.setProviderUserId(id);
            return this;
        }

        public SocialUserBuilder withRank(int rank) {
            this.user.setRank(rank);
            return this;
        }

        public SocialUserBuilder withDisplayName(String name) {
            this.user.setDisplayName(name);
            return this;
        }

        public SocialUserBuilder withProfileUrl(String url) {
            this.user.setProfileUrl(url);
            return this;
        }

        public SocialUserBuilder withImageUrl(String url) {
            this.user.setImageUrl(url);
            return this;
        }

        public SocialUserBuilder withAccessToken(String token) {
            this.user.setAccessToken(token);
            return this;
        }

        public SocialUserBuilder withSecret(String secret) {
            this.user.setSecret(secret);
            return this;
        }

        public SocialUserBuilder withRefreshToken(String token) {
            this.user.setRefreshToken(token);
            return this;
        }

        public SocialUserBuilder withExpireTime(Long time) {
            this.user.setExpireTime(time);
            return this;
        }
    }
