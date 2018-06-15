package com.mikechoch.prism.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;

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
     *
     * @return
     */
    public boolean isUserSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null ||
                GoogleSignIn.getLastSignedInAccount(this) != null;
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

            if (!isUserSignedIn()) {
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
