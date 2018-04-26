package com.mikechoch.prism.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

import java.util.Random;

public class PrismFirebaseMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Default.ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.like_heart)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.repost_iris))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true) //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setLights(Color.RED, 3000, 3000)
                .setColor(Color.RED)
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
