package com.mikechoch.prism.fire;

import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.PushNotification;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.type.NotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class OutgoingNotificationController {


    private static Handler handler = new Handler();
    private static HashMap<String, Runnable> likeNotificationHandlers = new HashMap<>();
    private static HashMap<String, Runnable> repostNotificationHandlers = new HashMap<>();
    private static HashMap<String, Runnable> followNotificationHandlers = new HashMap<>();


    protected static void prepareLikeNotification(PrismPost prismPost, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                createLikeNotification(prismPost, actionTimestamp);
                likeNotificationHandlers.remove(prismPost.getPostId());
            }
        };

        handler.postDelayed(runnable, Default.PUSH_NOTIFICATION_HANDLER_WAIT);
        likeNotificationHandlers.put(prismPost.getPostId(), runnable);

    }

    protected static void prepareRepostNotification(PrismPost prismPost, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                createRepostNotification(prismPost, actionTimestamp);
                repostNotificationHandlers.remove(prismPost.getPostId());
            }
        };

        handler.postDelayed(runnable, Default.PUSH_NOTIFICATION_HANDLER_WAIT);
        repostNotificationHandlers.put(prismPost.getPostId(), runnable);
    }

    protected static void prepareFollowNotification(PrismUser prismUser, long actionTimestamp) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                createFollowNotification(prismUser, actionTimestamp);
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

    private static void createLikeNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.LIKE;
        String notificationId = NotificationType.createLikeRepostNotificationId(prismPost, type);

        DatabaseReference usersReference = Default.USERS_REFERENCE;
        DatabaseReference notificationReference = usersReference.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        insertNotificationDataToCloud(notificationReference, actionTimestamp);

        generatePushNotification(prismPost.getUid(), type, prismPost, actionTimestamp);
    }

    private static void createRepostNotification(PrismPost prismPost, long actionTimestamp) {
        NotificationType type = NotificationType.REPOST;
        String notificationId = NotificationType.createLikeRepostNotificationId(prismPost, type);

        DatabaseReference notificationReference = Default.USERS_REFERENCE.child(prismPost.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        insertNotificationDataToCloud(notificationReference, actionTimestamp);

        generatePushNotification(prismPost.getUid(), type, prismPost, actionTimestamp);
    }

    private static void createFollowNotification(PrismUser prismUser, long actionTimestamp) {
        NotificationType type = NotificationType.FOLLOW;
        String notificationId = NotificationType.createFollowNotificationId();

        DatabaseReference notificationReference = Default.USERS_REFERENCE.child(prismUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        insertNotificationDataToCloud(notificationReference, actionTimestamp);

        generatePushNotification(prismUser.getUid(), type, prismUser, actionTimestamp);
    }

    private static void revertLikeNotification(PrismPost prismPost) {
        NotificationType type = NotificationType.UNLIKE;
        String notificationId = NotificationType.createLikeRepostNotificationId(prismPost, type);
        String DB_REF = type.getDatabaseRefKey();

        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        DatabaseReference notificationReference = Default.USERS_REFERENCE.child(prismPost.getUid())
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static void revertRepostNotification(PrismPost prismPost) {
        NotificationType type = NotificationType.UNREPOST;
        String notificationId = NotificationType.createLikeRepostNotificationId(prismPost, type);
        String DB_REF = type.getDatabaseRefKey();

        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
        DatabaseReference notificationReference = Default.USERS_REFERENCE.child(prismPost.getUid())
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static void revertFollowNotification(PrismUser prismUser) {
        String notificationId = NotificationType.createFollowNotificationId();

        DatabaseReference notificationReference = Default.USERS_REFERENCE.child(prismUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS).child(notificationId);

        notificationReference.removeValue();

    }

    private static void insertNotificationDataToCloud(DatabaseReference notificationReference, long actionTimestamp) {
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

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private static void generatePushNotification(String notificationReceiverPrismUserId, NotificationType type, Object object, long actionTimestamp) {
        DatabaseReference usersReference = Default.USERS_REFERENCE;
        usersReference.child(notificationReceiverPrismUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean allowPushNotification = (boolean) dataSnapshot
                                    .child(Key.DB_REF_USER_PREFERENCES)
                                    .child(type.getdbUserNotifPrefKey()).getValue();

                            if (dataSnapshot.hasChild(Key.USER_TOKEN) && allowPushNotification) {
                                String userToken = (String) dataSnapshot.child(Key.USER_TOKEN).getValue();
                                PushNotification pushNotification = new PushNotification();
                                if (object instanceof PrismPost) {
                                    pushNotification = constructPushNotification(type, (PrismPost) object, actionTimestamp);
                                } else if (object instanceof PrismUser) {
                                    pushNotification = constructPushNotification(type, (PrismUser) object, actionTimestamp);
                                }

                                triggerPushNotification(userToken, pushNotification);
                            } else {
                                Log.d("NotificationType", "User does not allow push notification");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

    }

    private static PushNotification constructPushNotification(NotificationType type, PrismPost prismPost, long actionTimestamp) {
        int notificationId = NotificationType.generateLikeRepostPushNotificationId(prismPost, type);
        PushNotification pushNotification = new PushNotification();
        pushNotification.setMostRecentUsername(CurrentUser.prismUser.getUsername());
        pushNotification.setMostRecentUserProfilePicUri(CurrentUser.prismUser.getProfilePicture().profilePicUri);
        pushNotification.setType(type);
        pushNotification.setActionTimestamp(actionTimestamp);
        pushNotification.setNotificationHashId(notificationId);
        pushNotification.setPrismPostId(prismPost.getPostId());
        return pushNotification;
    }

    private static PushNotification constructPushNotification(NotificationType type, PrismUser prismUser, long actionTimestamp) {
        int notificationId = NotificationType.generateFollowPushNotificationId();
        PushNotification pushNotification = new PushNotification();
        pushNotification.setMostRecentUsername(CurrentUser.prismUser.getUsername());
        pushNotification.setMostRecentUserProfilePicUri(CurrentUser.prismUser.getProfilePicture().profilePicUri);
        pushNotification.setType(type);
        pushNotification.setActionTimestamp(actionTimestamp);
        pushNotification.setNotificationHashId(notificationId);
        pushNotification.setPrismUserId(prismUser.getUid());
        return pushNotification;
    }

    private static void triggerPushNotification(String userToken, PushNotification pushNotification) {
        JSONObject dataJson = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        HashMap<String, String> params = new HashMap<>();
        try {
            dataJson.put(NotificationKey.MOST_RECENT_USER, pushNotification.getMostRecentUsername());
            dataJson.put(NotificationKey.MESSAGE, pushNotification.getType().getPushNotificationMessage());
            dataJson.put(NotificationKey.USER_PROFILE_PIC, pushNotification.getMostRecentUserProfilePicUri());
            dataJson.put(NotificationKey.TIMESTAMP, pushNotification.getActionTimestamp());
            dataJson.put(NotificationKey.NOTIFICATION_ID, pushNotification.getNotificationHashId());
            dataJson.put(NotificationKey.PRISM_POST_ID, pushNotification.getPrismPostId());
            dataJson.put(NotificationKey.PRISM_USER_ID, pushNotification.getPrismUserId());

            jsonBody.put("to", userToken);
            jsonBody.put("data", dataJson);

            params.put("Content-Type", "application/json charset=utf-8");
            params.put("Authorization", "key=" + MainActivity.FCM_API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                .addJSONObjectBody(jsonBody)
                .addHeaders(params)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OKHTTPCLIENT", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("OKHTTPCLIENT", error.getErrorDetail());
                    }
                });
    }

}
