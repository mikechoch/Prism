package com.mikechoch.prism.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.user_interface.ZoomControlLinearLayout;
import com.mikechoch.prism.attribute.PrismUser;

/**
 * Created by mikechoch on 3/10/18.
 */

public class ShowUserProfilePictureActivity extends AppCompatActivity {

    /*
    * Globals
    */
    private float scale;
    private int screenWidth;
    private int screenHeight;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private RelativeLayout dismissClickRelativeLayout;
    private ZoomControlLinearLayout userProfilePictureZoomControlLinearLayout;
    private ImageView largeUserProfilePictureImageView;

    private PrismUser prismUser;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu., menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_user_profile_picture_activity_layout);

        // Get the screen density of the current phone for later UI element scaling
        scale = getResources().getDisplayMetrics().density;

        // Get the screen width and height of the current phone
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        // Initialize all UI elements
        dismissClickRelativeLayout = findViewById(R.id.show_user_profile_picture_coordinate_layout);
        userProfilePictureZoomControlLinearLayout = findViewById(R.id.user_profile_picture_linear_layout);
        largeUserProfilePictureImageView = findViewById(R.id.large_user_profile_picture_image_view);

        prismUser = getIntent().getParcelableExtra("PrismUser");

        setupUIElements();
    }

    /**
     * Setup the toolbar
     */
    private void setupToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    /**
     * Setup the status bar so that it is transparent
     */
    private void setupStatusBar() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * Dismiss the activity if the background is pressed
     */
    private void setupDismissClickCoordinateLayout() {
        dismissClickRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowUserProfilePictureActivity.super.onBackPressed();
            }
        });
    }

    /**
     * Use Glide to populate the largeUserProfilePictureImageView
     * Setup the zoom control and also set postponed shared transition
     */
    private void setupUserProfilePicture() {
        supportStartPostponedEnterTransition();

        Glide.with(this)
                .asBitmap()
                .load(prismUser.getProfilePicture().hiResUri)
                .apply(new RequestOptions().fitCenter().override((int) (screenWidth * 0.8)))
                .into(new BitmapImageViewTarget(largeUserProfilePictureImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        if (!prismUser.getProfilePicture().isDefault) {
                            int whiteOutlinePadding = (int) (5 * scale);
                            largeUserProfilePictureImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                            largeUserProfilePictureImageView.setBackground(getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                        } else {
                            largeUserProfilePictureImageView.setPadding(0, 0, 0, 0);
                            largeUserProfilePictureImageView.setBackground(null);
                        }

                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        drawable.setCircular(true);
                        largeUserProfilePictureImageView.setImageDrawable(drawable);

                        startPostponedEnterTransition();
                    }
                });

        userProfilePictureZoomControlLinearLayout.getLayoutParams().height = (int) (screenWidth * 0.8);
        userProfilePictureZoomControlLinearLayout.getLayoutParams().width = (int) (screenWidth * 0.8);
        userProfilePictureZoomControlLinearLayout.addContext(this);
        userProfilePictureZoomControlLinearLayout.addImageView(largeUserProfilePictureImageView);
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();
        setupStatusBar();

        setupDismissClickCoordinateLayout();
        setupUserProfilePicture();
    }

}
