package com.mikechoch.prism.fire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.mikechoch.prism.adapter.PrismPostRecyclerViewAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.UserPreference;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.callback.OnFetchUserProfileCallback;
import com.mikechoch.prism.fire.callback.OnMaintenanceCheckCallback;
import com.mikechoch.prism.fragment.MainContentFragment;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.NotificationType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAction {

    /**
     * Adds prismPost to CurrentUser's USER_LIKES section
     * Adds userId to prismPost's LIKED_USERS section
     * Performs like locally on CurrentUser
     */
    public static void performLike(PrismPost prismPost) {
        String currentUserId = CurrentUser.prismUser.getUid();
        long actionTimestamp = Calendar.getInstance().getTimeInMillis();

        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPost.getPostId());
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);

        postReference.child(Key.DB_REF_POST_LIKED_USERS)
                .child(currentUserId)
                .setValue(actionTimestamp);

        currentUserReference.child(Key.DB_REF_USER_LIKES)
                .child(prismPost.getPostId())
                .setValue(actionTimestamp);

        CurrentUser.likePost(prismPost);

        if (!Helper.isPrismUserCurrentUser(prismPost.getUid())) {
            OutgoingNotificationController.prepareLikeNotification(prismPost, actionTimestamp);
        }

    }

    /**
     * Removes prismPost to CurrentUser's USER_LIKES section
     * Removes userId to prismPost's LIKED_USERS section
     * Performs unlike locally on CurrentUser*
     */
    public static void performUnlike(PrismPost prismPost) {
        String currentUserId = CurrentUser.prismUser.getUid();

        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPost.getPostId());
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);

        postReference.child(Key.DB_REF_POST_LIKED_USERS)
                .child(currentUserId)
                .removeValue();

        currentUserReference.child(Key.DB_REF_USER_LIKES)
                .child(prismPost.getPostId())
                .removeValue();

        CurrentUser.unlikePost(prismPost);

        OutgoingNotificationController.revokeLikeNotification(prismPost);

    }

    /**
     * Adds prismPost to CurrentUser's USER_REPOSTS section
     * Adds userId to prismPost's REPOSTED_USERS section
     * Performs repost locally on CurrentUser
     */
    public static void performRepost(PrismPost prismPost) {
        String currentUserId = CurrentUser.prismUser.getUid();
        long timestamp = Calendar.getInstance().getTimeInMillis();

        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPost.getPostId());
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);

        postReference.child(Key.DB_REF_POST_REPOSTED_USERS)
                .child(currentUserId)
                .setValue(timestamp);

        currentUserReference.child(Key.DB_REF_USER_REPOSTS)
                .child(prismPost.getPostId())
                .setValue(timestamp);

        CurrentUser.repostPost(prismPost);

        if (!Helper.isPrismUserCurrentUser(prismPost.getUid())) {
            OutgoingNotificationController.prepareRepostNotification(prismPost, timestamp);
        }
    }

    /**
     * Removes prismPost to CurrentUser's USER_REPOSTS section
     * Removes userId to prismPost's REPOSTED_USERS section
     * Performs unrepost locally on CurrentUser
     */
    public static void performUnrepost(PrismPost prismPost) {
        String currentUserId = CurrentUser.prismUser.getUid();

        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPost.getPostId());
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);

        postReference.child(Key.DB_REF_POST_REPOSTED_USERS)
                .child(currentUserId)
                .removeValue();

        currentUserReference.child(Key.DB_REF_USER_REPOSTS)
                .child(prismPost.getPostId())
                .removeValue();

        CurrentUser.unrepostPost(prismPost);

        OutgoingNotificationController.revokeRepostNotification(prismPost);
    }

    /**
     * Removes prismPost image from Firebase Storage. When that task is
     * successfully completed, the likers and reposters for the post
     * are fetched and the postId is deleted under each liker and reposter's
     * USER_LIKES and USER_REPOSTS section. Then the post is deleted under
     * USER_UPLOADS for the post owner. And then the post itself is
     * deleted from ALL_POSTS. Finally, the mainRecyclerViewAdapter is refreshed
     */
    public static void deletePost(PrismPost prismPost) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;

        FirebaseStorage.getInstance().getReferenceFromUrl(prismPost.getImage())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String postId = prismPost.getPostId();

                    allPostsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot postSnapshot) {
                            if (postSnapshot.exists()) {

                                DeleteHelper.deleteLikedUsers(postSnapshot, prismPost);

                                DeleteHelper.deleteRepostedUsers(postSnapshot, prismPost);

                                DeleteHelper.deletePostFromUserUploads(prismPost);

                                DeleteHelper.deletePostFromAllPosts(prismPost);

                                DeleteHelper.deletePostUnderItsHashTags(prismPost);

                                DeleteHelper.deletePostRelatedNotifications(prismPost);

                                CurrentUser.deletePost(prismPost);

                                // Update UI after the post is deleted
                                PrismPostRecyclerViewAdapter.prismPostArrayList.remove(prismPost);
                                refreshMainRecyclerViewAdapter();

                            } else {
                                Log.wtf(Default.TAG_DB, Message.NO_DATA);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                            Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
                        }
                    });

                } else {
                    Log.e(Default.TAG_DB, Message.POST_DELETE_FAIL);
                }
            }
        });
    }

    /**
     * Adds prismUser's uid to CurrentUser's FOLLOWERS section and then
     * adds CurrentUser's uid to prismUser's FOLLOWINGS section
     */
    public static void followUser(PrismUser prismUser) {
        String currentUserId = CurrentUser.prismUser.getUid();
        long timestamp = Calendar.getInstance().getTimeInMillis();

        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);
        DatabaseReference userReference = Default.USERS_REFERENCE.child(prismUser.getUid());

        userReference.child(Key.DB_REF_USER_FOLLOWERS)
                .child(currentUserId)
                .setValue(timestamp);

        currentUserReference.child(Key.DB_REF_USER_FOLLOWINGS)
                .child(prismUser.getUid())
                .setValue(timestamp);

        CurrentUser.followUser(prismUser, timestamp);

        if (!Helper.isPrismUserCurrentUser(prismUser.getUid())) {
            OutgoingNotificationController.prepareFollowNotification(prismUser, timestamp);
        }
    }

    /**
     * Removes prismUser's uid from CurrentUser's FOLLOWERS section and then
     * removes CurrentUser's uid from prismUser's FOLLOWINGS section
     */
    public static void unfollowUser(PrismUser prismUser) {
        String currentUserId = CurrentUser.prismUser.getUid();

        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(currentUserId);
        DatabaseReference userReference = Default.USERS_REFERENCE.child(prismUser.getUid());

        userReference.child(Key.DB_REF_USER_FOLLOWERS)
                .child(currentUserId)
                .removeValue();

        currentUserReference.child(Key.DB_REF_USER_FOLLOWINGS)
                .child(prismUser.getUid())
                .removeValue();

        CurrentUser.unfollowUser(prismUser);

        OutgoingNotificationController.revokeFollowNotification(prismUser);

    }

    public static void updatePreferenceForPushNotification(NotificationType type, boolean allowPushNotification) {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());

        currentUserReference.child(Key.DB_REF_USER_PREFERENCES)
                .child(type.getdbUserNotifPrefKey())
                .setValue(allowPushNotification);

        CurrentUser.preference.setPushNotificationPreference(type, allowPushNotification);
    }

    public static void updateViewedTimestampForAllNotifications() {
        long timestamp = System.currentTimeMillis();
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());

        currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot allNotificationSnapshots) {
                        if (allNotificationSnapshots.exists()) {
                            for (DataSnapshot notificationSnapshot : allNotificationSnapshots.getChildren()) {
                                notificationSnapshot
                                        .child(Key.NOTIFICATION_VIEWED_TIMESTAMP)
                                        .getRef().setValue(timestamp);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }


    /**
     * Creates prismUser for CurrentUser
     * Then fetches CurrentUser's liked, reposted and uploaded posts
     * And then refresh the mainRecyclerViewAdapter
     */
    static void constructCurrentUserProfile(OnFetchUserProfileCallback callback) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                DataSnapshot currentUserSnapshot = usersSnapshot.child(CurrentUser.getFirebaseUser().getUid());
                if (currentUserSnapshot.exists()) {
                    CurrentUser.prismUser = Helper.constructPrismUserObject(currentUserSnapshot);
                    CurrentUser.preference = ProfileFetcher.fetchUserPreferences(currentUserSnapshot);

                    CurrentUser.liked_posts_map = ProfileFetcher.fetchLikedPosts(currentUserSnapshot);
                    CurrentUser.reposted_posts_map = ProfileFetcher.fetchRepostedPosts(currentUserSnapshot);
                    CurrentUser.uploaded_posts_map = ProfileFetcher.fetchUploadedPosts(currentUserSnapshot);

                    CurrentUser.followers = ProfileFetcher.fetchUserFollowers(currentUserSnapshot);
                    CurrentUser.followings = ProfileFetcher.fetchUserFollowings(currentUserSnapshot);

                    populateUserProfilePosts(usersSnapshot, callback);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
                callback.onFailure();
            }
        });
    }

    private static void populateUserProfilePosts(DataSnapshot usersSnapshot, OnFetchUserProfileCallback callback) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allPostsSnapshot) {
                if (allPostsSnapshot.exists()) {
                    ArrayList<PrismPost> userLikes = ProfileFetcher.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.liked_posts_map);
                    ArrayList<PrismPost> userReposts = ProfileFetcher.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.reposted_posts_map);
                    ArrayList<PrismPost> userUploads = ProfileFetcher.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.uploaded_posts_map);

                    CurrentUser.likePosts(userLikes);
                    CurrentUser.repostPosts(userReposts);
                    CurrentUser.uploadPosts(userUploads);
                    CurrentUser.combineUploadsAndReposts();
                    // TODO: constructNewsFeed();
                }
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, Message.FETCH_POST_INFO_FAIL, databaseError.toException());
                callback.onFailure();
            }
        });
    }


    private static void constructNewsFeed(Context context, Intent intent) {
        ArrayList<String> followings = CurrentUser.getFollowings();
        ArrayList<Pair<String, PrismUser>> listOfPrismPosts = new ArrayList<>();
        CurrentUser.news_feed = new ArrayList<>();
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                for (String userId : followings) {
                    DataSnapshot uploadSnapshot = usersSnapshot.child(userId).child(Key.DB_REF_USER_UPLOADS);
                    if (uploadSnapshot.exists()) {
                        DataSnapshot userSnapshot = usersSnapshot.child(userId);
                        PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                        HashMap<String, Long> uploads = new HashMap<>((Map)uploadSnapshot.getValue());
                        for (String postId : uploads.keySet()) {
                            listOfPrismPosts.add(Pair.create(postId, prismUser));
                        }
                    }
                }
                allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot allPostsSnapshot) {
                        for (Pair pair : listOfPrismPosts) {
                            String postId = (String) pair.first;
                            DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
                            PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                            prismPost.setPrismUser((PrismUser) pair.second);
                            CurrentUser.news_feed.add(prismPost);
                        }

                        Collections.sort(CurrentUser.news_feed);

                        // TODO --> CurrentUser.refreshInterface(context, intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }



    /**
     * Refresh mainContentFragment's recyclerViewAdapter
     */
    static void refreshMainRecyclerViewAdapter() {
        MainContentFragment.mainContentRecyclerViewAdapter.notifyDataSetChanged();
    }

    public static void handleFirebaseTokenRefreshActivities(Context context) {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        boolean isUserLoggedIn = CurrentUser.getFirebaseUser() != null;
        if (firebaseToken == null || !isUserLoggedIn) {
            return;
        }

        // Update token in local storage
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(Default.FIREBASE_TOKEN, firebaseToken).apply();

        // Update token in cloud
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        currentUserReference.child(Key.USER_TOKEN).setValue(firebaseToken);
    }

    /**
     *
     * @param context
     * @param prismPost
     */
    public static void reportPost(Context context, PrismPost prismPost) {
        DatabaseReference contentReviewReference = Default.CONTENT_REVIEW_REFERENCE;
        contentReviewReference
                .child(prismPost.getUid())
                .child(CurrentUser.prismUser.getUid())
                .setValue(System.currentTimeMillis()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Helper.toast(context, "Thank you for reporting this post");
                    Log.d(Default.TAG_DB, Message.POST_REPORTED_SUCCESS);
                    Log.e(Default.TAG_DB, prismPost.getUid());
                } else {
                    Helper.toast(context, "Unable to report post, please try again later");
                    Log.e(Default.TAG_DB, Message.POST_REPORTED_FAIL, task.getException());
                }
            }
        });
    }

    public static void performMaintenanceCheck(OnMaintenanceCheckCallback callback) {
        DatabaseReference appStatusReference = Default.APP_STATUS_REFERENCE;
        appStatusReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot appStatusSnapshot) {
                if (appStatusSnapshot.exists()) {
                    Boolean isActive = (Boolean) appStatusSnapshot.child(Key.STATUS_IS_ACTIVE).getValue();
                    if (isActive) {
                        callback.onStatusActive();
                    } else {
                        String message = (String) appStatusSnapshot.child(Key.STATUS_MESSAGE).getValue();
                        callback.onStatusUnderMaintenance(message);
                    }
                } else {
                    callback.onStatusCheckFailed(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onStatusCheckFailed(databaseError.toException());
            }
        });
    }

}


class ProfileFetcher {

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static HashMap<String, Long> fetchLikedPosts(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_LIKES)) {
            return (HashMap<String, Long>) currentUserSnapshot.child(Key.DB_REF_USER_LIKES).getValue();
        }
        return new HashMap<>();
    }

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static HashMap<String, Long> fetchRepostedPosts(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_REPOSTS)) {
            return (HashMap<String, Long>) currentUserSnapshot.child(Key.DB_REF_USER_REPOSTS).getValue();
        }
        return new HashMap<>();
    }

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static HashMap<String, Long> fetchUploadedPosts(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_UPLOADS)) {
            return (HashMap<String, Long>) currentUserSnapshot.child(Key.DB_REF_USER_UPLOADS).getValue();
        }
        return new HashMap<>();
    }

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static HashMap<String, Long> fetchUserFollowers(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_FOLLOWERS)) {
            return (HashMap<String, Long>) currentUserSnapshot.child(Key.DB_REF_USER_FOLLOWERS).getValue();
        }
        return new HashMap<>();
    }

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static HashMap<String, Long> fetchUserFollowings(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_FOLLOWINGS)) {
            return (HashMap<String, Long>) currentUserSnapshot.child(Key.DB_REF_USER_FOLLOWINGS).getValue();
        }
        return new HashMap<>();
    }

    /**
     *
     * @param currentUserSnapshot
     * @return
     */
    static UserPreference fetchUserPreferences(DataSnapshot currentUserSnapshot) {
        if (currentUserSnapshot.hasChild(Key.DB_REF_USER_PREFERENCES)) {
            DataSnapshot preferenceSnapshot = currentUserSnapshot.child(Key.DB_REF_USER_PREFERENCES);
            boolean allowLike = (boolean) preferenceSnapshot.child(Key.PREFERENCE_ALLOW_LIKE_NOTIFICATION).getValue();
            boolean allowRepost = (boolean) preferenceSnapshot.child(Key.PREFERENCE_ALLOW_REPOST_NOTIFICATION).getValue();
            boolean allowFollow = (boolean) preferenceSnapshot.child(Key.PREFERENCE_ALLOW_FOLLOW_NOTIFICATION).getValue();
            return new UserPreference(allowLike, allowRepost, allowFollow);
        }
        return Default.USER_PREFERENCE;
    }

    /**
     * Takes in a hashMap of prismPost postIds and also takes in dataSnapshot
     * referencing to `ALL_POSTS` and `USERS` and constructs PrismPost objects
     * for each postId in the hashMap and puts all prismPost objects in a list.
     * Gets called for getting user liked_posts, reposted_posts, and uploaded_posts
     */
    static ArrayList<PrismPost> getListOfPrismPosts(DataSnapshot allPostsSnapshot,
                                                            DataSnapshot usersSnapshot,
                                                            HashMap<String, Long> mapOfPostIds)
    {
        ArrayList<PrismPost> listOfPrismPosts = new ArrayList<>();
        for (String postId : mapOfPostIds.keySet()) {
            DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
            if (postSnapshot.exists()) {
                PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                DataSnapshot userSnapshot = usersSnapshot.child(prismPost.getUid());
                PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                prismPost.setPrismUser(prismUser);

                listOfPrismPosts.add(prismPost);
            }
        }
        return listOfPrismPosts;
    }


}



class DeleteHelper {

    /**
     * Helper method that goes to all users who have liked the given
     * prismPost and deletes the postId under their USER_LIKES section
     */
    static void deleteLikedUsers(DataSnapshot postSnapshot, PrismPost post) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        HashMap<String, String> likedUsers = new HashMap<>();
        if (postSnapshot.child(Key.DB_REF_POST_LIKED_USERS).getChildrenCount() > 0) {
            likedUsers.putAll((Map) postSnapshot.child(Key.DB_REF_POST_LIKED_USERS).getValue());
            for (String userId : likedUsers.keySet()) {
                usersReference.child(userId)
                        .child(Key.DB_REF_USER_LIKES)
                        .child(post.getPostId())
                        .removeValue();
            }
        }
    }

    /**
     * Helper method that goes to all users who have reposted the given
     * prismPost and deletes the postId under their USER_REPOSTS section
     */
    static void deleteRepostedUsers(DataSnapshot postSnapshot, PrismPost post) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        HashMap<String, String> repostedUsers = new HashMap<>();
        if (postSnapshot.child(Key.DB_REF_POST_REPOSTED_USERS).getChildrenCount() > 0) {
            repostedUsers.putAll((Map) postSnapshot.child(Key.DB_REF_POST_REPOSTED_USERS).getValue());
            for (String userId : repostedUsers.keySet()) {
                usersReference.child(userId)
                        .child(Key.DB_REF_USER_REPOSTS)
                        .child(post.getPostId())
                        .removeValue();
            }
        }
    }

    static void deletePostFromUserUploads(PrismPost prismPost) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.child(prismPost.getPrismUser().getUid())
                .child(Key.DB_REF_USER_UPLOADS)
                .child(prismPost.getPostId()).removeValue();
    }

    static void deletePostUnderItsHashTags(PrismPost prismPost) {
        ArrayList<String> listOfHashTags = Helper.parseDescriptionForTags(prismPost.getCaption());
        DatabaseReference tagsReference = Default.TAGS_REFERENCE;

        for (String hashTag : listOfHashTags) {
            tagsReference.child(hashTag).removeValue();
        }
    }

    static void deletePostRelatedNotifications(PrismPost prismPost) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.child(prismPost.getPrismUser().getUid())
            .child(Key.DB_REF_USER_NOTIFICATIONS).orderByKey().startAt(prismPost.getPostId())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot notifSnapshot : dataSnapshot.getChildren()) {
                            notifSnapshot.getRef().removeValue();
                        }
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static void deletePostFromAllPosts(PrismPost prismPost) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        allPostsReference.child(prismPost.getPostId()).removeValue();
    }



}

