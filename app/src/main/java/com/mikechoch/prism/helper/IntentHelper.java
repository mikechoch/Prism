package com.mikechoch.prism.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.widget.ImageView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.DisplayUsersActivity;
import com.mikechoch.prism.activity.EditUserProfileActivity;
import com.mikechoch.prism.activity.EmailVerificationMessageActivity;
import com.mikechoch.prism.activity.LoginActivity;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.PrismPostDescriptionActivity;
import com.mikechoch.prism.activity.PrismPostDetailActivity;
import com.mikechoch.prism.activity.PrismPostImageEditActivity;
import com.mikechoch.prism.activity.PrismPostImageSelectionActivity;
import com.mikechoch.prism.activity.PrismUserProfileActivity;
import com.mikechoch.prism.activity.ProfilePictureUploadActivity;
import com.mikechoch.prism.activity.ShowUserProfilePictureActivity;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.type.PictureUpload;

import java.io.File;
import java.util.Date;

public class IntentHelper {

    public static void intentToEmailVerificationActivity(Context context, boolean shouldClearBackStack) {
        Intent intent = new Intent(context, EmailVerificationMessageActivity.class);
        if (shouldClearBackStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to Login Activity from Main Activity
     */
    public static void intentToLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        // ((Activity) context).finish(); TODO is this necessary?
    }

    /**
     * Intent to Main Activity from Register Activity
     * TODO Rename this method
     */
    public static void intentToMainActivity(Context context, boolean shouldClearBackStack) {
        Intent intent = new Intent(context, MainActivity.class);
        if (shouldClearBackStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        CurrentUser.prepareAppForUser(context, intent);
    }

    /**
     * Intent to Upload Image Activity from Main Activity
     */
    public static void intentToUploadImageSelectionActivity(Context context) {
        Intent uploadImageSelectionIntent = new Intent(context, PrismPostImageSelectionActivity.class);
        context.startActivity(uploadImageSelectionIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to Upload Image Activity from Main Activity
     */
    public static void intentToUploadImageEditActivity(Context context, String resultUri, PictureUpload pictureUpload) {
        Intent uploadImageEditIntent = new Intent(context, PrismPostImageEditActivity.class);
        uploadImageEditIntent.putExtra(Default.UPLOAD_IMAGE_SELECTION_URI_EXTRA, resultUri);
        uploadImageEditIntent.putExtra(Default.UPLOAD_IMAGE_SELECTION_TYPE_EXTRA, pictureUpload);
        context.startActivity(uploadImageEditIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to Upload Image Activity from Main Activity
     */
    public static void intentToUploadDescriptionActivity(Context context) {
        Intent uploadDescriptionIntent = new Intent(context, PrismPostDescriptionActivity.class);
        context.startActivity(uploadDescriptionIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Create an Intent to ask user to select a image they would like to upload
     */
    public static void selectImageFromGallery(Context context) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        ((Activity) context).startActivityForResult(Intent.createChooser(galleryIntent, "Select a picture"), Default.GALLERY_INTENT_REQUEST_CODE);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Create an Intent to ask user to take a picture with the phone's camera
     * Also prepares a file to save the image
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
     * Intent to EditUserProfileActivity where the user can modify their account information
     */
    public static void intentToEditUserProfileActivity(Context context) {
        Intent editUserProfileIntent = new Intent(context, EditUserProfileActivity.class);
        context.startActivity(editUserProfileIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to DisplayUserActivity with the correct intentType code
     * @param displayUsersCode
     */
    public static void intentToDisplayUsersActivity(Context context, String id, int displayUsersCode) {
        Intent displayUsersIntent = new Intent(context, DisplayUsersActivity.class);
        displayUsersIntent.putExtra(Default.USERS_INT_EXTRA, displayUsersCode);
        displayUsersIntent.putExtra(Default.USERS_DATA_ID_EXTRA, id);
        context.startActivity(displayUsersIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent from the current clicked PrismPost to the PrismPostDetail
     * @param context
     * @param prismPost
     */
    public static void intentToPrismPostDetailActivity(Context context, PrismPost prismPost) {
        Intent prismPostDetailIntent = new Intent(context, PrismPostDetailActivity.class);
        prismPostDetailIntent.putExtra(Default.PRISM_POST_DETAIL_EXTRA, prismPost);
        context.startActivity(prismPostDetailIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent from the current clicked PrismPost user to their PrismUserProfileActivity
     * @param context
     * @param prismUser
     */
    public static void intentToUserProfileActivity(Context context, PrismUser prismUser) {
        Intent prismUserProfileIntent = new Intent(context, PrismUserProfileActivity.class);
        prismUserProfileIntent.putExtra(Default.PRISM_USER_EXTRA, prismUser);
        context.startActivity(prismUserProfileIntent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     *
     * @param context
     * @param profilePictureType
     */
    public static void intentToProfilePictureUploadActivity(Context context, int profilePictureType) {
        Intent galleryImageUploadIntent = new Intent(context, ProfilePictureUploadActivity.class);
        galleryImageUploadIntent.putExtra(Default.PROFILE_PICTURE_TYPE_EXTRA, profilePictureType);
        ((Activity) context).startActivityForResult(galleryImageUploadIntent, Default.PROFILE_PIC_UPLOAD_INTENT_REQUEST_CODE);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Intent to ShowUserProfilePictureActivity
     * Where the hi-res PrismUser profile picture will be shown
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
    }

}
