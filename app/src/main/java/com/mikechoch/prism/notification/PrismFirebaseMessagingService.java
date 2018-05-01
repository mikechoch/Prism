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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.DisplayUsersActivity;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.PrismUserProfileActivity;
import com.mikechoch.prism.activity.SplashActivity;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.type.NotificationType;

import java.io.IOException;
import java.net.URL;

public class PrismFirebaseMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        int notificationId = Integer.parseInt(remoteMessage.getData().get(NotificationKey.NOTIFICATION_ID));
        String mostRecentUser = remoteMessage.getData().get(NotificationKey.MOST_RECENT_USER);

        int otherUserCount = 0;
        for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
            if (notification.getId() == notificationId) {
                otherUserCount = 1 + notification.getNotification().extras.getInt("other_count");
            }
        }

        // TODO git commit --amend -m "New commit message"

        String title = mostRecentUser;
        if (otherUserCount == 1) {
            title += " and 1 other";
        } else if (otherUserCount > 1) {
            title += " and " + otherUserCount + " others";
        }
        String message = remoteMessage.getData().get(NotificationKey.MESSAGE);
        String profilePicUri = remoteMessage.getData().get(NotificationKey.USER_PROFILE_PIC);
        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.repost_iris);
        try {
            URL url = new URL(profilePicUri);
            profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bundle bundle = new Bundle();
        bundle.putInt("other_count", otherUserCount);

        // TODO String postId = remoteMessage.getData().get(NotificationKey.POST_ID);
        String prismPostId = remoteMessage.getData().get(NotificationKey.PRISM_POST_ID);
        String prismUserId = remoteMessage.getData().get(NotificationKey.PRISM_USER_ID);

        Intent activityIntent;
        if (prismPostId != null) {
            activityIntent = new Intent(this, DisplayUsersActivity.class);
            activityIntent.putExtra("postId", prismPostId);
        } else if (prismUserId != null) {
            activityIntent = new Intent(this, PrismUserProfileActivity.class);
            activityIntent.putExtra("userId", prismUserId);
        } else {
            activityIntent = new Intent(this, MainActivity.class);
        }
        Intent splashIntent = new Intent(this, SplashActivity.class);
        splashIntent.putExtra(Intent.EXTRA_INTENT, activityIntent);

        // TODO
        // Intent viewIntent = new Intent(this, MainActivity.class);
        // viewIntent.putExtra("postId", postId);
         PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, activityIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Default.ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_prism)
                .setLargeIcon(profilePic)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true) //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setLights(Color.RED, 3000, 3000)
                .addExtras(bundle)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentIntent(viewPendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify("MERGE", notificationId /* ID of notification */, notificationBuilder.build());


    }

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
}
