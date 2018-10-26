package com.mikechoch.prism.attribute;

import android.net.Uri;

import java.io.Serializable;

public class ProfilePicture implements Serializable {

    private String profilePicUri;
    private boolean isDefault;

    public ProfilePicture(String profilePicUri) {
        this.profilePicUri = profilePicUri;
        this.isDefault = Character.isDigit(profilePicUri.charAt(0));
    }

    public Uri getHiResProfilePicUri() {
        if (isDefault) {
            int profileIndex = Integer.parseInt(profilePicUri);
            DefaultProfilePicture picture = DefaultProfilePicture.values()[profileIndex];
            return Uri.parse(picture.getProfilePicture());
        }
        return Uri.parse(profilePicUri);
    }

    public Uri getLowResProfilePicUri() {
        if (isDefault) {
            int profileIndex = Integer.parseInt(profilePicUri);
            DefaultProfilePicture picture = DefaultProfilePicture.values()[profileIndex];
            return Uri.parse(picture.getProfilePictureLow());
        }
        return Uri.parse(profilePicUri);
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public boolean isDefault() {
        return isDefault;
    }

}
