package com.mikechoch.prism.constant;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Default {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference ALL_POSTS_REFERENCE = databaseReference.child(Key.DB_REF_ALL_POSTS);
    public static final DatabaseReference USERS_REFERENCE = databaseReference.child(Key.DB_REF_USER_PROFILES);
    public static final DatabaseReference TAGS_REFERENCE = databaseReference.child(Key.DB_REF_TAGS);
    public static final DatabaseReference ACCOUNT_REFERENCE = databaseReference.child(Key.DB_REF_ACCOUNTS);

    public static final StorageReference STORAGE_REFERENCE = FirebaseStorage.getInstance().getReference();

    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 99;
    public static final int IMAGE_UPLOAD_INTENT_REQUEST_CODE = 100;
    public static final int GALLERY_INTENT_REQUEST = 101;
    public static final int PROFILE_PIC_UPLOAD_INTENT_REQUEST_CODE = 102;

    public static final int DISPLAY_USERS_LIKE_CODE = 0;
    public static final int DISPLAY_USERS_REPOST_CODE = 1;
    public static final int DISPLAY_USERS_FOLLOWER_CODE = 2;
    public static final int DISPLAY_USERS_FOLLOWING_CODE = 3;

    public static final int IMAGE_LOAD_THRESHOLD = 3;
    public static final int IMAGE_LOAD_COUNT = 10;

    // ViewPager
    public static final int MAIN_VIEW_PAGER_SIZE = 5 - 1;
    public static final int MAIN_VIEW_PAGER_HOME = 0;
    public static final int MAIN_VIEW_PAGER_TRENDING = 1;
    public static final int MAIN_VIEW_PAGER_SEARCH = 2;
    public static final int MAIN_VIEW_PAGER_NOTIFICATIONS = 3;
    public static final int MAIN_VIEW_PAGER_PROFILE = 4;

    public static final int USER_POSTS_VIEW_PAGER_SIZE = 2;
    public static final int USER_POSTS_VIEW_PAGER_POSTS = 0;
    public static final int USER_POSTS_VIEW_PAGER_LIKES = 1;

    // SettingType
    public static final int SETTINGS_OPTION_APP = 0;
    public static final int SETTINGS_OPTION_NOTIFICATION = 1;
    public static final int SETTINGS_OPTION_ACCOUNT = 2;
    public static final int SETTINGS_OPTION_HELP = 3;
    public static final int SETTINGS_OPTION_ABOUT = 4;
    public static final int SETTINGS_OPTION_LOGOUT = 5;

    public static final int USER_UPLOADED_POSTS_COLUMNS = 3;

    // Regex String
    public static final String USERNAME_PERIOD = ".";
    public static final String USERNAME_PERIOD_REPLACE = "-";

    // Firebase
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL";

    // Button Strings
    public static final String BUTTON_OK = "OK";
    public static final String BUTTON_SAVE = "SAVE";
    public static final String BUTTON_CANCEL = "CANCEL";
    public static final String BUTTON_DELETE = "DELETE";
    public static final String BUTTON_REPOST = "REPOST";
    public static final String BUTTON_UNFOLLOW = "UNFOLLOW";

    // Log Message Tags
    public static final String TAG_DB = "Firebase Database";
    public static final String TAG_DEBUG = "Debug Output";

    // Timer intervals
    public static final int NOTIFICATION_UPDATE_INTERVAL = 5000; // milliseconds

    // Scale variable for programmatically sized UI elements
    public static float SCALE;

    public static final String HIDDEN_PASSWORD = "********";

    public static final String REGEX_HASHTAG = "#(\\S+)";
}
