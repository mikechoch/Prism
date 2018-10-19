package com.mikechoch.prism.fire;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
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


    /**
     *
     * @param context
     */
    static void initializeNotifications(Context context) {
        fetchUserNotifications(context);
        initializeNotificationEventHandler(context);
    }

    /**
     *
     * @param context
     */
    private static void fetchUserNotifications(Context context) {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot notificationSnap : dataSnapshot.getChildren()) {
                            generateNotification(context, notificationSnap, true, true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    /**
     *
     * @param context
     */
    private static void initializeNotificationEventHandler(Context context) {
        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
        CurrentUser.notificationsListener = new ChildEventListener() {
            /* Invoked when app loads and when a new notification is created */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                generateNotification(context, dataSnapshot, true, false);
            }

            /* Invoked when an old notification gets updated */
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                generateNotification(context, dataSnapshot, false, false);
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

        refreshNotificationRecyclerViewAdapter(context);
    
        CurrentUser.notificationsHandler = new Handler();
        CurrentUser.notificationsRunnable = new Runnable() {
            public void run() {
                Log.i(Default.TAG_DEBUG, "Fetching notifications");
                refreshNotificationRecyclerViewAdapter(context);
                CurrentUser.notificationsHandler.postDelayed(this, Default.NOTIFICATION_UPDATE_INTERVAL);
            }
        };

        CurrentUser.notificationsHandler.postDelayed(CurrentUser.notificationsRunnable, Default.NOTIFICATION_UPDATE_INTERVAL);

    }

    /**
     *
     * @param context
     */
    private static void refreshNotificationRecyclerViewAdapter(Context context) {
        RecyclerView notificationRecyclerView = ((Activity) context).findViewById(R.id.notification_recycler_view);
        if (notificationRecyclerView != null) {
            Collections.sort(CurrentUser.getNotifications());
            notificationRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     *
     * @param context
     * @param dataSnapshot
     * @param isNewNotification
     * @param refreshAdapter
     */
    private static void generateNotification(Context context, DataSnapshot dataSnapshot, boolean isNewNotification, boolean refreshAdapter) {
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
                            fetchMostRecentUser(context, notification, notificationId, mostRecentUid,
                                    isNewNotification, refreshAdapter);
                        }
                    }

                    @Override public void onCancelled(DatabaseError databaseError) { }
                });
                break;
            case FOLLOW:
                fetchMostRecentUser(context, notification, notificationId, mostRecentUid, isNewNotification, refreshAdapter);
                break;
        }

    }

    /**
     *
     * @param context
     * @param notification
     * @param notificationId
     * @param mostRecentUid
     * @param isNewNotification
     * @param refreshAdapter
     */
    private static void fetchMostRecentUser(Context context, com.mikechoch.prism.attribute.Notification notification, String notificationId, String mostRecentUid,
                                            boolean isNewNotification, boolean refreshAdapter) {
        DatabaseReference mostRecentUserRef = Default.USERS_REFERENCE.child(mostRecentUid);
        mostRecentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                PrismUser mostRecentUser = Helper.constructPrismUserObject(userSnapshot);
                notification.setMostRecentUser(mostRecentUser);
                updateNotificationsList(context, isNewNotification, notificationId, notification, refreshAdapter);
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     *
     * @param context
     * @param isNewNotification
     * @param notificationId
     * @param notification
     * @param refreshAdapter
     */
    private static void updateNotificationsList(Context context, boolean isNewNotification, String notificationId, com.mikechoch.prism.attribute.Notification notification, boolean refreshAdapter) {
        if (!isNewNotification) {
            CurrentUser.removeNotification(notificationId);
        }

        CurrentUser.addNotification(notification, notificationId);

        if (refreshAdapter) {
            refreshNotificationRecyclerViewAdapter(context);
        }
    }
    
}
