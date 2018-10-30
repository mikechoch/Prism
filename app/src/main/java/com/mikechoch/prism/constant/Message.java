package com.mikechoch.prism.constant;

public class Message {

    // Log.i()
    public static final String NO_DATA = "No data pulled from Firebase";
    public static final String LOGIN_ATTEMPT_FAIL = "Failed login attempt";

    // Log.e()
    public static final String FILE_UPLOAD_FAIL = "Failed to upload the image to cloud";
    public static final String PROFILE_PIC_UPDATE_FAIL = "Failed to update profile picture";
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

    public static final String POST_REPORTED_SUCCESS = "Successfully submit post for content review";
    public static final String POST_REPORTED_FAIL = "Failed to submit post for content review";

    // Toast messages
    public static final String CANNOT_REPOST_OWN_POST = "You cannot repost your own post";

    // SnackBar messages
    public static final String NO_INTERNET = "No Internet Connection";

    public static final String SEND_VERIFICATION_EMAIL_SENT = "Verification email sent";
    public static final String SEND_VERIFICATION_EMAIL_FAIL = "Failed to send verification email";

    // TODO Check if these messages are okay to be displayed to the user (in the form of Toasts)

    // Login Activity
    public static final String FAILED_TO_LOGIN = "Failed to login";
    public static final String ACCOUNT_NOT_FOUND = "Account does not exist for the given email";
    public static final String RESET_PASSWORD_EMAIL_SENT = "Reset password email sent";
    public static final String RESET_PASSWORD_EMAIL_FAIL = "Failed to send email. Please try again";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";

    // Registration Activity
    public static final String USERNAME_TAKEN = "Username is taken";
    public static final String PASSWORD_TOO_WEAK = "Password is too weak";
    public static final String INVALID_EMAIL = "Invalid email";
    public static final String ACCOUNT_ALREADY_EXISTS = "An account already exists with this email";
    public static final String VERIFICATION_EMAIL_FAIL = "Failed to send verification email. Please try again";

    public static String verification_email(String email) {
        return "An email has been sent to " + email + ". Please click on the verification link in the email";
    }

    // Main Activity
    public static final String POST_UPDATE_PERMISSION_DENIED = "You don't have the permission to upload a post";
    public static final String POST_IMAGE_UPLOAD_FAIL = "Failed to upload the image. Please try again";
    public static final String POST_UPLOAD_FAIL = "Failed to upload the post. Please try again";
    public static final String POST_NOT_FOUND = "Post does not exist";
    public static final String POST_UPLOAD_DONE = "Done";

}
