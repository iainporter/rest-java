package com.incept5.rest.user.api;

import com.incept5.rest.user.domain.SessionToken;
import com.incept5.rest.user.domain.SocialUser;
import com.incept5.rest.user.domain.User;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.incept5.rest.util.StringUtil.maxLength;
import static com.incept5.rest.util.StringUtil.validEmail;


/**
 *
 * @author: Iain Porter
 */
@XmlRootElement
public class ExternalUser implements Principal {

    private String id;
    private String firstName;
    private String lastName;
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

    public ExternalUser(User user, SessionToken activeSession) {
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

    public boolean validate() {
        try {
            validEmail(emailAddress);
            if(StringUtils.hasText(firstName)) {
                maxLength(firstName, 100);
            }
            if(StringUtils.hasText(lastName)) {
                maxLength(lastName, 100);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getName() {
        return emailAddress;
    }

    public String getRole() {
        return role;
    }
}
