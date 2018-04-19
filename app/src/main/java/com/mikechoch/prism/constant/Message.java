package com.mikechoch.prism.constant;

/**
 * Created by parth on 2/5/18.
 */

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


      public static final String FULL_NAME_UPDATE_FAIL = "Failed to update full name";
      public static final String FULL_NAME_UPDATE_SUCCESS = "Full name successfully updated";

      public static final String USERNAME_UPDATE_FAIL = "Failed to update username";
      public static final String USERNAME_UPDATE_SUCCESS = "Username successfully updated";

      public static final String REAUTH_FAIL = "Failed to reauthenticate user";
      public static final String PASSWORD_UPDATE_FAIL = "Failed to update password";
      public static final String PASSWORD_UPDATE_SUCCESS = "Password successfully updated";

      public static final String EMAIL_UPDATE_FAIL = "Failed to update email";
      public static final String EMAIL_UPDATE_SUCCESS = "Email successfully updated";

}
