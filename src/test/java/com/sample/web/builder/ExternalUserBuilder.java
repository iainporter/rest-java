package com.sample.web.builder;

import com.sample.web.api.ExternalUser;

/**
 * Created by IntelliJ IDEA.
 * User: porter
 * Date: 12/03/2012
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
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
