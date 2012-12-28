package com.sample.web.api;

import com.sample.web.model.SocialUser;
import com.sample.web.model.User;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static com.sample.web.util.StringUtil.maxLength;
import static com.sample.web.util.StringUtil.validEmail;


/**
 *
 * @author: Iain Porter
 */
@XmlRootElement
public class ExternalUser {

    private String id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String qrCode;
    private boolean isVerified;

    @Deprecated
    private String username;

    private List<SocialProfile> socialProfiles;

    public ExternalUser() {}

    public ExternalUser(User user) {
        this.id = user.getUuid().toString();
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isVerified = user.isVerified();
        List<SocialProfile> profiles = new ArrayList<SocialProfile>();
        for(SocialUser socialUser: user.getSocialUsers()) {
            SocialProfile profile = new SocialProfile();
            profile.setDisplayName(socialUser.getDisplayName());
            profile.setImageUrl(socialUser.getImageUrl());
            profile.setProfileUrl(socialUser.getProfileUrl());
            profile.setProvider(socialUser.getProviderId());
            profile.setProviderUserId(socialUser.getProviderUserId());
            profiles.add(profile);
        }
        this.socialProfiles = profiles;
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

    public String getQrCode() {
        return qrCode;
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

}
