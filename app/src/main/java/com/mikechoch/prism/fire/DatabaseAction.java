package com.mikechoch.prism.fire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikechoch.prism.BuildConfig;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.action.OnDeletePostCallback;
import com.mikechoch.prism.callback.action.OnSendVerificationEmailCallback;
import com.mikechoch.prism.callback.action.OnUploadPostCallback;
import com.mikechoch.prism.callback.check.OnMaintenanceCheckCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.PermissionHelper;
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
        if (PermissionHelper.allowRepost(prismPost)) {
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
     *
     */
    public static void uploadPost(Uri uploadImageUri, String imageDescription, OnUploadPostCallback callback) {
        if (!PermissionHelper.allowUploadPost()) {
            callback.onPermissionDenied();
            return;
        }
        StorageReference postImageReference = Default.STORAGE_REFERENCE
                .child(Key.STORAGE_POST_IMAGES_REF)
                .child(uploadImageUri.getLastPathSegment());
        postImageReference.putFile(uploadImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                        callback.onProgressUpdate(progress);
                    }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot uploadedImageFileSnapshot) {
                // TODO CHAIN TASKS
                uploadedImageFileSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        PrismPost prismPost = Helper.constructPrismPostObjectForUpload(uri, imageDescription);

                        String postId = Default.ALL_POSTS_REFERENCE.push().getKey();
                        Map<String, Object> uploadMap = new HashMap<>();

                        UploadHelper.addPathOfPrismPostToUploadMap(uploadMap, prismPost, postId);
                        UploadHelper.addPathOfUserUploadsToUploadMap(uploadMap, prismPost, postId);
                        UploadHelper.addPathOfHashTagsToUploadMap(uploadMap, prismPost, postId);
                        UploadHelper.addPathOfFollowersFeedToUploadMap(uploadMap, prismPost, postId);

                        DatabaseReference rootReference = Default.ROOT_REFERENCE;
                        rootReference.updateChildren(uploadMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                prismPost.setPostId(postId);
                                prismPost.setPrismUser(CurrentUser.prismUser);
                                CurrentUser.uploadPost(prismPost);
                                callback.onSuccess(prismPost);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onPostUploadFail(e);
                            }
                        });
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onImageUploadFail(e);
            }
        });
    }

    /**
     * Removes prismPost image from Firebase Storage. When that task is
     * successfully completed, the likers and reposters for the post
     * are fetched and the postId is deleted under each liker and reposter's
     * USER_LIKES and USER_REPOSTS section. Then the post is deleted under
     * USER_UPLOADS for the post owner. And then the post itself is
     * deleted from ALL_POSTS. Finally, the mainRecyclerViewAdapter is refreshed
     */
    public static void deletePost(PrismPost prismPost, OnDeletePostCallback callback) {
        // This is a safety backend check
        if (!PermissionHelper.allowDeletePost(prismPost)) {
            callback.onPermissionDenied();
            return;
        }
        StorageReference postImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(prismPost.getImage());
        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPost.getPostId());

        postImageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                postReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            callback.onSuccess();

                        } else {
                            callback.onPostNotFound();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onPostDeleteFail(databaseError.toException());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onImageDeleteFail(e);
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

    /**
     *
     * @param type
     * @param allowPushNotification
     */
    public static void updatePreferenceForPushNotification(NotificationType type, boolean allowPushNotification) {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());

        currentUserReference.child(Key.DB_REF_USER_PREFERENCES)
                .child(type.getdbUserNotifPrefKey())
                .setValue(allowPushNotification);

        CurrentUser.preference.setPushNotificationPreference(type, allowPushNotification);
    }

    /**
     *
     */
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
     *
     * @param context
     */
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
                    Log.d(Default.TAG_DB, Message.POST_REPORT_SUCCESS);
                    Log.e(Default.TAG_DB, prismPost.getUid());
                } else {
                    Helper.toast(context, "Unable to report post, please try again later");
                    Log.e(Default.TAG_DB, Message.POST_REPORT_FAIL, task.getException());
                }
            }
        });
    }

    public static void sendVerificationEmail(FirebaseUser user, OnSendVerificationEmailCallback callback) {
        user.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public static void performMaintenanceCheck(OnMaintenanceCheckCallback callback) {
        DatabaseReference appStatusReference = Default.APP_STATUS_REFERENCE;
        appStatusReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot appStatusSnapshot) {
                if (appStatusSnapshot.exists()) {
                    int appVersion = BuildConfig.VERSION_CODE;
                    long minRequired = (long) appStatusSnapshot.child(Key.MIN_APP_VERSION).getValue();
                    boolean isActive = (boolean) appStatusSnapshot.child(Key.STATUS_IS_ACTIVE).getValue();

                    if (appVersion < minRequired) {
                        callback.onAppVersionTooOld();
                    } else if (isActive) {
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

class UploadHelper {

    /**
     * postId is passed in because postId is not assigned to prismPost
     * that is because we don't want postId to go into firebase as a child of prismPost
     * @param prismPost
     * @param postId
     * @return
     */
    static void addPathOfPrismPostToUploadMap(Map<String, Object> uploadMap, PrismPost prismPost, String postId) {
        String pathToPrismPost = Key.DB_REF_ALL_POSTS + "/" + postId;
        uploadMap.put(pathToPrismPost, prismPost);
    }

    static void addPathOfUserUploadsToUploadMap(Map<String, Object> uploadMap, PrismPost prismPost, String postId) {
        String pathToUserUploads = Key.DB_REF_USER_PROFILES + "/" + prismPost.getUid() + "/" + Key.DB_REF_USER_UPLOADS + "/" + postId;
        uploadMap.put(pathToUserUploads, prismPost.getTimestamp());
    }

    static void addPathOfHashTagsToUploadMap(Map<String, Object> uploadMap, PrismPost prismPost, String postId) {
        ArrayList<String> hashTags = Helper.parseDescriptionForTags(prismPost.getCaption());
        for (String hashTag : hashTags) {
            String pathToHashTag = Key.DB_REF_TAGS + "/" + hashTag + "/" + postId;
            uploadMap.put(pathToHashTag, prismPost.getTimestamp());
        }
    }

    static void addPathOfFollowersFeedToUploadMap(Map<String, Object> uploadMap, PrismPost prismPost, String postId) {
        ArrayList<String> followersUid = CurrentUser.getFollowers();
        for (String followerUid : followersUid) {
            String pathToFollowerFeed = Key.DB_REF_USER_PROFILES + "/" + followerUid + "/" + Key.DB_REF_USER_NEWS_FEED + "/" + postId;
            uploadMap.put(pathToFollowerFeed, prismPost.getTimestamp());
        }
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

    /**
     *
     * @param prismPost
     */
    static void deletePostFromUserUploads(PrismPost prismPost) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.child(prismPost.getPrismUser().getUid())
                .child(Key.DB_REF_USER_UPLOADS)
                .child(prismPost.getPostId()).removeValue();
    }

    /**
     *
     * @param prismPost
     */
    static void deletePostUnderItsHashTags(PrismPost prismPost) {
        ArrayList<String> listOfHashTags = Helper.parseDescriptionForTags(prismPost.getCaption());
        DatabaseReference tagsReference = Default.TAGS_REFERENCE;

        for (String hashTag : listOfHashTags) {
            tagsReference.child(hashTag).removeValue();
        }
    }

    /**
     *
     * @param prismPost
     */
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

    /**
     *
     * @param prismPost
     */
    static void deletePostFromAllPosts(PrismPost prismPost) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        allPostsReference.child(prismPost.getPostId()).removeValue();
    }

}

