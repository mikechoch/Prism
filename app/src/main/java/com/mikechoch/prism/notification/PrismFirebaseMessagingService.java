package com.mikechoch.prism.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.SplashActivity;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.helper.BitmapHelper;

import java.io.IOException;
import java.net.URL;

public class PrismFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * TODO everything in this class needs to be commented
     */

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        int notificationId = Integer.parseInt(remoteMessage.getData().get(NotificationKey.NOTIFICATION_ID));
        int otherUserCount = getOtherUserCount(notificationId);
        String message = getActionMessage(remoteMessage);
        String title = getMessageTitle(remoteMessage, otherUserCount);
        Bitmap profilePic = getUserProfilePic(remoteMessage);
        Bundle bundle = getNotificationBundle(otherUserCount);
        PendingIntent viewPendingIntent = getOnClickIntent(remoteMessage);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Default.ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_camera_iris_black_36dp)
                .setLargeIcon(BitmapHelper.getCircledBitmap(profilePic))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true) //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setLights(Color.RED, 3000, 3000)
                .addExtras(bundle)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setContentIntent(viewPendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify("MERGE", notificationId, notificationBuilder.build());
        } else {
            Log.e(Default.TAG_NOTIFICATION, Message.CANNOT_NOTIFY_USER);
        }


    }

    /**
     *
     * @param otherUserCount
     * @return
     */
    private Bundle getNotificationBundle(int otherUserCount) {
        Bundle bundle = new Bundle();
        bundle.putInt("other_count", otherUserCount);
        return bundle;
    }

    /**
     *
     * @param remoteMessage
     * @return
     */
    private PendingIntent getOnClickIntent(RemoteMessage remoteMessage) {
        String prismPostId = remoteMessage.getData().get(NotificationKey.PRISM_POST_ID);
        String prismUserId = remoteMessage.getData().get(NotificationKey.PRISM_USER_ID);

        Intent splashIntent = new Intent(this, MainActivity.class);

        if (prismPostId != null) {
            splashIntent = new Intent(this, SplashActivity.class);
            splashIntent.putExtra(NotificationKey.PRISM_POST_ID, prismPostId);
        } else if (prismUserId != null) {
            splashIntent = new Intent(this, SplashActivity.class);
            splashIntent.putExtra(NotificationKey.PRISM_USER_ID, prismUserId);
        }

        return PendingIntent.getActivity(this, 0, splashIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     *
     * @param remoteMessage
     * @return
     */
    private Bitmap getUserProfilePic(RemoteMessage remoteMessage) {
        String profilePicUri = remoteMessage.getData().get(NotificationKey.USER_PROFILE_PIC);
        Bitmap profilePic = null;
        try {
            URL url = new URL(profilePicUri);
            profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profilePic;
    }

    /**
     *
     * @param remoteMessage
     * @return
     */
    private String getActionMessage(RemoteMessage remoteMessage) {
        return remoteMessage.getData().get(NotificationKey.MESSAGE);
    }

    /**
     *
     * @param remoteMessage
     * @param otherUserCount
     * @return
     */
    private String getMessageTitle(RemoteMessage remoteMessage, int otherUserCount) {
        String title = remoteMessage.getData().get(NotificationKey.MOST_RECENT_USER);
        if (otherUserCount == 1) {
            title += " and 1 other";
        } else if (otherUserCount > 1) {
            title += " and " + otherUserCount + " others";
        }
        return title;
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = "Admin Channel Name";
        String adminChannelDescription = "Admin Channel Description";
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(Default.ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    /**
     *
     * @param notificationId
     * @return
     */
    public int getOtherUserCount(int notificationId) {
        int otherUserCount = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
                if (notification.getId() == notificationId) {
                    otherUserCount = 1 + notification.getNotification().extras.getInt("other_count");
                }
            }
        }
        return otherUserCount;
    }
}
