package com.mikechoch.prism.constant;

public class Message {

    // Log.i()
    public static final String NO_DATA = "No data pulled from Firebase";
    public static final String LOGIN_ATTEMPT_FAIL = "Failed login attempt";

    // Log.e()
    public static final String FILE_UPLOAD_FAIL = "Failed to upload the image to cloud";
    public static final String PROFILE_PIC_UPDATE_FAIL = "Failed to update profile picture";
    public static final String POST_UPLOAD_FAIL = "Failed to upload post to cloud";
    public static final String POST_DELETE_FAIL = "Failed to delete post from cloud";
    public static final String USER_EXIST_CHECK_FAIL = "Failed tp check if username exists in database";
    public static final String USER_ACCOUNT_CREATION_FAIL = "Failed to create the firebaseUser account";
    public static final String FETCH_USERS_FAIL = "Failed to pull list of users";
    public static final String FETCH_USER_DETAILS_FAIL = "Failed to details for users";
    public static final String FETCH_POST_INFO_FAIL = "Failed to fetch post details";
    public static final String FETCH_USERS_NOT_FOUND = "No users found";
    public static final String POST_NOT_EXIST = "Prism post does not exist but it should exist";
    public static final String USER_NOT_EXIST = "Prism user does not exist but it should exist";

    public static final String FULL_NAME_UPDATE_FAIL = "Failed to update full name";
    public static final String FULL_NAME_UPDATE_SUCCESS = "Full name successfully updated";

    public static final String USERNAME_UPDATE_FAIL = "Failed to update username";
    public static final String USERNAME_UPDATE_SUCCESS = "Username successfully updated";

    public static final String REAUTH_FAIL = "Failed to reauthenticate user";
    public static final String PASSWORD_UPDATE_FAIL = "Failed to update password";
    public static final String PASSWORD_UPDATE_SUCCESS = "Password successfully updated";

    public static final String EMAIL_UPDATE_FAIL = "Failed to update email";
    public static final String EMAIL_UPDATE_SUCCESS = "Email successfully updated";

    public static final String CANNOT_NOTIFY_USER = "NotificationType message received, but notification manager is null";

    public static final String PASSWORD_RESET_EMAIL_SEND_FAIL = "Failed to send password reset email";

    public static final String POST_REPORTED_SUCCESS = "Successfully submit post for content review";
    public static final String POST_REPORTED_FAIL = "Failed to submit post for content review";

    // Toast messages
    public static final String CANNOT_REPOST_OWN_POST = "You cannot repost your own post";

    // SnackBar messages
    public static final String NO_INTERNET = "No Internet Connection";

}
