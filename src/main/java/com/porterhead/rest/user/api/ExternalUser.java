package com.porterhead.rest.user.api;

import com.porterhead.rest.user.domain.AuthorizationToken;
import com.porterhead.rest.user.domain.SocialUser;
import com.porterhead.rest.user.domain.User;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author: Iain Porter
 */
@XmlRootElement
public class ExternalUser implements Principal {

    private String id;

    @Length(max=50)
    private String firstName;

    @Length(max=50)
    private String lastName;

    @NotNull
    @Email
    private String emailAddress;

    private boolean isVerified;

    @JsonIgnore
    private String role;

    private List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();

    public ExternalUser() {}

    public ExternalUser(String userId) {
        this.id = userId;
    }

    public ExternalUser(User user) {
        this.id = user.getUuid().toString();
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isVerified = user.isVerified();
        for(SocialUser socialUser: user.getSocialUsers()) {
            SocialProfile profile = new SocialProfile();
            profile.setDisplayName(socialUser.getDisplayName());
            profile.setImageUrl(socialUser.getImageUrl());
            profile.setProfileUrl(socialUser.getProfileUrl());
            profile.setProvider(socialUser.getProviderId());
            profile.setProviderUserId(socialUser.getProviderUserId());
            socialProfiles.add(profile);
        }
        role = user.getRole().toString();
    }

    public ExternalUser(User user, AuthorizationToken activeSession) {
        this(user);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public String getId() {
        return id;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getName() {
        return emailAddress;
    }

    public String getRole() {
        return role;
    }
}
