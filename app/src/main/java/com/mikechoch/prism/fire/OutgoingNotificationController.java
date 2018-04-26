package com.mikechoch.prism.fire;

import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.notification.PushNotification;
import com.mikechoch.prism.type.NotificationType;

import java.util.HashMap;

public class OutgoingNotificationController {


    private static DatabaseReference usersReference = Default.USERS_REFERENCE;
    private static DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;

    private static Handler handler = new Handler();
    private static HashMap<String, Runnable> likeNotificationHandlers = new HashMap<>();
    private static HashMap<String, Runnable> repostNotificationHandlers = new HashMap<>();
    private static HashMap<String, Runnable> followNotificationHandlers = new HashMap<>();


    protected static void generateLikeNotification(PrismPost prismPost, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                pushLikeNotification(prismPost, actionTimestamp);
                likeNotificationHandlers.remove(prismPost.getPostId());
            }
        };

        handler.postDelayed(runnable, Default.PUSH_NOTIFICATION_HANDLER_WAIT);
        likeNotificationHandlers.put(prismPost.getPostId(), runnable);

    }

    protected static void generateRepostNotification(PrismPost prismPost, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                pushRepostNotification(prismPost, actionTimestamp);
                repostNotificationHandlers.remove(prismPost.getPostId());
            }
        };

        handler.postDelayed(runnable, Default.PUSH_NOTIFICATION_HANDLER_WAIT);
        repostNotificationHandlers.put(prismPost.getPostId(), runnable);
    }

    protected static void generateFollowNotification(PrismUser prismUser, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                pushFollowNotification(prismUser, actionTimestamp);
                followNotificationHandlers.remove(prismUser.getUid());
            }
        };

        handler.postDelayed(runnable, Default.PUSH_NOTIFICATION_HANDLER_WAIT);
        followNotificationHandlers.put(prismUser.getUid(), runnable);

    }

    protected static void revokeLikeNotification(PrismPost prismPost) {

        revertLikeNotification(prismPost);
        if (likeNotificationHandlers.containsKey(prismPost.getPostId())) {
            Runnable run = likeNotificationHandlers.remove(prismPost.getPostId());
            handler.removeCallbacks(run);
        }
    }

    protected static void revokeRepostNotification(PrismPost prismPost) {

        revertRepostNotification(prismPost);
        if (repostNotificationHandlers.containsKey(prismPost.getPostId())) {
            Runnable run = repostNotificationHandlers.remove(prismPost.getPostId());
            handler.removeCallbacks(run);
        }
    }

    protected static void revokeFollowNotification(PrismUser prismUser) {

        revertFollowNotification(prismUser);
        if (followNotificationHandlers.containsKey(prismUser.getUid())) {
            Runnable run = repostNotificationHandlers.remove(prismUser.getUid());
            handler.removeCallbacks(run);
        }
    }

    private static void pushLikeNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.LIKE;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        getMostRecentUserAndInsertNotification(notificationReference, actionTimestamp);

        getTokenAndPushNotification(prismPost.getUid(), type);
    }

    private static void pushRepostNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.REPOST;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        getMostRecentUserAndInsertNotification(notificationReference, actionTimestamp);

        getTokenAndPushNotification(prismPost.getUid(), type);

    }

    private static void pushFollowNotification(PrismUser prismUser, long actionTimestamp) {
        NotificationType type = NotificationType.FOLLOW;
        String notificationId = prismUser.getUid() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        getMostRecentUserAndInsertNotification(notificationReference, actionTimestamp);

        getTokenAndPushNotification(prismUser.getUid(), type);
    }

    private static void revertLikeNotification(PrismPost prismPost) {
        NotificationType type = NotificationType.UNLIKE;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();
        String DB_REF = type.getDatabaseRefKey();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        allPostsReference.child(prismPost.getPostId()).child(DB_REF).orderByValue().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String previousRecentUid = dataSnapshot
                                    .getChildren().iterator().next().getKey();
                            long previousActionTimestamp = (long) dataSnapshot
                                    .getChildren().iterator().next().getValue();
                            HashMap<String, Object> updatedNotification = new HashMap<>();
                            updatedNotification.put(Key.NOTIFICATION_MOST_RECENT_USER, previousRecentUid);
                            updatedNotification.put(Key.NOTIFICATION_ACTION_TIMESTAMP, previousActionTimestamp);
                            notificationReference.updateChildren(updatedNotification);
                        } else {
                            notificationReference.removeValue();
                        }
                    }

                    @Override public void onCancelled(DatabaseError databaseError) { }
                });
        // TODO Revoke FCM Push notification here
    }

    private static void revertRepostNotification(PrismPost prismPost) {
        NotificationType type = NotificationType.UNREPOST;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();
        String DB_REF = type.getDatabaseRefKey();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        allPostsReference.child(prismPost.getPostId()).child(DB_REF).orderByValue().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String previousRecentUid = dataSnapshot
                                    .getChildren().iterator().next().getKey();
                            long previousActionTimestamp = (long) dataSnapshot
                                    .getChildren().iterator().next().getValue();
                            HashMap<String, Object> updatedNotification = new HashMap<>();
                            updatedNotification.put(Key.NOTIFICATION_MOST_RECENT_USER, previousRecentUid);
                            updatedNotification.put(Key.NOTIFICATION_ACTION_TIMESTAMP, previousActionTimestamp);
                            notificationReference.updateChildren(updatedNotification);
                        } else {
                            notificationReference.removeValue();
                        }
                    }

                    @Override public void onCancelled(DatabaseError databaseError) { }
                });
        // TODO Revoke FCM Push notification here
    }

    private static void revertFollowNotification(PrismUser prismUser) {
        NotificationType type = NotificationType.UNFOLLOW;
        String notificationId = prismUser.getUid() + type.getNotifIdSuffix();
        String DB_REF = type.getDatabaseRefKey();

        DatabaseReference notificationReference = usersReference.child(prismUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        usersReference.child(prismUser.getUid()).child(DB_REF).orderByValue().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String previousRecentUid = dataSnapshot
                                    .getChildren().iterator().next().getKey();
                            long previousActionTimestamp = (long) dataSnapshot
                                    .getChildren().iterator().next().getValue();
                            HashMap<String, Object> updatedNotification = new HashMap<>();
                            updatedNotification.put(Key.NOTIFICATION_MOST_RECENT_USER, previousRecentUid);
                            updatedNotification.put(Key.NOTIFICATION_ACTION_TIMESTAMP, previousActionTimestamp);
                            notificationReference.updateChildren(updatedNotification);
                        } else {
                            notificationReference.removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
        // TODO Revoke FCM Push notification here
    }

    private static void getTokenAndPushNotification(String userId, NotificationType type) {
        usersReference.child(userId).child(Key.USER_TOKEN)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot tokenSnapshot) {
                        if (tokenSnapshot.exists()) {
                            String token = (String) tokenSnapshot.getValue();
                            // TODO Generate Notification Object here
                            // TODO Then invoke FCM push Notification
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    private static void getMostRecentUserAndInsertNotification(DatabaseReference notificationReference, long actionTimestamp) {
        notificationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long viewedTimestamp = 0;
                if (dataSnapshot.hasChild(Key.NOTIFICATION_VIEWED_TIMESTAMP)) {
                    viewedTimestamp = (long) dataSnapshot.child(Key.NOTIFICATION_VIEWED_TIMESTAMP).getValue();
                }
                HashMap<String, Object> notification = new HashMap<>();
                notification.put(Key.NOTIFICATION_MOST_RECENT_USER, CurrentUser.prismUser.getUid());
                notification.put(Key.NOTIFICATION_ACTION_TIMESTAMP, actionTimestamp);
                notification.put(Key.NOTIFICATION_VIEWED_TIMESTAMP, viewedTimestamp);
                notificationReference.setValue(notification);

            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });
    }

}
