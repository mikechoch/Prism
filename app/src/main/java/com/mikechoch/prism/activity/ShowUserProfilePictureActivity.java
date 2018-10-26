package com.mikechoch.prism.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.user_interface.ZoomControlLinearLayout;

public class ShowUserProfilePictureActivity extends AppCompatActivity {
    
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
                super.onBackPressed();
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

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        // Initialize all UI elements
        dismissClickRelativeLayout = findViewById(R.id.show_user_profile_picture_coordinate_layout);
        userProfilePictureZoomControlLinearLayout = findViewById(R.id.user_profile_picture_linear_layout);
        largeUserProfilePictureImageView = findViewById(R.id.large_user_profile_picture_image_view);

        prismUser = (PrismUser) getIntent().getSerializableExtra(Default.PRISM_USER_EXTRA);

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
                .load(prismUser.getProfilePicture().getHiResProfilePicUri())
                .apply(new RequestOptions().fitCenter().override((int) (Default.screenWidth * 0.8)))
                .into(new BitmapImageViewTarget(largeUserProfilePictureImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (5 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        ShowUserProfilePictureActivity.this,
                                        largeUserProfilePictureImageView,
                                        prismUser.getProfilePicture().isDefault(),
                                        resource,
                                        imageViewPadding);
                        largeUserProfilePictureImageView.setImageDrawable(profilePictureDrawable);

                        startPostponedEnterTransition();
                    }
                });

        userProfilePictureZoomControlLinearLayout.getLayoutParams().height = (int) (Default.screenWidth * 0.8);
        userProfilePictureZoomControlLinearLayout.getLayoutParams().width = (int) (Default.screenWidth * 0.8);
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
