package com.mikechoch.prism.fire;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.attribute.LinkedPrismPosts;
import com.mikechoch.prism.attribute.LinkedPrismUsers;
import com.mikechoch.prism.attribute.OldNotification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.UserPreference;
import com.mikechoch.prism.callback.fetch.OnFetchNotificationsCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostsCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismUserCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismUsersCallback;
import com.mikechoch.prism.callback.fetch.OnFetchUserProfileCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseRead {


    /**
     * Takes a string hashTag and pulls list of PrismPost ids for posts
     * that are associated with the given hashTag. Then it calls
     * fetchPrismPosts which parses prismPost details for those posts
     * and returns them using the callback
     * @param tag - string HashTag for which posts need to be pulled
     * @param callback - used to return list of PrismPosts for the hashtag
     */
    public static void fetchPrismPostsForTag(String tag, OnFetchPrismPostsCallback callback) {
        DatabaseReference tagReference = Default.TAGS_REFERENCE.child(tag);
        tagReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot postsSnapshot) {
                if (postsSnapshot.exists()) {
                    Map<String, Long> taggedPosts =  (HashMap<String, Long>) postsSnapshot.getValue();
                    fetchPrismPosts(new ArrayList<>(taggedPosts.keySet()), callback);
                } else {
                    callback.onPrismPostsNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    /**
     * 
     * @param postId
     * @param callback
     */
    public static void fetchLikedUsers(String postId, OnFetchPrismUsersCallback callback) {
        DatabaseReference likedUsersReference = Default.ALL_POSTS_REFERENCE
                .child(postId)
                .child(Key.DB_REF_POST_LIKED_USERS);

        likedUsersReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                if (usersSnapshot.exists()) {
                    Map<String, Long> likedUsers =  (HashMap<String, Long>) usersSnapshot.getValue();
                    fetchPrismUsers(new ArrayList<>(likedUsers.keySet()), callback);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });

    }

    public static void fetchRepostedUsers(String postId, OnFetchPrismUsersCallback callback) {
        DatabaseReference repostedUsersReference = Default.ALL_POSTS_REFERENCE
                .child(postId)
                .child(Key.DB_REF_POST_REPOSTED_USERS);

        repostedUsersReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot repostedUsersSnapshot) {
                if (repostedUsersSnapshot.exists()) {
                    Map<String, Long> repostedUsers =  (HashMap<String, Long>) repostedUsersSnapshot.getValue();
                    fetchPrismUsers(new ArrayList<>(repostedUsers.keySet()), callback);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismUserFollowings(String userId, OnFetchPrismUsersCallback callback) {
        DatabaseReference userFollowingsReference = Default.USERS_REFERENCE
                .child(userId)
                .child(Key.DB_REF_USER_FOLLOWINGS);

        userFollowingsReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userFollowingsSnapshot) {
                if (userFollowingsSnapshot.exists()) {
                    Map<String, Long> userFollowings = (HashMap<String, Long>) userFollowingsSnapshot.getValue();
                    fetchPrismUsers(new ArrayList<>(userFollowings.keySet()), callback);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismUserFollowers(String userId, OnFetchPrismUsersCallback callback) {
        DatabaseReference userFollowersReference = Default.USERS_REFERENCE
                .child(userId)
                .child(Key.DB_REF_USER_FOLLOWERS);

        userFollowersReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userFollowersSnapshot) {
                if (userFollowersSnapshot.exists()) {
                    Map<String, Long> userFollowers = (HashMap<String, Long>) userFollowersSnapshot.getValue();
                    fetchPrismUsers(new ArrayList<>(userFollowers.keySet()), callback);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismUserUploadedPosts(PrismUser prismUser, OnFetchPrismPostsCallback callback) {
        DatabaseReference userUploadedPostsReference = Default.USERS_REFERENCE
                .child(prismUser.getUid())
                .child(Key.DB_REF_USER_UPLOADS);

        userUploadedPostsReference.orderByChild(Key.POST_TIMESTAMP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot uploadedPostsSnapshot) {
                        if (uploadedPostsSnapshot.exists()) {
                            Map<String, Long> userUploads = (HashMap<String, Long>) uploadedPostsSnapshot.getValue();
                            fetchPrismPosts(new ArrayList<>(userUploads.keySet()), callback);
                        } else {
                            callback.onPrismPostsNotFound();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.toException());
                    }
                });
    }

    public static void fetchPrismUserRepostedPosts(PrismUser prismUser, OnFetchPrismPostsCallback callback) {
        DatabaseReference userRepostedPostsReference = Default.USERS_REFERENCE
                .child(prismUser.getUid())
                .child(Key.DB_REF_USER_REPOSTS);

        userRepostedPostsReference.orderByChild(Key.POST_TIMESTAMP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot repostedPostsSnapshot) {
                        if (repostedPostsSnapshot.exists()) {
                            Map<String, Long> userReposts = (HashMap<String, Long>) repostedPostsSnapshot.getValue();
                            fetchPrismPosts(new ArrayList<>(userReposts.keySet()), callback);
                        } else {
                            callback.onPrismPostsNotFound();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure(databaseError.toException());
                    }
                });
    }

    public static void fetchPrismUsers(ArrayList<String> prismUserIds, OnFetchPrismUsersCallback callback) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                if (usersSnapshot.exists()) {
                    //LinkedHashMap<String, PrismUser> prismUsersHashMap = new LinkedHashMap<>();
                    LinkedPrismUsers linkedPrismUsers = new LinkedPrismUsers();
                    for (String prismUserId : prismUserIds) {
                        DataSnapshot userSnapshot = usersSnapshot.child(prismUserId);
                        if (userSnapshot.exists()) {
                            PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                            linkedPrismUsers.addPrismUser(prismUser);
                        } else {
                            prismUserIds.remove(prismUserId); // TODO Log this - this shouldn't happen
                        }
                    }
                    callback.onSuccess(linkedPrismUsers);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchLatestPrismPosts(OnFetchPrismPostsCallback callback) {
        Query allPostsReference = Default.ALL_POSTS_REFERENCE.orderByChild(Key.POST_TIMESTAMP).limitToFirst(Default.IMAGE_LOAD_COUNT);
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allPostsSnapshot) {
                if (allPostsSnapshot.exists()) {
                    LinkedPrismPosts linkedPrismPosts = new LinkedPrismPosts();
                    for (DataSnapshot postSnapshot : allPostsSnapshot.getChildren()) {
                        PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                        linkedPrismPosts.addPrismPost(prismPost);
                    }

                    fetchPrismUsers(linkedPrismPosts.getPrismUserIds(), new OnFetchPrismUsersCallback() {
                        @Override
                        public void onSuccess(LinkedPrismUsers linkedPrismUsers) {
                            linkedPrismPosts.updatePrismUsersForPost(linkedPrismUsers);
                            callback.onSuccess(linkedPrismPosts);
                        }

                        @Override
                        public void onPrismUsersNotFound() { }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                } else {
                    callback.onPrismPostsNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchMorePrismPosts(long lastPostTimestamp, OnFetchPrismPostsCallback callback) {
        Query morePostsReference = Default.ALL_POSTS_REFERENCE.orderByChild(Key.POST_TIMESTAMP).startAt(lastPostTimestamp + 1).limitToFirst(Default.IMAGE_LOAD_COUNT);
        morePostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allPostsSnapshot) {
                if (allPostsSnapshot.exists()) {
                    LinkedPrismPosts linkedPrismPosts = new LinkedPrismPosts();
                    for (DataSnapshot postSnapshot : allPostsSnapshot.getChildren()) {
                        PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                        linkedPrismPosts.addPrismPost(prismPost);
                    }
                    fetchPrismUsers(linkedPrismPosts.getPrismUserIds(), new OnFetchPrismUsersCallback() {
                        @Override
                        public void onSuccess(LinkedPrismUsers linkedPrismUsers) {
                            linkedPrismPosts.updatePrismUsersForPost(linkedPrismUsers);
                            callback.onSuccess(linkedPrismPosts);
                        }

                        @Override
                        public void onPrismUsersNotFound() { }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                } else {
                    callback.onPrismPostsNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismPosts(ArrayList<String> prismPostIds, OnFetchPrismPostsCallback callback) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        allPostsReference.orderByChild(Key.POST_TIMESTAMP).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allPostsSnapshot) {
                if (allPostsSnapshot.exists()) {
                    LinkedPrismPosts linkedPrismPosts = new LinkedPrismPosts();
                    for (String postId : prismPostIds) {
                        DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
                        if (postSnapshot.exists()) {
                            PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                            linkedPrismPosts.addPrismPost(prismPost);
                        }
                    }

                    fetchPrismUsers(linkedPrismPosts.getPrismUserIds(), new OnFetchPrismUsersCallback() {
                        @Override
                        public void onSuccess(LinkedPrismUsers linkedPrismUsers) {
                            linkedPrismPosts.updatePrismUsersForPost(linkedPrismUsers);
                            callback.onSuccess(linkedPrismPosts);
                        }

                        @Override
                        public void onPrismUsersNotFound() { }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                } else {
                    callback.onPrismPostsNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismPost(String prismPostId, OnFetchPrismPostCallback callback) {
        DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.child(prismPostId);
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot postSnapshot) {
                if (postSnapshot.exists()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    fetchPrismUser(prismPost.getUid(), new OnFetchPrismUserCallback() {
                        @Override
                        public void onSuccess(PrismUser prismUser) {
                            prismPost.setPrismUser(prismUser);
                            callback.onSuccess(prismPost);
                        }

                        @Override
                        public void onUserNotFound() {
                            callback.onPostAuthorNotFound();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                } else {
                    callback.onPostNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchPrismUser(String prismUserId, OnFetchPrismUserCallback callback) {
        DatabaseReference userReference = Default.USERS_REFERENCE.child(prismUserId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                    callback.onSuccess(prismUser);
                } else {
                    callback.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
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
                    CurrentUser.setPrismUser(Helper.constructPrismUserObject(currentUserSnapshot));
                    CurrentUser.preference = ProfileBuilder.fetchUserPreferences(currentUserSnapshot);

                    CurrentUser.liked_posts_map = ProfileBuilder.fetchLikedPosts(currentUserSnapshot);
                    CurrentUser.reposted_posts_map = ProfileBuilder.fetchRepostedPosts(currentUserSnapshot);
                    CurrentUser.uploaded_posts_map = ProfileBuilder.fetchUploadedPosts(currentUserSnapshot);

                    CurrentUser.followers = ProfileBuilder.fetchUserFollowers(currentUserSnapshot);
                    CurrentUser.followings = ProfileBuilder.fetchUserFollowings(currentUserSnapshot);

                    populateCurrentUserProfilePosts(usersSnapshot, callback);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
                callback.onFailure(databaseError.toException());
            }
        });
    }

    /* THIS IS VERY INEFFICIENT AND WILL NEED
     * TO BE REDONE AFTER 1000 USERS  */
    public static void fetchAllUsers(OnFetchPrismUsersCallback callback) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                if (usersSnapshot.exists()) {
                    LinkedPrismUsers linkedPrismUsers = new LinkedPrismUsers();
                    for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                        PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                        linkedPrismUsers.addPrismUser(prismUser);
                    }
                    callback.onSuccess(linkedPrismUsers);
                } else {
                    callback.onPrismUsersNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    private static void populateCurrentUserProfilePosts(DataSnapshot usersSnapshot, OnFetchUserProfileCallback callback) {
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allPostsSnapshot) {
                if (allPostsSnapshot.exists()) {
                    ArrayList<PrismPost> userLikes = ProfileBuilder.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.liked_posts_map);
                    ArrayList<PrismPost> userReposts = ProfileBuilder.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.reposted_posts_map);
                    ArrayList<PrismPost> userUploads = ProfileBuilder.getListOfPrismPosts(allPostsSnapshot,
                            usersSnapshot, CurrentUser.uploaded_posts_map);

                    CurrentUser.addLikedPosts(userLikes);
                    CurrentUser.addRepostedPosts(userReposts);
                    CurrentUser.addUploadPosts(userUploads);
                    CurrentUser.combineUploadsAndReposts();
                    // CurrentUser.constructNewsFeed();
                }
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    public static void fetchCurrentNotifications(OnFetchNotificationsCallback callback) {
        Query notificationsReference = Default.USERS_REFERENCE
                .child(CurrentUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS)
                .orderByChild(Key.NOTIFICATION_ACTION_TIMESTAMP);

        notificationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot notificationsSnapshot) {
                if (notificationsSnapshot.exists()) {
                    ArrayList<OldNotification> oldNotifications = new ArrayList<>();

                    for (DataSnapshot notificationSnapshot : notificationsSnapshot.getChildren()) {
                        OldNotification oldNotification = Helper.constructNotification(notificationSnapshot);
                        oldNotifications.add(oldNotification);
                    }

                    for (OldNotification oldNotification : oldNotifications) {
                        fetchNotificationDetails(oldNotification);
                    }
                    callback.onSuccess(oldNotifications);


                } else {
                    callback.onNotificationsNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });

    }

    static void fetchNotificationDetails(OldNotification oldNotification) {
        switch (oldNotification.getType()) {
            case LIKE:
            case REPOST:
                String postId = oldNotification.getNotificationPostId();
                fetchPrismPost(postId, new OnFetchPrismPostCallback() {
                    @Override
                    public void onSuccess(PrismPost prismPost) {
                        oldNotification.setPrismPost(prismPost);
                        String mostRecentUserId = oldNotification.getMostRecentUser().getUid();
                        fetchPrismUser(mostRecentUserId, new OnFetchPrismUserCallback() {
                            @Override
                            public void onSuccess(PrismUser prismUser) {
                                oldNotification.setMostRecentUser(prismUser);
                            }

                            @Override
                            public void onUserNotFound() { }

                            @Override
                            public void onFailure(Exception e) { }
                        });

                    }

                    @Override
                    public void onPostNotFound() {
                        oldNotification.setPrismPost(null);
                    }

                    @Override
                    public void onPostAuthorNotFound() { /* Wouldn't happen */}

                    @Override
                    public void onFailure(Exception e) {
                        oldNotification.setPrismPost(null);
                    }
                });

                break;

            case FOLLOW:
                String userId = oldNotification.getNotificationUserId();
                fetchPrismUser(userId, new OnFetchPrismUserCallback() {
                    @Override
                    public void onSuccess(PrismUser prismUser) {
                        oldNotification.setMostRecentUser(prismUser);
                    }

                    @Override
                    public void onUserNotFound() {
                        oldNotification.setMostRecentUser(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        oldNotification.setMostRecentUser(null);
                    }
                });

        }
    }


}

class ProfileBuilder {

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
