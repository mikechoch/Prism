package com.mikechoch.prism.attribute;

import com.mikechoch.prism.helper.ProfileHelper;

import java.io.Serializable;

public class PrismUser implements Serializable {

    private String uid;
    private String username;
    private String fullName;
    private ProfilePicture profilePicture;
    private int followerCount;
    private int followingCount;
    private int uploadCount;
    private String token;

    public PrismUser() { }

    public PrismUser(String uid, String username, String fullName, ProfilePicture profilePicture, int followerCount, int followingCount, int uploadCount, String token) {
        this.uid = uid;
        this.username = username;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.uploadCount = uploadCount;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return ProfileHelper.getFirebaseDecodedUsername(username);
    }

    public String getFullName() {
        return fullName;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getUploadCount() {
        return uploadCount;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setUploadCount(int uploadCount) {
        this.uploadCount = uploadCount;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
