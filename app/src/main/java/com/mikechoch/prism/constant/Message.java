package com.mikechoch.prism.constant;

public class Message {


    // LoginActivity
    public static final String FAILED_TO_LOGIN = "Failed to login";
    public static final String ACCOUNT_NOT_FOUND = "Account does not exist";
    public static final String RESET_PASSWORD_EMAIL_SENT = "Reset password email sent";
    public static final String RESET_PASSWORD_EMAIL_FAIL = "Failed to send email, please try again";
    public static final String INVALID_PASSWORD = "Invalid password";

    // RegistrationActivity
    public static final String USERNAME_TAKEN = "Username is taken";
    public static final String PASSWORD_NOT_STRONG = "Password is not strong enough";
    public static final String INVALID_EMAIL = "Email is invalid";
    public static final String ACCOUNT_ALREADY_EXISTS = "An account already exists with this email";
    public static final String VERIFICATION_EMAIL_SENT = "Verification email sent";
    public static final String VERIFICATION_EMAIL_FAIL = "Failed to send verification email, please try again";

    // MainActivity
    public static final String FETCH_LATEST_POSTS_FAIL = "Failed to pull latest posts";
    public static final String POST_UPLOAD_PERMISSION_DENIED = "Permission denied to upload a post";
    public static final String POST_IMAGE_UPLOAD_FAIL = "Failed to upload the image, please try again";
    public static final String POST_UPLOAD_FAIL = "Failed to upload the post, please try again";
    public static final String POST_NOT_FOUND = "Post does not exist";
    public static final String POST_UPLOAD_UPLOADING_IMAGE = "Uploading image...";
    public static final String POST_UPLOAD_FINISHING_UP = "Finishing up...";
    public static final String POST_UPLOAD_DONE = "Done";
    public static final String CANNOT_REPOST_OWN_POST = "You cannot repost your own post";
    public static final String NO_INTERNET = "No internet connection";
    public static final String POST_REPORT_SUCCESS = "Post successfully submitted for content review";
    public static final String POST_REPORT_FAIL = "Failed to submit post for content review";

    // DisplayUsersActivity
    public static final String FETCH_USERS_FAIL = "Failed to fetch the users";

    // EditProfileActivity
    public static final String FULL_NAME_UPDATE_FAIL = "Failed to update full name";
    public static final String FULL_NAME_UPDATE_SUCCESS = "Full name successfully updated";
    public static final String USERNAME_UPDATE_FAIL = "Failed to update username";
    public static final String USERNAME_UPDATE_SUCCESS = "Username successfully updated";
    public static final String PASSWORD_UPDATE_FAIL = "Failed to update password";
    public static final String PASSWORD_UPDATE_SUCCESS = "Password successfully updated";
    public static final String EMAIL_UPDATE_FAIL = "Failed to update email";
    public static final String EMAIL_UPDATE_SUCCESS = "Email successfully updated";
    public static final String PROFILE_PIC_UPDATE_SUCCESS = "Profile picture successfully updated";
    public static final String PROFILE_PIC_UPDATE_FAIL = "Failed to update profile picture";

    // EmailVerificationActivity
    public static final String EMAIL_VERIFICATION_SUCCESS = "Email successfully verified";

    // UnderMaintenanceActivity
    public static final String DEFAULT_UNDER_MAINTENANCE_MESSAGE = "Prism is currently under maintenance, please come back later. We apologize for the inconvenience.";

    // UsernameRegistrationActivity
    public static final String GOOGLE_SIGN_IN_FAIL = "Failed to Sign In with Google";
    public static final String USERNAME_REGISTRATION_FAIL = "Failed to register username";

    // PrismTagActivity
    public static final String PRISM_POST_NOT_FOUND = "Post not found";
    public static final String FETCH_POST_INFO_FAIL = "Failed to fetch post";

    // PrismUserProfileActivity
    public static final String FETCH_USER_REPOSTS_FAIL = "Failed to fetch user reposts";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String NO_LIKED_POSTS = "No liked posts";
    public static final String NO_UPLOADED_OR_REPOSTED_POSTS = "No uploaded or reposted posts";

    // Others
    public static final String CANNOT_NOTIFY_USER = "NotificationType message received, but notification manager is null";


    public static String verification_email(String email) {
        return "An email has been sent to " + email + ", please click on the verification link in the email";
    }

    public static String verification_email_long_message(String email) {
        return "Thank you for registering. We have sent you a " +
                "verification link to your email at " + email +
                ". Once you verify your account, you can start exploring and sharing your artwork.";
    }



}
