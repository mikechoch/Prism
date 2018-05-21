package com.mikechoch.prism.attribute;

import android.os.Parcel;
import android.os.Parcelable;

import com.mikechoch.prism.helper.Helper;

/**
 * Created by mikechoch on 1/30/18.
 */

public class PrismUser implements Parcelable {

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
        return Helper.getFirebaseDecodedUsername(username);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeInt(followerCount);
        dest.writeInt(followingCount);
        dest.writeInt(uploadCount);
        dest.writeParcelable(profilePicture, 0);
        dest.writeString(token);
    }

    protected PrismUser(Parcel in) {
        uid = in.readString();
        username = in.readString();
        fullName = in.readString();
        followerCount = in.readInt();
        followingCount = in.readInt();
        uploadCount = in.readInt();
        profilePicture = in.readParcelable(ProfilePicture.class.getClassLoader());
        token = in.readString();
    }

    public static final Creator<PrismUser> CREATOR = new Creator<PrismUser>() {
        @Override
        public PrismUser createFromParcel(Parcel in) {
            return new PrismUser(in);
        }

        @Override
        public PrismUser[] newArray(int size) {
            return new PrismUser[size];
        }
    };


}
