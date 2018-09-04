package com.mikechoch.prism.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.PrismPostDetailActivity;
import com.mikechoch.prism.activity.PrismTagActivity;
import com.mikechoch.prism.activity.PrismUserProfileActivity;
import com.mikechoch.prism.activity.ProfilePictureUploadActivity;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.ProfilePicture;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.TimeUnit;
import com.mikechoch.prism.fire.CurrentUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parth on 2/16/18.
 */

public class Helper {

    /**
     * Takes in a String permission checks the status of the permission
     * If permission denied then a request will be made if the permission is valid
     * Currently only handling:
     * Manifest.permission.WRITE_EXTERNAL_STORAGE
     * Manifest.permission.CAMERA
     * @param context
     * @param permission
     */
    public static boolean permissionRequest(Context context, String permission) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, permission);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            int permissionRequestCode = -1;
            switch (permission) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    permissionRequestCode = Default.MY_PERMISSIONS_WRITE_MEDIA_REQUEST_CODE;
                    break;
                case Manifest.permission.CAMERA:
                    permissionRequestCode = Default.MY_PERMISSIONS_CAMERA_REQUEST_CODE;
                    break;
            }
            if (permissionRequestCode != -1) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, permissionRequestCode);
                return false;
            }
        }
        return true;
    }

    /**
     * Takes in a dataSnapshot object and parses its contents
     * and returns a prismPost object
     * @return PrismPost object
     */
    public static PrismPost constructPrismPostObject(DataSnapshot postSnapshot) {
        PrismPost prismPost = postSnapshot.getValue(PrismPost.class);
        prismPost.setPostId(postSnapshot.getKey());
        prismPost.setLikes((int) postSnapshot.child(Key.DB_REF_POST_LIKED_USERS).getChildrenCount());
        prismPost.setReposts((int) postSnapshot.child(Key.DB_REF_POST_REPOSTED_USERS).getChildrenCount());
        return prismPost;
    }

    /**
     * Takes in userSnapshot object and parses the firebaseUser details
     * and creates a prismUser object
     * @return PrismUser object
     */
    public static PrismUser constructPrismUserObject(DataSnapshot userSnapshot) {
        PrismUser prismUser = new PrismUser();
        prismUser.setUid(userSnapshot.getKey());
        prismUser.setUsername((String) userSnapshot.child(Key.USER_PROFILE_USERNAME).getValue());
        prismUser.setFullName((String) userSnapshot.child(Key.USER_PROFILE_FULL_NAME).getValue());
        prismUser.setProfilePicture(new ProfilePicture((String) userSnapshot.child(Key.USER_PROFILE_PIC).getValue()));

        int followerCount = 0;
        int followingCount = 0;
        int uploadCount = 0;

        if (userSnapshot.hasChild(Key.DB_REF_USER_FOLLOWERS)) {
            followerCount = (int) userSnapshot.child(Key.DB_REF_USER_FOLLOWERS).getChildrenCount();
        }
        if (userSnapshot.hasChild(Key.DB_REF_USER_FOLLOWINGS)) {
            followingCount = (int) userSnapshot.child(Key.DB_REF_USER_FOLLOWINGS).getChildrenCount();
        }
        if (userSnapshot.hasChild(Key.DB_REF_USER_UPLOADS)) {
            uploadCount = (int) userSnapshot.child(Key.DB_REF_USER_UPLOADS).getChildrenCount();
        }
        if (userSnapshot.hasChild(Key.USER_TOKEN)) {
            prismUser.setToken((String) userSnapshot.child(Key.USER_TOKEN).getValue());
        }

        prismUser.setFollowerCount(followerCount);
        prismUser.setFollowingCount(followingCount);
        prismUser.setUploadCount(uploadCount);

        return prismUser;
    }

    /**
     *
     */
    public static boolean isPrismUserCurrentUser(PrismUser prismUser) {
        return CurrentUser.prismUser.getUid().equals(prismUser.getUid());
    }

    public static boolean isPrismUserCurrentUser(String prismUserId) {
        return CurrentUser.prismUser.getUid().equals(prismUserId);
    }


    /**
     *
     */
    public static String getSingularOrPluralText(String string, int count) {
        return count == 1 ? string : string + "s";
    }

    /**
     * Takes in the time of the post and creates a fancy string difference
     * Examples:
     * 10 seconds ago/Just now      (time < minute)
     * 20 minutes ago               (time < hour)
     * 2 hours ago                  (time < day)
     * 4 days ago                   (time < week)
     * January 21                   (time < year)
     * September 18, 2017           (else)
     */
    public static String getFancyDateDifferenceString(long time) {
        // Create a calendar object and calculate the timeFromStart
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        long timeFromCurrent = calendar.getTimeInMillis() - time;

        // Set the calendar object to be the time of the post
        calendar.setTimeInMillis(time);

        // Calculate all units for the given timeFromCurrent
        long secondsTime = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(timeFromCurrent);
        long minutesTime = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(timeFromCurrent);
        long hoursTime = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(timeFromCurrent);
        long daysTime = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(timeFromCurrent);

        // The fancyDateString will start off as this DateFormat to satisfy the else case
        String fancyDateString = DateFormat.format("MMM dd, yyyy", calendar).toString();

        // Check each calculated time unit until it is clear the unit of timeFromCurrent
        if (secondsTime < TimeUnit.SECONDS_UNIT) {
//                String fancyDateTail = secondsTime == 1 ? " second ago" : " seconds ago";
//                fancyDateString = secondsTime + fancyDateTail;
            fancyDateString = "Just now";
        } else if (minutesTime < TimeUnit.MINUTES_UNIT) {
            String fancyDateTail = minutesTime == 1 ? " minute ago" : " minutes ago";
            fancyDateString = minutesTime + fancyDateTail;
        } else if (hoursTime < TimeUnit.HOURS_UNIT) {
            String fancyDateTail = hoursTime == 1 ? " hour ago" : " hours ago";
            fancyDateString = hoursTime + fancyDateTail;
        } else if (daysTime < 7) {
            String fancyDateTail = daysTime == 1 ? " day ago" : " days ago";
            fancyDateString = daysTime + fancyDateTail;
        } else if (daysTime < 30) {
            String fancyDateTail = (daysTime / 7) == 1 ? " week ago" : " weeks ago";
            fancyDateString = (daysTime / 7) + fancyDateTail;
        } else if (daysTime < 365) {
            String fancyDateTail = (daysTime / 30) == 1 ? " month ago" : " months ago";
            fancyDateString = (daysTime / 30) + fancyDateTail;
        } else {
            String fancyDateTail = (daysTime / 365) == 1 ? " year ago" : " years ago";
            fancyDateString = (daysTime / 365) + fancyDateTail;
        }
        return fancyDateString;
    }

    /**
     * Checks to see if given prismPost has been reposted by given
     * prismUser by comparing the uid of prismPost author by given
     * prismUser. If uids match, post author = given prismUser and
     * hence it's an upload, otherwise it is a repost
     */
    public static boolean isPostReposted(PrismPost prismPost, PrismUser prismUser) {
        return prismPost.getUid().equals(prismUser.getUid());
    }

    /**
     *
     */
    public static ArrayList<String> parseDescriptionForTags(String description) {
        ArrayList<String> listOfTags = new ArrayList<>();
        char currentChar;
        for (int i = 0; i < description.length();) {
            currentChar = description.charAt(i++);
            if (currentChar == '#') {
                StringBuilder tag = new StringBuilder();
                while (i < description.length() && !Default.ILLEGAL_TAG_CHARS.contains(description.charAt(i))) {
                    currentChar = description.charAt(i++);
                    tag.append(currentChar);
                }
                if (tag.length() > 0) {
                    listOfTags.add(tag.toString());
                }
            }
        }
        return listOfTags;
    }

    /**
     *
     * @param context
     * @param string
     * @return
     */
    public static SpannableString createClickableTagsInString(Context context, String string) {
        SpannableString spannableString = new SpannableString(string);
        char currentChar;
        for (int i = 0; i < string.length();) {
            currentChar = string.charAt(i++);
            if (currentChar == '#') {
                StringBuilder tag = new StringBuilder();
                while (i < string.length() && !Default.ILLEGAL_TAG_CHARS.contains(string.charAt(i))) {
                    currentChar = string.charAt(i++);
                    tag.append(currentChar);
                }
                if (tag.length() > 0) {
                    int urlStartIndex = i - 1 - tag.length();
                    int urlEndIndex = i;
                    createClickableSpan(context, spannableString, tag.toString(), urlStartIndex, urlEndIndex);
                }
            }
        }
        return spannableString;
    }

    /**
     *
     * @param context
     * @param spannableString
     * @param tag
     * @param startIndex
     * @param endIndex
     */
    private static void createClickableSpan(final Context context, SpannableString spannableString, String tag, int startIndex, int endIndex) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent searchIntent = new Intent(context, PrismTagActivity.class);
                searchIntent.putExtra(Default.CLICKED_TAG_EXTRA, tag);
                context.startActivity(searchIntent);
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        spannableString.setSpan(clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     *
     * @param context
     * @param tabTitle
     * @return
     */
    public static TextView createTabTextView(Context context, String tabTitle) {
        TextView postsTabTextView = new TextView(context);
        postsTabTextView.setText(tabTitle);
        postsTabTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        postsTabTextView.setTextSize(16);
        postsTabTextView.setTextColor(Color.WHITE);
        postsTabTextView.setTypeface(Default.sourceSansProBold);
        return postsTabTextView;
    }

    /**
     *
     * @param context
     * @param message
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String message, boolean longLength) {
        if (longLength) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            toast(context, message);
        }
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Disables the snackBar dismiss on swipe to right
     */
    public static void disableSnackbarSwipeDismiss(final View snackBarView) {
        snackBarView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackBarView.getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams()).setBehavior(null);
                return true;
            }
        });
    }

    /* TODO use this */
    public static void toggleProgressBar(ProgressBar progressBar, boolean showProgressBar) {
        int visibility = showProgressBar ? View.VISIBLE : View.GONE;
        progressBar.setVisibility(visibility);
    }

}
