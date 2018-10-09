package com.mikechoch.prism.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.callback.check.OnMaintenanceCheckCallback;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;

public class SplashActivity extends AppCompatActivity {

    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
            Default.initializeScreenSizeElements(SplashActivity.this);
            Default.initializeTypeface(SplashActivity.this);

            if (!Helper.isNetworkAvailable(SplashActivity.this)) {
                IntentHelper.intentToNoInternetActivity(SplashActivity.this);;
            } else {

                DatabaseAction.performMaintenanceCheck(new OnMaintenanceCheckCallback() {
                    @Override
                    public void onStatusActive() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        if (!CurrentUser.isUserSignedIn()) {
                            IntentHelper.intentToLoginActivity(SplashActivity.this);
                        } else {
                            if (isNotificationIntent()) {
                                intent = getNotificationIntent(intent);
                            }
                            CurrentUser.prepareAppForUser(SplashActivity.this, intent);
                        }
                    }

                    @Override
                    public void onStatusUnderMaintenance(String message) {
                        IntentHelper.intentToUnderMaintenancewActivity(SplashActivity.this, message);
                    }

                    @Override
                    public void onStatusCheckFailed(Exception e) {
                        Log.e(Default.TAG_DB, "Failed to perform maintenance check", e);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }


    private boolean isNotificationIntent() {
        return getIntent().getExtras() != null;
    }

    private Intent getNotificationIntent(Intent intent) {
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String prismPostId = extras.getString(NotificationKey.PRISM_POST_ID);
        String prismUserId = extras.getString(NotificationKey.PRISM_USER_ID);
        if (prismPostId != null) {
            intent = new Intent(SplashActivity.this, PrismPostDetailActivity.class);
            intent.putExtra(NotificationKey.PRISM_POST_ID, prismPostId);
        } else if (prismUserId != null) {
            intent = new Intent(SplashActivity.this, PrismUserProfileActivity.class);
            intent.putExtra(NotificationKey.PRISM_USER_ID, prismUserId);
        }

        return intent;
    }


}


