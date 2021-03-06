package com.mikechoch.prism.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.RelativeDateTimeFormatter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.PrismTagActivity;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PostBasedNotification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.ProfilePicture;
import com.mikechoch.prism.attribute.UserBasedNotification;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.TimeUnit;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.type.NotificationType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
     *
     * @param message
     * @param splitIndex
     * @return
     */
    public static SpannableString createUsernameClickableSpan(Context context, PostBasedNotification postBasedNotification, String message, int splitIndex) {
        SpannableString spannableString = new SpannableString(message);

        ClickableSpan usernameClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                IntentHelper.intentToUserProfileActivity(context, postBasedNotification.getPrismPost().getPrismUser());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                Typeface typeface = postBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
                ds.setUnderlineText(false);
                ds.setTypeface(typeface);
            }
        };
        spannableString.setSpan(usernameClickableSpan, 0, splitIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (message.length() >= splitIndex + 1) {
            ClickableSpan othersClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    IntentHelper.intentToDisplayUsersActivity(context, postBasedNotification.getPrismPost().getPostId(), postBasedNotification.getNotificationType().getNotifUserDisplayCode());
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    Typeface typeface = postBasedNotification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
                    ds.setUnderlineText(false);
                    ds.setTypeface(typeface);
                }
            };
            spannableString.setSpan(othersClickableSpan, splitIndex + 1, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        return spannableString;
    }

    /**
     * Takes in the downloadUrl that was create in cloud and reference to the post that
     * got created in cloud and prepares the PrismPost object that will be pushed
     */
    public static PrismPost constructPrismPostObjectForUpload(Uri downloadUrl, String imageDescription) {
        String imageUri = downloadUrl.toString();
        String description = imageDescription;
        String userId = CurrentUser.getFirebaseUser().getUid();
        Long timestamp = Helper.getNegativeCurrentTimestamp();
        return new PrismPost(imageUri, description, userId, timestamp);
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
        prismPost.setIsReposted(CurrentUser.hasReposted(postSnapshot.getKey()));
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

    public static Notification constructNotification(DataSnapshot notificationSnapshot) {
        Notification notification = null;

        String notificationId = notificationSnapshot.getKey();
        NotificationType notificationType = NotificationType.getType(notificationId);
        long actionTimestamp = (long) notificationSnapshot.child(Key.NOTIFICATION_ACTION_TIMESTAMP).getValue();
        long viewedTimestamp = (long) notificationSnapshot.child(Key.NOTIFICATION_VIEWED_TIMESTAMP).getValue();
        String mostRecentUserId = (String) notificationSnapshot.child(Key.NOTIFICATION_MOST_RECENT_USER).getValue();

        boolean viewed = viewedTimestamp > actionTimestamp;

        switch (notificationType) {
            case LIKE:
            case REPOST:
                notification = new PostBasedNotification();
                break;
            case FOLLOW:
                notification = new UserBasedNotification();
                break;
        }

        notification.setNotificationId(notificationId);
        notification.setNotificationType(notificationType);
        notification.setActionTimestamp(actionTimestamp);
        notification.setMostRecentUid(mostRecentUserId);
        notification.setViewed(viewed);

        return notification;
    }

    /**
     *
     */
    public static boolean isPrismUserCurrentUser(PrismUser prismUser) {
        return CurrentUser.getUid().equals(prismUser.getUid());
    }

    public static boolean isPrismUserCurrentUser(String prismUserId) {
        return CurrentUser.getUid().equals(prismUserId);
    }


    /**
     *
     */
    public static String getSingularOrPluralText(String string, int count) {
        return String.valueOf(count) + " " + (count == 1 ? string : string + "s");
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
        if (time < 0) {
            time *= -1;
        }
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
                while (i < description.length() && !Default.ILLEGAL_HASHTAG_CHARS.contains(description.charAt(i))) {
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
                while (i < string.length() && !Default.ILLEGAL_HASHTAG_CHARS.contains(string.charAt(i))) {
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
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toast(Context context, String message, boolean longLength) {
        if (longLength) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast(context, message);
        }
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
    public static void disableSnackBarSwipeDismiss(final View snackBarView) {
        snackBarView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackBarView.getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams()).setBehavior(null);
                return true;
            }
        });
    }

    /**
     *
     * @param progress
     * @param min
     * @param max
     * @return
     */
    public static float getEditSeekBarValue(int progress, float min, float max) {
        return (((progress / 200.0f) * (max - min)) + min);
    }


    public static long getNegativeCurrentTimestamp() {
        return -1 * System.currentTimeMillis();
    }

    /**
     *
     * @param progress
     * @param min
     * @param max
     * @return
     */
    public static float getRotationSeekBarValue(int progress, float min, float max) {
        return (((progress / 100.0f) * (max - min)) + min);
    }

    /* TODO use this */
    public static void toggleProgressBar(ProgressBar progressBar, boolean showProgressBar) {
        int visibility = showProgressBar ? View.VISIBLE : View.GONE;
        progressBar.setVisibility(visibility);
    }

}
