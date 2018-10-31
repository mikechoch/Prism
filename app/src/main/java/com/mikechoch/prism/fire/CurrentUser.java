package com.mikechoch.prism.fire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.UserPreference;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.callback.fetch.OnFetchUserProfileCallback;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CurrentUser {

    public static PrismUser prismUser;
    public static UserPreference preference;

    static ArrayList<PrismPost> news_feed;

    static DatabaseReference notificationsReference;
    static ChildEventListener notificationsListener;
    static Handler notificationsHandler;
    static Runnable notificationsRunnable;

    /**
     * Key: String postId
     * Value: long timestamp
    **/
    static HashMap<String, Long> liked_posts_map;
    static HashMap<String, Long> reposted_posts_map;
    static HashMap<String, Long> uploaded_posts_map;

    /** ArrayList of PrismPost objects for above structures **/
    private static ArrayList<PrismPost> liked_posts;
    private static ArrayList<PrismPost> reposted_posts;
    private static ArrayList<PrismPost> uploaded_posts;
    private static ArrayList<PrismPost> uploaded_and_reposted_posts;

    /**
     * Key: String notificationId
     * Value: NotificationType object
     */
    private static HashMap<String, Notification> notifications_map;

    /** ArrayList of NotificationType objects for above structures **/
    private static ArrayList<Notification> notifications;

    /**
     * Key: String uid
     * Value: Long timestamp
     */
    static HashMap<String, Long> followers;
    static HashMap<String, Long> followings;

    /**
     *
     * @param context
     * @param intent
     */
    public static void prepareAppForUser(Context context, Intent intent) {
        refreshUserProfile(context, intent);
    }

    /**
     * Returns True if CurrentUser is following given PrismUser
     * @param prismUser
     * @return
     */
    public static boolean isFollowingPrismUser(PrismUser prismUser) {
        return followings.containsKey(prismUser.getUid());
    }

    /**
     * Adds given prismUser to CurrentUser's followings HashMap
     * @param prismUser
     * @param timestamp
     */
    static void followUser(PrismUser prismUser, Long timestamp) {
        followings.put(prismUser.getUid(), timestamp);
    }


    /**
     * Removes given PrismUser from CurrentUser's followings HashMap
     * @param prismUser
     */
    static void unfollowUser(PrismUser prismUser) {
        if (followings.containsKey(prismUser.getUid())) {
            followings.remove(prismUser.getUid());
        }
    }

    /**
     * Returns True if given PrismUser is a follower of CurrentUser
     * @param prismUser
     * @return
     */
    public static boolean isPrismUserFollower(PrismUser prismUser) {
        return followers.containsKey(prismUser.getUid());
    }


    /**
     * Returns True if CurrentUser has liked given prismPost
     * @param prismPost
     * @return
     */
    public static boolean hasLiked(PrismPost prismPost) {
        return liked_posts != null && liked_posts_map.containsKey(prismPost.getPostId());
    }

    /**
     * Adds prismPost to CurrentUser's liked_posts list and hashMap
     * @param prismPost
     */
    static void likePost(PrismPost prismPost) {
        liked_posts.add(prismPost);
        liked_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds list of liked prismPosts to CurrentUser's liked_posts list
     * @param likedPosts
     */
    static void likePosts(ArrayList<PrismPost> likedPosts) {
        liked_posts.addAll(likedPosts);
    }

    /**
     * Removes prismPost from CurrentUser's liked_posts list and hashMap
     * @param prismPost
     */
    static void unlikePost(PrismPost prismPost) {
        liked_posts.remove(prismPost);
        liked_posts_map.remove(prismPost.getPostId());
    }

    /**
     * Returns True if CurrentUser has reposted given prismPost
     * @param prismPost
     * @return
     */
    public static boolean hasReposted(PrismPost prismPost) {
        return hasReposted(prismPost.getPostId());
    }

    public static boolean hasReposted(String prismPostId) {
        return reposted_posts_map != null && reposted_posts_map.containsKey(prismPostId);
    }

    /**
     * Adds prismPost to CurrentUser's reposted_posts list and hashMap
     * @param prismPost
     */
    static void repostPost(PrismPost prismPost) {
        reposted_posts.add(prismPost);
        reposted_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds the list of reposted prismPosts to CurrentUser's reposted_posts list
     * @param repostedPosts
     */
    static void repostPosts(ArrayList<PrismPost> repostedPosts) {
        reposted_posts.addAll(repostedPosts);
        for (PrismPost prismPost : repostedPosts) {
            prismPost.setIsReposted(true);
        }
    }

    /**
     * Removes prismPost from CurrentUser's repost_posts list and hashMap
     * @param prismPost
     */
    static void unrepostPost(PrismPost prismPost) {
        reposted_posts.remove(prismPost);
        reposted_posts_map.remove(prismPost.getPostId());
    }

    /**
     * Adds prismPost to CurrentUser's uploaded_posts list and hashMap
     * @param prismPost
     */
    static void uploadPost(PrismPost prismPost) {
        uploaded_posts.add(prismPost);
        uploaded_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds the list of uploaded prismPosts to CurrentUser's uploaded_posts list and hashMap
     * @param uploadedPosts
     */
    static void uploadPosts(ArrayList<PrismPost> uploadedPosts) {
        uploaded_posts.addAll(uploadedPosts);
    }

    /**
     * Removes prismPost from CurrentUser's uploaded_posts list and hashMap
     * @param prismPost
     */
    static void deletePost(PrismPost prismPost) {
        uploaded_posts.remove(prismPost);
        uploaded_posts_map.remove(prismPost.getPostId());
    }

    /**
     *
     * @param notification
     * @param notificationId
     */
    static void addNotification(Notification notification, String notificationId) {
        if (!notifications_map.containsKey(notificationId)) {
            notifications.add(0, notification);
            notifications_map.put(notificationId, notification);
        }
    }

    /**
     *
     * @param oldNotificationId
     */
    static void removeNotification(String oldNotificationId) {
        Notification oldNotification = notifications_map.get(oldNotificationId);
        notifications.remove(oldNotification);
        notifications_map.remove(oldNotificationId);
    }

    /**
     * Creates prismUser for CurrentUser and refreshes/updates the
     * list of posts uploaded, liked, and reposted by CurrentUser.
     * Also fetches user's followers and followings.
     */
    private static void refreshUserProfile(Context context, Intent intent) {
        prismUser = null;
        preference = null;

        liked_posts = new ArrayList<>();
        reposted_posts = new ArrayList<>();
        uploaded_posts = new ArrayList<>();
        uploaded_and_reposted_posts = new ArrayList<>();

        liked_posts_map = new HashMap<>();
        reposted_posts_map = new HashMap<>();
        uploaded_posts_map = new HashMap<>();

        followers = new HashMap<>();
        followings = new HashMap<>();

        notifications_map = new HashMap<>();
        notifications = new ArrayList<>();

        if (Helper.isNetworkAvailable(context)) {
            DatabaseRead.constructCurrentUserProfile(new OnFetchUserProfileCallback() {
                @Override
                public void onSuccess() {
                    CurrentUser.refreshInterface(context, intent);
                }

                @Override
                public void onFailure() { }
            });
        }
    }

    /**
     *
     * @param context
     */
    public static void refreshUserProfile(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Default.ONLY_PERFORM_REFRESH_EXTRA, true);
        refreshUserProfile(context, intent);
    }

    /**
     * Returns list of CurrentUser.uploaded_posts
     */
    public static ArrayList<PrismPost> getUserUploads() {
        return uploaded_posts;
    }

    /**
     * Returns list of CurrentUser.liked_posts
     */
    public static ArrayList<PrismPost> getUserLikes() {
        return liked_posts;
    }

    /**
     * Returns list of CurrentUser.reposted_posts
     */
    public static ArrayList<PrismPost> getUserReposts() {
        return reposted_posts;
    }

    public static ArrayList<PrismPost> getUserUploadsAndReposts() {
        return uploaded_and_reposted_posts;
    }

    /**
     * Prepares combined list of CurrentUser's uploaded
     * and reposted prismPosts
     */
    static void combineUploadsAndReposts() {
        uploaded_and_reposted_posts.addAll(uploaded_posts);
        uploaded_and_reposted_posts.addAll(reposted_posts);

        Collections.sort(uploaded_and_reposted_posts, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return Long.compare(p1.getTimestamp(), p2.getTimestamp());
            }
        });
    }

    /**
     * Returns list of uid of Current user's followers
     */
    static ArrayList<String> getFollowers() {
        return new ArrayList<>(followers.keySet());
    }

    /**
     * Returns list of uid of Current user's followings
     */
    static ArrayList<String> getFollowings() {
        return new ArrayList<>(followings.keySet());
    }

    /**
     *
     * @return
     */
    public static ArrayList<Notification> getNotifications() {
        return notifications;
    }

    /**
     *
     * @param context
     * @param intent
     */
    private static void refreshInterface(Context context, Intent intent) {
        // Handle notification firebase token related activities
        DatabaseAction.handleFirebaseTokenRefreshActivities(context);
        IncomingNotificationController.initializeNotifications(context);

        if (intent.getBooleanExtra(Default.ONLY_PERFORM_REFRESH_EXTRA, false)) {
            //TODO: We need to call notify data set changed here
//            MainFeedFragment.mainContentRecyclerViewAdapter.notifyDataSetChanged();
            MainActivity.updateProfileFragmentInterface(context);

        } else {
            Intent[] intents;
            Intent mainIntent = new Intent(context, MainActivity.class);
            if (intent.filterEquals(mainIntent)) {
                intents = new Intent[]{intent};
            } else {
                intents = new Intent[]{mainIntent, intent};
            }
            context.startActivities(intents);
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            ((Activity) context).finish();
        }

    }

    /**
     *
     * @return
     */
    public static FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     *
     * @return
     */
    public static boolean isUserSignedIn() {
        FirebaseUser user = getFirebaseUser();
        if (user == null) {
            return false;
        }
        if (!user.isEmailVerified()) {
            performSignOut();
            return false;
        }
        return true;
    }

    /**
     *
     */
    public static void performSignOut() {
        if (CurrentUser.notificationsListener != null) {
            CurrentUser.notificationsReference.removeEventListener(CurrentUser.notificationsListener);
        }
        if (CurrentUser.notificationsRunnable != null) {
            CurrentUser.notificationsHandler.removeCallbacks(CurrentUser.notificationsRunnable);
        }

        CurrentUser.prismUser = null;
        CurrentUser.preference = null;

        FirebaseAuth.getInstance().signOut();

//       CurrentUser.news_feed = null;  // TODO should do this?

    }

}
