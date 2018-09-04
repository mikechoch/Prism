package com.mikechoch.prism.fire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.mikechoch.prism.activity.NoInternetActivity;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.UserPreference;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.callback.OnFetchUserProfileCallback;
import com.mikechoch.prism.fragment.MainContentFragment;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by parth on 1/25/18.
 */

public class CurrentUser {

    /*
     * Globals
     */
    private static FirebaseAuth auth;
    public static FirebaseUser firebaseUser;
    private static DatabaseReference currentUserReference;
    private static DatabaseReference allPostReference;
    
    public static PrismUser prismUser;
    public static UserPreference preference;

    public static ArrayList<PrismPost> news_feed;

    public static DatabaseReference notificationsReference;
    public static ChildEventListener notificationsListener;
    public static Handler notificationsHandler;
    public static Runnable notificationsRunnable;

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
     * Value: Notification object
     */
    private static HashMap<String, Notification> notifications_map;

    /** ArrayList of Notification objects for above structures **/
    private static ArrayList<Notification> notifications;

    /**
     * Key: String uid
     * Value: Long timestamp
     */
    static HashMap<String, Long> followers;
    static HashMap<String, Long> followings;



    private CurrentUser(Context context, Intent intent) {
        updateLocalCurrentUser();
        currentUserReference = Default.USERS_REFERENCE.child(firebaseUser.getUid());
        allPostReference = Default.ALL_POSTS_REFERENCE;
        
        refreshUserProfile(context, intent);
    }

    public static void prepareAppForUser(Context context, Intent intent) {
        if (Helper.isNetworkAvailable(context)) {
            new CurrentUser(context, intent);
        } else {
            Intent noInternetIntent = new Intent(context, NoInternetActivity.class);
            context.startActivity(noInternetIntent);
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            ((Activity) context).finish();
        }
    }

    /**
     *
     */
    public static void updateLocalCurrentUser() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    /**
     * Returns True if CurrentUser is following given PrismUser
     */
    public static boolean isFollowingPrismUser(PrismUser prismUser) {
        return followings.containsKey(prismUser.getUid());
    }

    /**
     * Adds given prismUser to CurrentUser's followings HashMap
     */
    static void followUser(PrismUser prismUser, Long timestamp) {
        followings.put(prismUser.getUid(), timestamp);
    }


    /**
     * Removes given PrismUser from CurrentUser's followings HashMap
     */
    static void unfollowUser(PrismUser prismUser) {
        if (followings.containsKey(prismUser.getUid())) {
            followings.remove(prismUser.getUid());
        }
    }

    /**
     * Returns True if given PrismUser is a follower of CurrentUser
     */
    public static boolean isPrismUserFollower(PrismUser prismUser) {
        return followers.containsKey(prismUser.getUid());
    }


    /**
     * Returns True if CurrentUser has liked given prismPost
     */
    public static boolean hasLiked(PrismPost prismPost) {
        return liked_posts != null && liked_posts_map.containsKey(prismPost.getPostId());
    }

    /**
     * Adds prismPost to CurrentUser's liked_posts list and hashMap
     */
    static void likePost(PrismPost prismPost) {
        liked_posts.add(prismPost);
        liked_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds list of liked prismPosts to CurrentUser's liked_posts list
     */
    static void likePosts(ArrayList<PrismPost> likedPosts) {
        liked_posts.addAll(likedPosts);
    }

    /**
     * Removes prismPost from CurrentUser's liked_posts list and hashMap
     */
    static void unlikePost(PrismPost prismPost) {
        liked_posts.remove(prismPost);
        liked_posts_map.remove(prismPost.getPostId());
    }

    /**
     * Returns True if CurrentUser has reposted given prismPost
     */
    public static boolean hasReposted(PrismPost prismPost) {
        return reposted_posts_map != null && reposted_posts_map.containsKey(prismPost.getPostId());
    }

    /**
     * Adds prismPost to CurrentUser's reposted_posts list and hashMap
     */
    static void repostPost(PrismPost prismPost) {
        reposted_posts.add(prismPost);
        reposted_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds the list of reposted prismPosts to CurrentUser's reposted_posts list
     */
    static void repostPosts(ArrayList<PrismPost> repostedPosts) {
        reposted_posts.addAll(repostedPosts);
        for (PrismPost prismPost : repostedPosts) {
            prismPost.setIsReposted(true);
        }
    }

    /**
     * Removes prismPost from CurrentUser's repost_posts list and hashMap
     */
    static void unrepostPost(PrismPost prismPost) {
        reposted_posts.remove(prismPost);
        reposted_posts_map.remove(prismPost.getPostId());
    }

    /**
     *
     * Adds prismPost to CurrentUser's uploaded_posts list and hashMap
     */
    static void uploadPost(PrismPost prismPost) {
        uploaded_posts.add(prismPost);
        uploaded_posts_map.put(prismPost.getPostId(), prismPost.getTimestamp());
    }

    /**
     * Adds the list of uploaded prismPosts to CurrentUser's uploaded_posts list and hashMap
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
     */
    static void addNotification(Notification notification, String notificationId) {
        if (!notifications_map.containsKey(notificationId)) {
            notifications.add(0, notification);
            notifications_map.put(notificationId, notification);
        }
    }

    /**
     *
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
            DatabaseAction.constructCurrentUserProfile(new OnFetchUserProfileCallback() {
                @Override
                public void onSuccess() {
                    CurrentUser.refreshInterface(context, intent);
                }

                @Override
                public void onFailure() { }
            });
        }
    }

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
                return (int) (p1.getTimestamp() - p2.getTimestamp());
            }
        });
    }

    /**
     * Returns list of uid of Current user's followers
     */
    public static ArrayList<String> getFollowers() {
        return new ArrayList<>(followers.keySet());
    }

    /**
     * Returns list of uid of Current user's followings
     */
    public static ArrayList<String> getFollowings() {
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
     * TODO @Mike: Can we we put this function inside InterfaceAction?
     */
    static void updateUserProfileFragmentUI(Context context) {
        ImageView userProfileImageView = ((Activity) context).findViewById(R.id.profile_fragment_user_profile_image_view);
        TextView userProfileTextView = ((Activity) context).findViewById(R.id.profile_fragment_user_full_name_text_view);

        // TODO: Crash on fullname using tablet
        userProfileTextView.setText(prismUser.getFullName());
        Glide.with(context)
                .asBitmap()
                .thumbnail(0.05f)
                .load(prismUser.getProfilePicture().lowResUri)
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(userProfileImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        if (!prismUser.getProfilePicture().isDefault) {
                            int whiteOutlinePadding = (int) (1 * Default.scale);
                            userProfileImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                            userProfileImageView.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                        } else {
                            userProfileImageView.setPadding(0, 0, 0, 0);
                            userProfileImageView.setBackground(null);
                        }

                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        drawable.setCircular(true);
                        userProfileImageView.setImageDrawable(drawable);
                    }
                });
    }

    static void refreshInterface(Context context, Intent intent) {
        // Handle notification firebase token related activities
        DatabaseAction.handleFirebaseTokenRefreshActivities(context);
        IncomingNotificationController.initializeNotifications();

        if (intent.getBooleanExtra(Default.ONLY_PERFORM_REFRESH_EXTRA, false)) {
            MainContentFragment.mainContentRecyclerViewAdapter.notifyDataSetChanged();
            updateUserProfileFragmentUI(context);
            DiscoverController.setupDiscoverContent(context);
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

    public static void performSignOut() {
       CurrentUser.notificationsReference.removeEventListener(CurrentUser.notificationsListener);
       CurrentUser.notificationsHandler.removeCallbacks(CurrentUser.notificationsRunnable);

       CurrentUser.firebaseUser = null;
       CurrentUser.prismUser = null;
       CurrentUser.preference = null;

       FirebaseAuth.getInstance().signOut();

//       CurrentUser.news_feed = null;  // TODO should do this?


    }

}
