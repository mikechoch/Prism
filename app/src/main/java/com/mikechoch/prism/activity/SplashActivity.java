package com.mikechoch.prism.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mikechoch on 1/21/18.
 */

public class SplashActivity extends AppCompatActivity {

    /*
     * Global variables
     */
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        // TODO delete this
        invokeFakePushNotification();

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);

        // Setup animation and start animation for iconImageView
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(750);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iconImageView.startAnimation(rotateAnimation);

        new IntentLoaderTask().execute();
    }

    private void invokeFakePushNotification() {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Default.ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_prism)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera_iris_black_36dp))   // TODO in real notification, we display profile pic here
                .setContentTitle("mikechoch and 69 others")
                .setContentText("like your post")
                .setAutoCancel(false) //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setLights(Color.RED, 3000, 3000)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify("MERGE", 111, notificationBuilder.build());
        }
    }

    /**
     * AsyncTask for handling 1000ms delay on animation and then deciding if user is logged in
     * If logged in already go to MainActivity
     * Otherwise, go to LoginActivity
     */
    private class IntentLoaderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... v) {
            Default.scale = getResources().getDisplayMetrics().density;
            Default.screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            Default.screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            Default.sourceSansProLight = Typeface.createFromAsset(getAssets(), Default.sourceSansProLightPath);
            Default.sourceSansProBold = Typeface.createFromAsset(getAssets(), Default.sourceSansProBoldPath);

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String prismPostId = extras.getString(NotificationKey.PRISM_POST_ID);
                String prismUserId = extras.getString(NotificationKey.PRISM_USER_ID);
                if (prismPostId != null) {
                    intent = new Intent(SplashActivity.this, PrismPostDetailActivity.class);
                    intent.putExtra(NotificationKey.PRISM_POST_ID, prismPostId);
                } else if (prismUserId != null) {
                    intent = new Intent(SplashActivity.this, PrismUserProfileActivity.class);
                    intent.putExtra(NotificationKey.PRISM_USER_ID, prismUserId);
                }
            }

            boolean isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
            if (!isSignedIn) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            } else {
                CurrentUser.prepareAppForUser(SplashActivity.this, intent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

}
