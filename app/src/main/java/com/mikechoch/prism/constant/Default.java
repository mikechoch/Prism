package com.mikechoch.prism.constant;

import android.app.Activity;
import android.graphics.Typeface;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikechoch.prism.attribute.UserPreference;

import java.util.Arrays;
import java.util.List;


public class Default {

    private static final DatabaseReference TEST_DEFAULT_DATABASE = FirebaseDatabase.getInstance("https://prism-cd0f8.firebaseio.com/").getReference();
    private static final DatabaseReference PRODUCTION_DATABASE = FirebaseDatabase.getInstance("https://prism-cd0f8-36af2.firebaseio.com/").getReference();

    /* CHANGE THIS BEFORE PUBLISHING */
    private static final DatabaseReference databaseReference = TEST_DEFAULT_DATABASE;

    public static final DatabaseReference ROOT_REFERENCE = databaseReference;
    public static final DatabaseReference ALL_POSTS_REFERENCE = databaseReference.child(Key.DB_REF_ALL_POSTS);
    public static final DatabaseReference USERS_REFERENCE = databaseReference.child(Key.DB_REF_USER_PROFILES);
    public static final DatabaseReference TAGS_REFERENCE = databaseReference.child(Key.DB_REF_TAGS);
    public static final DatabaseReference ACCOUNTS_REFERENCE = databaseReference.child(Key.DB_REF_ACCOUNTS);
    public static final DatabaseReference CONTENT_REVIEW_REFERENCE = databaseReference.child(Key.DB_REF_CONTENT_REVIEW);
    public static final DatabaseReference APP_STATUS_REFERENCE = databaseReference.child(Key.DB_REF_APP_STATUS);

    public static final StorageReference STORAGE_REFERENCE = FirebaseStorage.getInstance().getReference();

    public static float scale;
    public static int screenHeight;
    public static int screenWidth;

    private static String sourceSansProLightPath = "fonts/SourceSansPro-Light.ttf";
    private static String sourceSansProBoldPath = "fonts/SourceSansPro-Black.ttf";
    public static Typeface sourceSansProLight;
    public static Typeface sourceSansProBold;

    // Permissions Result Request Codes
    public static final int MY_PERMISSIONS_WRITE_MEDIA_REQUEST_CODE = 200;
    public static final int MY_PERMISSIONS_CAMERA_REQUEST_CODE = 201;

    // Intent Request Codes
    public static final int IMAGE_UPLOAD_INTENT_REQUEST_CODE = 100;
    public static final int PROFILE_PIC_UPLOAD_INTENT_REQUEST_CODE = 101;
    public static final int GALLERY_INTENT_REQUEST_CODE = 102;
    public static final int CAMERA_INTENT_REQUEST_CODE = 103;

    // Login Result Request Codes
    public static final int SIGN_IN_WITH_GOOGLE_REQUEST_CODE = 300;

    public static final int IMAGE_LOAD_THRESHOLD = 3;
    public static final int IMAGE_LOAD_COUNT = 10;

    // Extra Strings
    public static final String USERNAME_REGISTRATION_EXTRA = "UsernameRegistration";
    public static final String CLICKED_TAG_EXTRA = "ClickedTag";
    public static final String ONLY_PERFORM_REFRESH_EXTRA = "OnlyPerformRefresh";
    public static final String PRISM_POST_DETAIL_EXTRA = "PrismPostDetail";
    public static final String PRISM_POST_DETAIL_TRANSITION_NAME_EXTRA = "PrismPostDetailTransitionName";
    public static final String PRISM_USER_PROFILE_PICTURE_TRANSITION_NAME_EXTRA = "PrismUserProfilePictureTransitionName";
    public static final String PRISM_USER_EXTRA = "PrismUser";
    public static final String DISPLAY_USERS_TYPE = "UsersType";
    public static final String DISPLAY_USERS_ITEM_ID = "PostOrUserId";
    public static final String IMAGE_URI_EXTRA = "ImageUri";
    public static final String IMAGE_DESCRIPTION_EXTRA = "ImageDescription";
    public static final String CROPPED_PROFILE_PICTURE_EXTRA = "CroppedProfilePicture";
    public static final String PROFILE_PICTURE_TYPE_EXTRA = "ProfilePictureType";
    public static final String UPLOAD_IMAGE_INTENT_KEY = "UploadImage";
    public static final String UPLOAD_IMAGE_SELECTION_URI_EXTRA = "UploadImageSelectionUri";
    public static final String UPLOAD_IMAGE_SELECTION_TYPE_EXTRA = "UploadImageSelectionType";
    public static final String UPLOAD_IMAGE_FILE_PATH_EXTRA = "EditedPrismPostFilePath";

    // Bottom Navigation Bar Strings
    public static final String BOTTOM_NAVIGATION_BAR_NAME = "navigation_bar_height";
    public static final String BOTTOM_NAVIGATION_BAR_DEF_TYPE = "dimen";
    public static final String BOTTOM_NAVIGATION_BAR_DEF_PACKAGE = "android";

    // ViewPager
    public static final int MAIN_VIEW_PAGER_SIZE = 4;
    public static final int MAIN_VIEW_PAGER_MAIN_FEED = 0;
    public static final int MAIN_VIEW_PAGER_SEARCH = 1;
    public static final int MAIN_VIEW_PAGER_NOTIFICATIONS = 2;
    public static final int MAIN_VIEW_PAGER_PROFILE = 3;

    public static final int USER_POSTS_VIEW_PAGER_SIZE = 2;
    public static final int USER_POSTS_VIEW_PAGER_POSTS = 0;
    public static final int USER_POSTS_VIEW_PAGER_LIKES = 1;

    public static final int IMAGE_EDIT_TYPE_VIEW_PAGER_SIZE = 2;
    public static final int IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER = 0;
    public static final int IMAGE_EDIT_TYPE_VIEW_PAGER_EDIT = 1;

    public static final int SEARCH_TYPE_VIEW_PAGER_SIZE = 2;
    public static final int SEARCH_TYPE_VIEW_PAGER_PEOPLE = 0;
    public static final int SEARCH_TYPE_VIEW_PAGER_TAG = 1;

    public static final int POSTS_COLUMNS = 3;

    public static final int CROP_VALID = 0;
    public static final int CROP_RES_INVALID = 1;
    public static final int CROP_ASPECT_RATIO_INVALID = 2;

    // Regex String
    public static final String USERNAME_PERIOD = ".";
    public static final String USERNAME_PERIOD_REPLACE = "-";

    // Firebase
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String ADMIN_CHANNEL_ID = "ADMIN_CHANNEL";

    // Button Strings
    // TODO These can be enums
    public static final String BUTTON_OK = "OK";
    public static final String BUTTON_SAVE = "SAVE";
    public static final String BUTTON_UPDATE = "UPDATE";
    public static final String BUTTON_SUBMIT = "SUBMIT";
    public static final String BUTTON_CANCEL = "CANCEL";
    public static final String BUTTON_DELETE = "DELETE";
    public static final String BUTTON_REPOST = "REPOST";
    public static final String BUTTON_UNFOLLOW = "UNFOLLOW";
    public static final String BUTTON_REPORT = "REPORT";

    // Log Message Tags
    public static final String TAG_DB = "FirebaseDatabase";
    public static final String TAG_GOOGLE_CLIENT = "GoogleSignInClient";
    public static final String TAG_DEBUG = "Debug Output";
    public static final String TAG_NOTIFICATION = "Prism NotificationType";

    // Discover
    public static final int DISCOVER_PAGE_HASHTAGS = 2;

    // Timer intervals in milliseconds
    public static final int NOTIFICATION_UPDATE_INTERVAL = 5000;
    public static final int PUSH_NOTIFICATION_HANDLER_WAIT = 2500; // this should be 10 seconds or 30 seconds

    public static final String HIDDEN_PASSWORD = "********";

    public static final List<Character> ILLEGAL_HASHTAG_CHARS = Arrays.asList(' ', '\n', '#', ',');

    public static final UserPreference USER_PREFERENCE = new UserPreference(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);

    public static final String PRISM_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.mikechoch.prism";

    public static void initializeScreenSizeElements(Activity context) {
        Default.scale = context.getResources().getDisplayMetrics().density;
        Default.screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        Default.screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
    }

    public static void initializeTypeface(Activity context) {
        Default.sourceSansProLight = Typeface.createFromAsset(context.getAssets(), Default.sourceSansProLightPath);
        Default.sourceSansProBold = Typeface.createFromAsset(context.getAssets(), Default.sourceSansProBoldPath);
    }
}
