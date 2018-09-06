package com.mikechoch.prism.fire;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.fragment.NotificationFragment;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.NotificationType;

import java.util.Collections;

public class IncomingNotificationController {


    static void initializeNotifications() {
        fetchUserNotifications();
        initializeNotificationEventHandler();
    }
    
    private static void fetchUserNotifications() {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot notificationSnap : dataSnapshot.getChildren()) {
                            generateNotification(notificationSnap, true, true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }
    
    private static void initializeNotificationEventHandler() {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        CurrentUser.notificationsListener = new ChildEventListener() {
            /* Invoked when app loads and when a new notification is created */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                generateNotification(dataSnapshot, true, false);
            }

            /* Invoked when an old notification gets updated */
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                generateNotification(dataSnapshot, false, false);
            }

            /* Invoked when a notification is deleted */
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String notificationId = dataSnapshot.getKey();
                CurrentUser.removeNotification(notificationId);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Not sure what to do here
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
            }
        };

        CurrentUser.notificationsReference = currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS);
        CurrentUser.notificationsReference.addChildEventListener(CurrentUser.notificationsListener);

        refreshNotificationRecyclerViewAdapter();
    
        CurrentUser.notificationsHandler = new Handler();
        CurrentUser.notificationsRunnable = new Runnable() {
            public void run() {
                Log.i(Default.TAG_DEBUG, "Fetching notifications");
                refreshNotificationRecyclerViewAdapter();
                CurrentUser.notificationsHandler.postDelayed(this, Default.NOTIFICATION_UPDATE_INTERVAL);
            }
        };

        CurrentUser.notificationsHandler.postDelayed(CurrentUser.notificationsRunnable, Default.NOTIFICATION_UPDATE_INTERVAL);

    }

    private static void refreshNotificationRecyclerViewAdapter() {
        if (NotificationFragment.notificationRecyclerViewAdapter != null) {
            Collections.sort(CurrentUser.getNotifications());
            NotificationFragment.notificationRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     *
     * @param dataSnapshot
     * @param isNewNotification
     */
    private static void generateNotification(DataSnapshot dataSnapshot, boolean isNewNotification, boolean refreshAdapter) {
        String notificationId = dataSnapshot.getKey();
        NotificationType type = NotificationType.getType(notificationId);
        String mostRecentUid = (String) dataSnapshot.child(Key.NOTIFICATION_MOST_RECENT_USER).getValue();
        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;

        long actionTimestamp = (long) dataSnapshot.child(Key.NOTIFICATION_ACTION_TIMESTAMP).getValue();
        long viewedTimestamp = (long) dataSnapshot.child(Key.NOTIFICATION_VIEWED_TIMESTAMP).getValue();
        boolean viewed = viewedTimestamp > actionTimestamp;

        Notification notification = new Notification();
        notification.setType(type);
        notification.setActionTimestamp(actionTimestamp);
        notification.setViewed(viewed);

        switch (type) {
            case LIKE:
            case REPOST:
                String postId = type.decodeNotificationPostId(notificationId);
                allPostsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot postSnapshot) {
                        if (postSnapshot.exists()) {
                            PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                            prismPost.setPrismUser(CurrentUser.prismUser);
                            notification.setPrismPost(prismPost);
                            fetchMostRecentUser(notification, notificationId, mostRecentUid,
                                    isNewNotification, refreshAdapter);
                        }
                    }

                    @Override public void onCancelled(DatabaseError databaseError) { }
                });
                break;
            case FOLLOW:
                fetchMostRecentUser(notification, notificationId, mostRecentUid, isNewNotification, refreshAdapter);
                break;
        }

    }

    /**
     *
     */
    private static void fetchMostRecentUser(com.mikechoch.prism.attribute.Notification notification, String notificationId, String mostRecentUid,
                                            boolean isNewNotification, boolean refreshAdapter) {
        DatabaseReference mostRecentUserRef = Default.USERS_REFERENCE.child(mostRecentUid);
        mostRecentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                PrismUser mostRecentUser = Helper.constructPrismUserObject(userSnapshot);
                notification.setMostRecentUser(mostRecentUser);
                updateNotificationsList(isNewNotification, notificationId, notification, refreshAdapter);
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     *
     * @param isNewNotification
     * @param notificationId
     * @param notification
     */
    private static void updateNotificationsList(boolean isNewNotification, String notificationId, com.mikechoch.prism.attribute.Notification notification, boolean refreshAdapter) {
        if (!isNewNotification) {
            CurrentUser.removeNotification(notificationId);
        }

        CurrentUser.addNotification(notification, notificationId);

        if (refreshAdapter) {
            refreshNotificationRecyclerViewAdapter();
        }
    }
    
}
