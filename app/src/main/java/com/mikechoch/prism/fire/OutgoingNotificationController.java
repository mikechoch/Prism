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

        // TODO put this inside Handler/Runnable
        pushLikeNotification(prismPost, actionTimestamp);

    }

    protected static void generateRepostNotification(PrismPost prismPost, long actionTimestamp) {

        // TODO put this inside Handler/Runnable
        pushRepostNotification(prismPost, actionTimestamp);
    }

    protected static void generateFollowNotification(PrismUser prismUser, long actionTimestamp) {

        // TODO put this inside Handler/Runnable
        pushFollowNotification(prismUser, actionTimestamp);
    }

    protected static void revokeLikeNotification(PrismPost prismPost) {

        // TODO cancel runnable if not invoked yet
        revertLikeNotification(prismPost);
    }

    protected static void revokeRepostNotification(PrismPost prismPost) {

        // TODO cancel runnable if not invoked yet
        revertRepostNotification(prismPost);
    }

    protected static void revokeFollowNotification(PrismUser prismUser) {

        // TODO cancel runnable if not invoked yet
        revertFollowNotification(prismUser);
    }

    private static void pushLikeNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.LIKE;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

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

                // TODO Invoke FCM Push notification here
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private static void pushRepostNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.REPOST;
        String notificationId = prismPost.getPostId() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

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

                // TODO Invoke FCM Push notification here
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });

    }

    private static void pushFollowNotification(PrismUser prismUser, long actionTimestamp) {
        NotificationType type = NotificationType.FOLLOW;
        String notificationId = prismUser.getUid() + type.getNotifIdSuffix();

        DatabaseReference notificationReference = usersReference.child(prismUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

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

                // TODO Invoke FCM Push notification here
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
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

}