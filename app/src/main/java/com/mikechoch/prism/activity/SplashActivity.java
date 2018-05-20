package com.mikechoch.prism.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
    private Animation rotateAnimation;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        Default.scale = getResources().getDisplayMetrics().density;
        Default.screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        Default.screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        Default.sourceSansProLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        Default.sourceSansProBold = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Black.ttf");

        // Initialize all UI elements
        iconImageView = findViewById(R.id.icon_image_view);

        // Setup animation and start animation for iconImageView
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_rotate);
        iconImageView.startAnimation(rotateAnimation);

        intent = new Intent(SplashActivity.this, MainActivity.class);

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
            try {
                 Thread.sleep(0);

                boolean isSignedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
                if (!isSignedIn) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                  int enterAnim = isSignedIn ? R.anim.fade_in : 0;
//                  int exitAnim = isSignedIn ? R.anim.fade_out : 0;
//                  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, iconImageView, "icon");
//                  startActivity(intent, options.toBundle());
                    startActivity(intent);
                    finish();

                } else {
                    CurrentUser.prepareAppForUser(SplashActivity.this, intent);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);


        }
    }

}


//iconImageView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startActivity(intent, options.toBundle());
////                    overridePendingTransition(enterAnim, exitAnim);
//                            iconImageView.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    finish();
//                                }
//                            }, 1000);
//                        }
//                    }, 250);