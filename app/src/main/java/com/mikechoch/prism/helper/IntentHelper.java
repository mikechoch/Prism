package com.mikechoch.prism.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.widget.ImageView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.DisplayUsersActivity;
import com.mikechoch.prism.activity.EditUserProfileActivity;
import com.mikechoch.prism.activity.EmailVerificationMessageActivity;
import com.mikechoch.prism.activity.ImageEditActivity;
import com.mikechoch.prism.activity.LoginActivity;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.NoInternetActivity;
import com.mikechoch.prism.activity.NotificationSettingsActivity;
import com.mikechoch.prism.activity.PrismPostDetailActivity;
import com.mikechoch.prism.activity.PrismPostImageSelectionActivity;
import com.mikechoch.prism.activity.PrismTagActivity;
import com.mikechoch.prism.activity.PrismUserProfileActivity;
import com.mikechoch.prism.activity.ProfilePictureUploadActivity;
import com.mikechoch.prism.activity.SearchActivity;
import com.mikechoch.prism.activity.ShowUserProfilePictureActivity;
import com.mikechoch.prism.activity.UnderMaintenanceActivity;
import com.mikechoch.prism.activity.UpdateAppRequiredActivity;
import com.mikechoch.prism.activity.UsernameRegistrationActivity;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.type.DisplayUserType;
import com.mikechoch.prism.type.PictureUpload;
import com.mikechoch.prism.type.ProfilePictureOption;

import java.io.File;
import java.util.Date;


public class IntentHelper {

    /**
     * Reset entire application from cold start
     * @param context - Context of current activity app will intent from
     */
    public static void resetApplication(Context context) {
        Intent resetApplicationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (resetApplicationIntent != null) {
            resetApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(resetApplicationIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * When user is verifying their email, the app must wait at this activity until confirmed
     * @param context - Context of current activity app will intent from
     * @param shouldClearBackStack - boolean to handle clearing the back stack after intent
     */
    public static void intentToEmailVerificationActivity(Context context, boolean shouldClearBackStack) {
        Intent intent = new Intent(context, EmailVerificationMessageActivity.class);
        if (shouldClearBackStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * When user signs in with Google or Facebook, they need to give a username for their account
     * @param context - Context of current activity app will intent from
     * @param displayName - username that the user wants to set for their Google/FB account sign-in
     */
    public static void intentToUsernameRegistrationActivity(Context context, String displayName) {
        Intent intent = new Intent(context, UsernameRegistrationActivity.class);
        intent.putExtra(Default.USERNAME_REGISTRATION_EXTRA, displayName);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Travel to the login activity of the application
     * @param context - Context of current activity app will intent from
     */
    public static void intentToLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Travel to the main activity of the application
     * @param context - Context of current activity app will intent from
     */
    public static void intentToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ((Activity) context).finish();
    }

    /**
     * WARNING: ONLY USE WHEN COMING UPLOADING PRISMPOST
     * Only used to intent to MainActivity from PrismPost upload process
     * @param context - Context of current activity app will intent from
     * @param imageUri - imageUri of PrismPost image to upload to Firebase
     * @param description - description of PrismPost to upload to Firebase
     */
    public static void intentToMainActivityWithPrismUploadSuccess(Context context, String imageUri, String description) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainActivityIntent.putExtra(Default.UPLOAD_IMAGE_INTENT_KEY, true);
        mainActivityIntent.putExtra(Default.IMAGE_URI_EXTRA, imageUri);
        mainActivityIntent.putExtra(Default.IMAGE_DESCRIPTION_EXTRA, description);
        context.startActivity(mainActivityIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to SearchActivity for searching for PrismUsers and Tags
     * @param context - Context of current activity app will intent from
     */
    public static void intentToSearchActivity(Context context) {
        Intent searchIntent = new Intent(context, SearchActivity.class);
        context.startActivity(searchIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to PrismTagActivity for showing all PrismPosts linked to the tag
     * @param context - Context of current activity app will intent from
     * @param tag - PrismTag to show information about
     */
    public static void intentToTagActivity(Context context, String tag) {
        Intent tagIntent = new Intent(context, PrismTagActivity.class);
        tagIntent.putExtra(Default.CLICKED_TAG_EXTRA, tag);
        context.startActivity(tagIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Image selection intent, where user will take a picture or select from gallery
     * @param context - Context of current activity app will intent from
     */
    public static void intentToUploadImageSelectionActivity(Context context) {
        Intent uploadImageSelectionIntent = new Intent(context, PrismPostImageSelectionActivity.class);
        context.startActivity(uploadImageSelectionIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Go to image edit activity, which will take a cropped image from an activity and allow it to be modified
     * @param context - Context of current activity app will intent from
     * @param resultUri - uri of cropped image
     * @param pictureUpload - Enum representing the type of image uploading
     *                      ex. PrismPost or ProfilePicture
     */
    public static void intentToUploadImageEditActivity(Context context, String resultUri, PictureUpload pictureUpload) {
        Intent uploadImageEditIntent = new Intent(context, ImageEditActivity.class);
        uploadImageEditIntent.putExtra(Default.UPLOAD_IMAGE_SELECTION_URI_EXTRA, resultUri);
        uploadImageEditIntent.putExtra(Default.UPLOAD_IMAGE_SELECTION_TYPE_EXTRA, pictureUpload);
        context.startActivity(uploadImageEditIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to gallery activity and using on ActivityResult obtain the URI
     * @param context - Context of current activity app will intent from
     */
    public static void selectImageFromGallery(Context context) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(Intent.createChooser(galleryIntent, "Select a picture"), Default.GALLERY_INTENT_REQUEST_CODE);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to camera activity to capture an image and return the URI to a specific activity
     * @param context - Context of current activity app will intent from
     * @return - URI of image taken from camera activity
     */
    public static Uri takePictureFromCamera(Context context) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(dir, "image" + new Date().getTime() + ".jpeg");
        Uri imageUriExtra = Uri.fromFile(output);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        ((Activity) context).startActivityForResult(cameraIntent, Default.CAMERA_INTENT_REQUEST_CODE);
        return imageUriExtra;
    }

    /**
     * Users information is shown here and can be edited at their leisure
     * @param context - Context of current activity app will intent from
     */
    public static void intentToEditUserProfileActivity(Context context) {
        Intent editUserProfileIntent = new Intent(context, EditUserProfileActivity.class);
        context.startActivity(editUserProfileIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Handle the intent to a DisplayUsersActivity for likes, reposts, followers, or following
     * @param context - Context of current activity app will intent from
     * @param id - PrismPost Id or PrismUser Id
     * @param displayUserType - enum handling which type of users are going to be shown in a list
     *                         ex. Users who Liked, Reposted, Followed, or are Following
     */
    public static void intentToDisplayUsersActivity(Context context, String id, DisplayUserType displayUserType) {
        Intent displayUsersIntent = new Intent(context, DisplayUsersActivity.class);

        displayUsersIntent.putExtra(Default.DISPLAY_USERS_TYPE, displayUserType);
        displayUsersIntent.putExtra(Default.DISPLAY_USERS_ITEM_ID, id);
        context.startActivity(displayUsersIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to PrismPostDetailActivity to show all information pertaining
     * to the specific PrismPost
     * @param context - Context of current activity app will intent from
     * @param prismPost - PrismPost that's detail page will be shown
     */
    public static void intentToPrismPostDetailActivity(Context context, PrismPost prismPost) {
        Intent prismPostDetailIntent = new Intent(context, PrismPostDetailActivity.class);
        prismPostDetailIntent.putExtra(Default.PRISM_POST_DETAIL_EXTRA, prismPost);
        context.startActivity(prismPostDetailIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to UserProfileActivity to show all information pertaining to the PrismUser passed
     * @param context - Context of current activity app will intent from
     * @param prismUser - PrismUser that's profile will be shown
     */
    public static void intentToUserProfileActivity(Context context, PrismUser prismUser) {
        Intent prismUserProfileIntent = new Intent(context, PrismUserProfileActivity.class);
        prismUserProfileIntent.putExtra(Default.PRISM_USER_EXTRA, prismUser);
        context.startActivity(prismUserProfileIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to ProfilePictureUpload, where the image selection is circular and image edit
     * is same as PrismPost uploading
     * @param context - Context of current activity app will intent from
     * @param profilePictureType - int directing the user to take a selfie or choose form gallery
     */
    public static void intentToProfilePictureUploadActivity(Context context, ProfilePictureOption profilePictureType) {
        Intent galleryImageUploadIntent = new Intent(context, ProfilePictureUploadActivity.class);
        galleryImageUploadIntent.putExtra(Default.PROFILE_PICTURE_TYPE_EXTRA, profilePictureType);
        ((Activity) context).startActivityForResult(galleryImageUploadIntent, Default.PROFILE_PIC_UPLOAD_INTENT_REQUEST_CODE);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Show a large scale profile picture image
     * @param context - Context of current activity app will intent from
     * @param prismUser - PrismUser to show profile picture for
     * @param userProfilePicImageView - ImageView to have Transition from
     */
    public static void intentToShowUserProfilePictureActivity(Context context, PrismUser prismUser, ImageView userProfilePicImageView) {
        Intent showProfilePictureIntent = new Intent(context, ShowUserProfilePictureActivity.class);

        showProfilePictureIntent.putExtra(Default.PRISM_USER_EXTRA, prismUser);
        showProfilePictureIntent.putExtra(Default.PRISM_USER_PROFILE_PICTURE_TRANSITION_NAME_EXTRA, ViewCompat.getTransitionName(userProfilePicImageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity) context,
                userProfilePicImageView,
                ViewCompat.getTransitionName(userProfilePicImageView));

        context.startActivity(showProfilePictureIntent, options.toBundle());
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     *
     * @param context
     */
    public static void intentToNotificationSettingsActivity(Context context) {
        Intent notificationIntent = new Intent(context, NotificationSettingsActivity.class);
        context.startActivity(notificationIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * This is strictly handled by a the internet status of the app
     * @param context - Context of current activity app will intent from
     */
    public static void intentToNoInternetActivity(Context context) {
        Intent noInternetIntent = new Intent(context, NoInternetActivity.class);
        noInternetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(noInternetIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * This is strictly handled by a boolean in Firebase, and will show when that boolean is True
     * @param context - Context of current activity app will intent from
     * @param message - under maintenance message String
     */
    public static void intentToUnderMaintenanceActivity(Context context, String message) {
        Intent intent = new Intent(context, UnderMaintenanceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Key.STATUS_MESSAGE, message);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * This is strictly handled by current Prism version code and the Firebase min required
     * @param context - Context of current activity app will intent from
     */
    public static void intentToUpdatedAppRequiredActivity(Context context) {
        Intent intent = new Intent(context, UpdateAppRequiredActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to the app store url provided
     * @param context - Context of current activity app will intent from
     * @param prismAppStoreUrl -  String url to intent to in app store
     */
    public static void intentToAppStoreUrl(Context context, String prismAppStoreUrl) {
        Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prismAppStoreUrl));
        context.startActivity(appStoreIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static Intent prepareNotificationIntent(Context context) {
        Bundle extras = ((Activity) context).getIntent().getExtras();
        if (extras == null) {
            return null;
        }
        Intent intent = null;
        String prismPostId = extras.getString(NotificationKey.PRISM_POST_ID);
        String prismUserId = extras.getString(NotificationKey.PRISM_USER_ID);

        if (prismPostId != null) {
            intent = new Intent(context, PrismPostDetailActivity.class);
            intent.putExtra(NotificationKey.PRISM_POST_ID, prismPostId);
        } else if (prismUserId != null) {
            intent = new Intent(context, PrismUserProfileActivity.class);
            intent.putExtra(NotificationKey.PRISM_USER_ID, prismUserId);
        }
        return intent;
    }

    public static void intentToNotification(Context context) {
        Intent notificationIntent = prepareNotificationIntent(context);
        Intent mainIntent = new Intent(context, MainActivity.class);
        Intent[] intents = new Intent[]{mainIntent, notificationIntent};
        context.startActivities(intents);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ((Activity) context).finish();
    }
}
