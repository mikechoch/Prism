package com.mikechoch.prism.activity;

import android.app.Activity;
import android.content.Context;
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
import com.mikechoch.prism.constant.Message;
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

        new IntentLoaderTask().execute(this);
    }

    /**
     * AsyncTask for handling 1000ms delay on animation and then deciding if user is logged in
     * If logged in already go to MainActivity
     * Otherwise, go to LoginActivity
     */
    private static class IntentLoaderTask extends AsyncTask<Object, Object[], Object[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Object[] doInBackground(Object... params) {
            Context context = (Context) params[0];
            Default.initializeScreenSizeElements((Activity) context);
            Default.initializeTypeface((Activity) context);

            if (!Helper.isNetworkAvailable(context)) {
                IntentHelper.intentToNoInternetActivity(context);
            } else {

                DatabaseAction.performMaintenanceCheck(new OnMaintenanceCheckCallback() {
                    @Override
                    public void onAppVersionTooOld() {
                        IntentHelper.intentToUpdatedAppRequiredActivity(context);
                    }

                    @Override
                    public void onStatusActive() {
                        Intent intent = new Intent(context, MainActivity.class);
                        if (!CurrentUser.isUserSignedIn()) {
                            IntentHelper.intentToLoginActivity(context);
                        } else {
                            if (isNotificationIntent((Activity) context)) {
                                intent = getNotificationIntent(context, (Activity) context, intent);
                            }
                            CurrentUser.prepareAppForUser(context, intent);
                        }
                    }

                    @Override
                    public void onStatusUnderMaintenance(String message) {
                        IntentHelper.intentToUnderMaintenanceActivity(context, message);
                    }


                    @Override
                    public void onStatusCheckFailed(Exception e) {
                        IntentHelper.intentToUnderMaintenanceActivity(context, Message.DEFAULT_UNDER_MAINTENANCE_MESSAGE);
                    }
                });
            }

            return params;
        }

        @Override
        protected void onPostExecute(Object[] params) {
            super.onPostExecute(params);
        }
    }

    /**
     *
     * @param context
     * @return
     */
    private static boolean isNotificationIntent(Activity context) {
        return context.getIntent().getExtras() != null;
    }

    /**
     *
     * @param context
     * @param activity
     * @param intent
     * @return
     */
    private static Intent getNotificationIntent(Context context, Activity activity, Intent intent) {
        Bundle extras = activity.getIntent().getExtras();
        assert extras != null;
        String prismPostId = extras.getString(NotificationKey.PRISM_POST_ID);
        String prismUserId = extras.getString(NotificationKey.PRISM_USER_ID);
        if (prismPostId != null) {
            intent = new Intent(context, PrismPostDetailActivity.class);
            intent.putExtra(NotificationKey.PRISM_POST_ID, prismPostId);
        } else if (prismUserId != null) {
            intent = new Intent(context, PrismUserProfileActivity.class);
            intent.putExtra(NotificationKey.PRISM_USER_ID, prismUserId);
        }

        return intent;
    }


}


