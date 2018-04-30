package com.mikechoch.prism.attribute;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by parth on 2/6/18.
 */

public class ProfilePicture implements Parcelable {


    public String profilePicUri;
    public Uri hiResUri;
    public Uri lowResUri;
    public boolean isDefault = true;

    public ProfilePicture(String profilePicUri) {
        this.profilePicUri = profilePicUri;
        isDefault = Character.isDigit(profilePicUri.charAt(0));
        hiResUri = getHiResProfilePicUri();
        lowResUri = getLowResProfilePicUri();
    }

    private Uri getHiResProfilePicUri() {
        if (isDefault) {
            int profileIndex = Integer.parseInt(profilePicUri);
            DefaultProfilePicture picture = DefaultProfilePicture.values()[profileIndex];
            return Uri.parse(picture.getProfilePicture());
        }
        return Uri.parse(profilePicUri);
    }

    private Uri getLowResProfilePicUri() {
        if (isDefault) {
            int profileIndex = Integer.parseInt(profilePicUri);
            DefaultProfilePicture picture = DefaultProfilePicture.values()[profileIndex];
            return Uri.parse(picture.getProfilePictureLow());
        }
        return Uri.parse(profilePicUri);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePicUri);
        dest.writeParcelable(hiResUri, flags);
        dest.writeParcelable(lowResUri, flags);
        dest.writeByte((byte) (isDefault ? 1 : 0));
    }

    protected ProfilePicture(Parcel in) {
        profilePicUri = in.readString();
        hiResUri = in.readParcelable(Uri.class.getClassLoader());
        lowResUri = in.readParcelable(Uri.class.getClassLoader());
        isDefault = in.readByte() != 0;
    }

    public static final Creator<ProfilePicture> CREATOR = new Creator<ProfilePicture>() {
        @Override
        public ProfilePicture createFromParcel(Parcel in) {
            return new ProfilePicture(in);
        }

        @Override
        public ProfilePicture[] newArray(int size) {
            return new ProfilePicture[size];
        }
    };
}
