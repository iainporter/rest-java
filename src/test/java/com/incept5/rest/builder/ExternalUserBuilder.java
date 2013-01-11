package com.incept5.rest.builder;

import com.incept5.rest.api.ExternalUser;

/**
 * User: porter
 * Date: 12/03/2012
 * Time: 14:32
 */
public class ExternalUserBuilder {

    public static ExternalUserBuilder create() {
          return new ExternalUserBuilder();
      }


    private final ExternalUser user;

    public ExternalUserBuilder() {
        user = new ExternalUser();
    }

    public ExternalUser build() {
        return user;
    }

    public ExternalUserBuilder withFirstName(String name) {
        user.setFirstName(name);
        return this;
    }

    public ExternalUserBuilder withLastName(String name) {
        user.setLastName(name);
        return this;
    }

    public ExternalUserBuilder withEmailAddress(String email) {
        user.setEmailAddress(email);
        return this;
    }
}
