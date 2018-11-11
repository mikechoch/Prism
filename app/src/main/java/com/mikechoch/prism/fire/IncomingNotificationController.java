package com.mikechoch.prism.fire;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.OldNotification;
import com.mikechoch.prism.callback.fetch.OnFetchNotificationsCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.HashMap;


public class IncomingNotificationController {

    private static DatabaseReference notificationsReference;
    private static ChildEventListener notificationsListener;
    private static Handler notificationsHandler;
    private static Runnable notificationsRunnable;

    private static HashMap<String, OldNotification> notificationsMap;  // Key: notificationId, Value: OldNotification Object


    static void initializeNotifications() {



    }



//    /**
//     *
//     * @param context
//     */
//    static void initializeNotifications(Context context) {
//        fetchUserNotifications(context);
//        initializeNotificationEventHandler(context);
//    }

    static void fetchCurrentNotifications(Context context) {
        CurrentUser.oldNotifications = new ArrayList<>();
        notificationsMap = new HashMap<>();
        DatabaseRead.fetchCurrentNotifications(new OnFetchNotificationsCallback() {
            @Override
            public void onSuccess(ArrayList<OldNotification> oldNotifications) {
                for (OldNotification oldNotification : oldNotifications) {
                    if (!notificationsMap.containsKey(oldNotification.getNotificationId())) {
                        CurrentUser.addNotification(oldNotification);
                    }
                }
                refreshNotificationRecyclerViewAdapter(context);

                initializeNotificationsListener(context);
            }

            @Override
            public void onNotificationsNotFound() { }

            @Override
            public void onFailure(Exception e) { }
        });
    }

    private static void initializeNotificationsListener(Context context) {
        notificationsReference = Default.USERS_REFERENCE
                .child(CurrentUser.getUid())
                .child(Key.DB_REF_USER_NOTIFICATIONS);

        notificationsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot notificationSnapshot, @Nullable String s) {
                OldNotification oldNotification = Helper.constructNotification(notificationSnapshot);
                if (!notificationsMap.containsKey(oldNotification.getNotificationId())) {
                    DatabaseRead.fetchNotificationDetails(oldNotification);
                    CurrentUser.addNotification(oldNotification);
                    refreshNotificationRecyclerViewAdapter(context);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot notificationSnapshot, @Nullable String s) {
                OldNotification oldNotification = Helper.constructNotification(notificationSnapshot);
                DatabaseRead.fetchNotificationDetails(oldNotification);
                CurrentUser.removeNotification(oldNotification);
                CurrentUser.addNotification(oldNotification);
                refreshNotificationRecyclerViewAdapter(context);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot notificationSnapshot) {
                OldNotification oldNotification = Helper.constructNotification(notificationSnapshot);
                CurrentUser.removeNotification(oldNotification);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot notificationSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        notificationsReference.addChildEventListener(notificationsListener);

    }

    static void clearNotifications() {
        if (notificationsListener != null) {
            notificationsReference.removeEventListener(notificationsListener);
        }

        if (notificationsRunnable != null) {
            notificationsHandler.removeCallbacks(notificationsRunnable);
        }

        notificationsReference = null;
        notificationsListener = null;

        notificationsHandler = null;
        notificationsRunnable = null;

        CurrentUser.oldNotifications = new ArrayList<>();
        notificationsMap = new HashMap<>();
    }


//    /**
//     *
//     * @param context
//     */
//    private static void fetchUserNotifications(Context context) {
//        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
//        currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot notificationSnap : dataSnapshot.getChildren()) {
//                            generateNotification(context, notificationSnap, true, true);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//                });
//    }

//    /**
//     *
//     * @param context
//     */
//    private static void initializeNotificationEventHandler(Context context) {
//        CurrentUser.clearNotifications();
//        DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid());
//        CurrentUser.notificationsListener = new ChildEventListener() {
//            /* Invoked when app loads and when a new notification is created */
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                generateNotification(context, dataSnapshot, true, false);
//            }
//
//            /* Invoked when an old notification gets updated */
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                generateNotification(context, dataSnapshot, false, false);
//            }
//
//            /* Invoked when a notification is deleted */
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                String notificationId = dataSnapshot.getKey();
//                CurrentUser.removeNotification(notificationId);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                // Not sure what to do here
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
//            }
//        };
//
//        CurrentUser.notificationsReference = currentUserReference.child(Key.DB_REF_USER_NOTIFICATIONS);
//        CurrentUser.notificationsReference.addChildEventListener(CurrentUser.notificationsListener);
//
//        refreshNotificationRecyclerViewAdapter(context);
//
//        CurrentUser.notificationsHandler = new Handler();
//        CurrentUser.notificationsRunnable = new Runnable() {
//            public void run() {
//                Log.i(Default.TAG_DEBUG, "Fetching oldNotifications");
//                refreshNotificationRecyclerViewAdapter(context);
//                CurrentUser.notificationsHandler.postDelayed(this, Default.NOTIFICATION_UPDATE_INTERVAL);
//            }
//        };
//
//        CurrentUser.notificationsHandler.postDelayed(CurrentUser.notificationsRunnable, Default.NOTIFICATION_UPDATE_INTERVAL);
//
//    }

    /**
     *
     * @param context
     */
    private static void refreshNotificationRecyclerViewAdapter(Context context) {
        RecyclerView notificationRecyclerView = ((Activity) context).findViewById(R.id.notification_recycler_view);
        if (notificationRecyclerView != null && notificationRecyclerView.getAdapter() != null) {
            // Collections.sort(CurrentUser.getOldNotifications()); // TODO Might not be necessary
            notificationRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

//    /**
//     *
//     * @param context
//     * @param dataSnapshot
//     * @param isNewNotification
//     * @param refreshAdapter
//     */
//    private static void generateNotification(Context context, DataSnapshot dataSnapshot, boolean isNewNotification, boolean refreshAdapter) {
//        String notificationId = dataSnapshot.getKey();
//        NotificationType type = NotificationType.getType(notificationId);
//        String mostRecentUid = (String) dataSnapshot.child(Key.NOTIFICATION_MOST_RECENT_USER).getValue();
//        DatabaseReference allPostsReference = Default.ALL_POSTS_REFERENCE;
//
//        long actionTimestamp = (long) dataSnapshot.child(Key.NOTIFICATION_ACTION_TIMESTAMP).getValue();
//        long viewedTimestamp = (long) dataSnapshot.child(Key.NOTIFICATION_VIEWED_TIMESTAMP).getValue();
//        boolean viewed = viewedTimestamp > actionTimestamp;
//
//        OldNotification notification = new OldNotification();
//        notification.setType(type);
//        notification.setActionTimestamp(actionTimestamp);
//        notification.setViewed(viewed);
//
//        switch (type) {
//            case LIKE:
//            case REPOST:
//                String postId = type.decodeNotificationPostId(notificationId);
//                allPostsReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot postSnapshot) {
//                        if (postSnapshot.exists()) {
//                            PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
//                            prismPost.setPrismUser(CurrentUser.prismUser);
//                            notification.setPrismPost(prismPost);
//                            fetchMostRecentUser(context, notification, notificationId, mostRecentUid,
//                                    isNewNotification, refreshAdapter);
//                        }
//                    }
//
//                    @Override public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                break;
//            case FOLLOW:
//                fetchMostRecentUser(context, notification, notificationId, mostRecentUid, isNewNotification, refreshAdapter);
//                break;
//        }
//
//    }

//    /**
//     * TODO user DatabaseRead to fetch mostRecentUser details
//     * @param context
//     * @param notification
//     * @param notificationId
//     * @param mostRecentUid
//     * @param isNewNotification
//     * @param refreshAdapter
//     */
//    private static void fetchMostRecentUser(Context context, OldNotification notification, String notificationId, String mostRecentUid,
//                                            boolean isNewNotification, boolean refreshAdapter) {
//        DatabaseReference mostRecentUserRef = Default.USERS_REFERENCE.child(mostRecentUid);
//        mostRecentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot userSnapshot) {
//                PrismUser mostRecentUser = Helper.constructPrismUserObject(userSnapshot);
//                notification.setMostRecentUser(mostRecentUser);
//                updateNotificationsList(context, isNewNotification, notificationId, notification, refreshAdapter);
//            }
//
//            @Override public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    /**
     *
     * @param context
     * @param isNewNotification
     * @param notificationId
     * @param notification
     * @param refreshAdapter
     */
//    private static void updateNotificationsList(Context context, boolean isNewNotification, String notificationId, com.mikechoch.prism.attribute.OldNotification notification, boolean refreshAdapter) {
//        if (!isNewNotification) {
//            CurrentUser.removeNotification(notificationId);
//        }
//
//        CurrentUser.addNotification(notification, notificationId);
//
//        if (refreshAdapter) {
//            refreshNotificationRecyclerViewAdapter(context);
//        }
//    }
    
}
