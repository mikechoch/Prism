package com.mikechoch.prism.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fire.DatabaseRead;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.user_interface.InterfaceAction;
import com.mikechoch.prism.user_interface.ToolbarPullDownLayout;
import com.mikechoch.prism.user_interface.ZoomControlLinearLayout;

public class PrismPostDetailActivity extends AppCompatActivity {

    private int scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
    private int noScrollFlags = 0;

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private ToolbarPullDownLayout toolbarPullDownLayout;
    private CoordinatorLayout prismPostDetailCoordinateLayout;
    private NestedScrollView prismPostDetailNestedScrollView;
    private ScrollView prismPostDetailScrollView;
    private RelativeLayout prismPostDetailsRelativeLayout;
    private ImageView moreActionButton;
    private ImageView likeActionButton;
    private TextView likesCountTextView;
    private ImageView repostActionButton;
    private TextView repostCountTextView;
    private ZoomControlLinearLayout detailZoomControlLinearLayout;
    private ImageView detailImageView;
    private RelativeLayout userRelativeLayout;
    private ImageView detailUserProfilePictureImageView;
    private TextView detailUsernameTextView;
    private TextView detailPrismPostDateTextView;
    private TextView detailPrismPostDescriptionTextView;
    private ImageView collapsingToolbarCollapseUpButton;
    private ImageView collapsingToolbarDragArrow;

    private PrismPost prismPost;

    private Handler collapseUpButtonHandler = new Handler();
    private Runnable showCollapseUpButtonRunnable;
    private Runnable hideCollapseUpButtonRunnable;
    private Handler dragArrowHandler = new Handler();
    private Runnable showDragArrowRunnable;
    private Runnable hideDragArrowRunnable;
    private boolean shouldCollapseUpButtonShow = false;
    private Menu menu;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prism_post_detail_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setVisible(false);
            Drawable drawable = menuItem.getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.prism_post_detail_action_more:
                boolean isCurrentUserThePostCreator = Helper.isPrismUserCurrentUser(prismPost.getPrismUser());
                AlertDialog morePrismPostAlertDialog = InterfaceAction.createMorePrismPostAlertDialog(this, prismPost, isCurrentUserThePostCreator);
                morePrismPostAlertDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prism_post_detail_activity_layout);

        appBarLayout = findViewById(R.id.prism_post_detail_app_bar_layout);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbarPullDownLayout = findViewById(R.id.pull_down_relative_layout);

        prismPostDetailCoordinateLayout = findViewById(R.id.prism_post_detail_coordinate_layout);
        prismPostDetailNestedScrollView = findViewById(R.id.prism_post_detail_nested_scroll_view);
        prismPostDetailScrollView = findViewById(R.id.prism_post_detail_scroll_view);
        prismPostDetailsRelativeLayout = findViewById(R.id.prism_post_detail_relative_layout);
        moreActionButton = findViewById(R.id.prism_post_detail_more_action_button);
        likeActionButton = findViewById(R.id.prism_post_detail_like_action_button);
        likesCountTextView = findViewById(R.id.prism_post_detail_like_count);
        repostActionButton = findViewById(R.id.prism_post_detail_repost_action_button);
        repostCountTextView = findViewById(R.id.prism_post_detail_repost_count);
        detailZoomControlLinearLayout = findViewById(R.id.prism_post_detail_zoom_control_linear_layout);
        detailImageView = findViewById(R.id.prism_post_detail_image_view);
        userRelativeLayout = findViewById(R.id.prism_post_detail_user_relative_layout);
        detailUserProfilePictureImageView = findViewById(R.id.prism_post_detail_user_profile_picture_image_view);
        detailUsernameTextView = findViewById(R.id.prism_post_detail_username_text_view);
        detailPrismPostDateTextView = findViewById(R.id.prism_post_detail_date_text_view);
        detailPrismPostDescriptionTextView = findViewById(R.id.prism_post_description);
        collapsingToolbarCollapseUpButton = findViewById(R.id.collapsing_toolbar_collapse_up_button);
        collapsingToolbarDragArrow = findViewById(R.id.collapsing_toolbar_drag_arrow);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            prismPost = (PrismPost) getIntent().getSerializableExtra(Default.PRISM_POST_DETAIL_EXTRA);
            if (prismPost != null) {
                parseAllPrismPostData();
                setupInterfaceElements();
            } else {
                String postId = extras.getString(NotificationKey.PRISM_POST_ID);
                fetchPrismPostData(postId);
            }
        }
    }

    private void fetchPrismPostData(String postId) {
        DatabaseRead.fetchPrismPost(postId, new OnFetchPrismPostCallback() {
            @Override
            public void onSuccess(PrismPost post) {
                prismPost = post;
                parseAllPrismPostData();
                setupInterfaceElements();
            }

            @Override
            public void onPostNotFound() {
                Helper.toast(PrismPostDetailActivity.this, Message.POST_NOT_FOUND, true);
                onBackPressed();
            }

            @Override
            public void onPostAuthorNotFound() {
                // This would never happen, the app wouldn't log the user in
                // if their details do not exist
            }

            @Override
            public void onFailure(Exception e) {
                IntentHelper.resetApplication(PrismPostDetailActivity.this);
                // TODO Log this
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Get the Intent and then get the PrismPost parcelable
     * Set all of the global variables associated with the PrismPost
     */
    private void parseAllPrismPostData() {
        Bundle extras = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(Default.PRISM_POST_DETAIL_TRANSITION_NAME_EXTRA);
            detailImageView.setTransitionName(imageTransitionName);
        }

    }

    /**
     * A method to find height of the status bar
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * A method to find height of the action bar
     */
    private int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * A method to find height of the status bar
     */
    private int getBottomNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier(Default.BOTTOM_NAVIGATION_BAR_NAME,
                Default.BOTTOM_NAVIGATION_BAR_DEF_TYPE,
                Default.BOTTOM_NAVIGATION_BAR_DEF_PACKAGE);
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Setup the addOnOffsetChangedListener so when the appBarLayout is expanded or collapsed,
     * the drag and scroll animations will show accordingly
     */
    private void setupAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if ((Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange()) == 0) {
                    // Collapsed
                    hideCollapseUpButton(0);
                } else if (Math.abs(verticalOffset) == 0) {
                    // Expanded
                    if (shouldCollapseUpButtonShow) {
                        shouldCollapseUpButtonShow = false;
                        showCollapseUpButton(0);
                        hideCollapseUpButton(4000);
                    }
                } else {
                    // Between
                    shouldCollapseUpButtonShow = true;
                    hideCollapseUpButton(0);
                }
            }
        });
    }

    /**
     * Setup the toolbar and back button to return to PrismUserProfileActivity
     */
    private void setupToolbar() {
        toolbar.setTitle("");
        toolbar.bringToFront();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
     * Make the height of the ToolbarPullDownLayout the height of the action bar
     * Give the ToolbarPullDownLayout the parent view and all children scroll views
     */
    private void setupToolbarPullDownLayout() {
        toolbarPullDownLayout.getLayoutParams().height = getActionBarHeight();
        toolbarPullDownLayout.addParentView(this, prismPostDetailCoordinateLayout);
        ViewGroup[] scrollViews = {prismPostDetailNestedScrollView, prismPostDetailScrollView};
        toolbarPullDownLayout.addScrollViews(scrollViews);
    }

    /**
     * Setup the PrismPostImageView so that postponed shared transition is supported
     * Populate the ImageVie with Glide
     * The image size dictates functionality for the toolbar, collapsingToolbar, statusBar, and appBarLayout
     * ZoomControlLinearLayout is setup for the ImageView, which controls all zoom functionality
     */
    private void setupPrismPostImageView() {
        populatePrismPostImageView();

        detailZoomControlLinearLayout.addContext(this);
        detailZoomControlLinearLayout.addImageView(detailImageView);
        detailZoomControlLinearLayout.addToolbarPullDownLayout(toolbarPullDownLayout);
        detailZoomControlLinearLayout.addScrollViews(new ViewGroup[]{prismPostDetailNestedScrollView, prismPostDetailScrollView});
    }

    /**
     * Method to populate and constrain all elements around the PrismPostImageView
     */
    private void populatePrismPostImageView() {
        Glide.with(this)
                .asBitmap()
                .load(prismPost.getImage())
                .apply(new RequestOptions().fitCenter())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        // Set the height of the appBarLayout, detailImageView, and toolbar
                        // This ensures proper UI response when scrolling the image and info window
                        appBarLayout.getLayoutParams().height = resource.getHeight();
                        detailImageView.getLayoutParams().height = resource.getHeight();

                        // Calculate the PrismPost info window height
                        int userInfoHeight = detailUserProfilePictureImageView.getHeight() +
                                detailPrismPostDescriptionTextView.getHeight() +
                                prismPostDetailNestedScrollView.getPaddingTop() +
                                prismPostDetailNestedScrollView.getPaddingBottom();

                        // Check that the image height is larger or equal to actual screen height
                        // If so, set ScaleType to CENTER_CROP
                        // Otherwise, set ScaleType to FIT_START
                        boolean isLongPortraitImage = resource.getHeight() >= Default.screenHeight;
                        detailImageView.setScaleType(isLongPortraitImage ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_START);

                        // Check that the image and info window height is larger or equal to actual screen height
                        // If so, enable collapsing toolbar using scroll flags
                        // Otherwise, disable collapsing toolbar using scroll flags
                        boolean isScrollImage = (resource.getHeight() + userInfoHeight) >= Default.screenHeight;
                        int toolbarHeight = isScrollImage ? (Default.screenHeight - getBottomNavigationBarHeight() - userInfoHeight) : resource.getHeight();
                        boolean isToolbarHeightNegative = toolbarHeight <= getActionBarHeight();
//                        toolbar.getLayoutParams().height = isToolbarHeightNegative ? getActionBarHeight() : toolbarHeight;

                        int toolbarTopMarginPadding = getStatusBarHeight();
                        if (isScrollImage) {
                            toolbar.getLayoutParams().height = getActionBarHeight();
                            int toolbarBottomMargin = (isToolbarHeightNegative ? getActionBarHeight() : toolbarHeight) - getActionBarHeight();
                            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
                            params.setMargins(0, toolbarTopMarginPadding, 0, toolbarBottomMargin);
                            toolbar.setLayoutParams(params);
                        } else {
                            toolbar.getLayoutParams().height = getStatusBarHeight() + getActionBarHeight();
                            toolbar.setPadding(0, toolbarTopMarginPadding, 0, 0);
                        }

                        // Set scroll flags for collapsingToolbarLayout containing the PrismPost image
                        AppBarLayout.LayoutParams collapsingToolbarLayoutLayoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                        collapsingToolbarLayoutLayoutParams.setScrollFlags(isScrollImage ? scrollFlags : noScrollFlags);
                        collapsingToolbarLayout.setLayoutParams(collapsingToolbarLayoutLayoutParams);

                        return false;
                    }
                })
                .into(detailImageView);
    }

    /**
     * Setup all prismPostUserInfo
     * Use Glide to populate the profilePicture
     * Add a clickListener to the view so it takes you to the user profile
     * Populate the prismPost description
     */
    private void setupPrismPostUserInfo() {
        userRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToUserProfileActivity(PrismPostDetailActivity.this, prismPost.getPrismUser());
            }
        });

        Glide.with(this)
                .asBitmap()
                .load(prismPost.getPrismUser().getProfilePicture().getLowResProfilePicUri())
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(detailUserProfilePictureImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (1 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        PrismPostDetailActivity.this,
                                        detailUserProfilePictureImageView,
                                        prismPost.getPrismUser().getProfilePicture().isDefault(),
                                        resource,
                                        imageViewPadding);
                        detailUserProfilePictureImageView.setImageDrawable(profilePictureDrawable);
                    }
                });

        detailUsernameTextView.setText(prismPost.getPrismUser().getUsername());
        detailPrismPostDateTextView.setText(Helper.getFancyDateDifferenceString(prismPost.getTimestamp() * -1));


        //TODO: Figure out how we should display the description in TextView
        String prismPostDescription = prismPost.getCaption();
        SpannableString spannableString = Helper.createClickableTagsInString(this, prismPostDescription);
        detailPrismPostDescriptionTextView.setText(spannableString);
        detailPrismPostDescriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        detailPrismPostDescriptionTextView.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Setup the like action button for the PrismPost
     * When like action button is clicked run handleLikeButtonClick method for updating Firebase
     * When like count TextView is clicked go to DisplayUsersActivity and shows all users that
     * liked the PrismPost
     */
    private void setupLikeActionButton() {
        InterfaceAction.setupLikeActionButton(this, null, likeActionButton, CurrentUser.hasLiked(prismPost));
        likeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLikeButtonClick();
            }
        });

        likesCountTextView.setText(String.valueOf(prismPost.getLikes()));
        likesCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismPostDetailActivity.this, prismPost.getPostId(), Default.DISPLAY_USERS_LIKE_CODE);
            }
        });
    }

    /**
     * Check liked_posts_map HashMap if it contains the postId or not. If it contains
     * the postId, then firebaseUser has already liked the post and perform UNLIKE operation
     * If it doesn't exist, firebaseUser has not liked it yet, and perform LIKE operation
     * Operation LIKE (performLIKE = true): does 3 things. First it adds the the firebaseUser's
     * uid to the LIKED_USERS table under the post. Then it adds the postId to the
     * USER_LIKES table under the firebaseUser. Then it adds the postId and timestamp to the
     * local liked_posts_map HashMap so that recycler view can update
     * Operation UNLIKE (performLike = false): undoes above 3 things
     * TODO: update comments
     */
    private void handleLikeButtonClick() {
        boolean performLike = !CurrentUser.hasLiked(prismPost);
        performUIActivitiesForLike(performLike);

        if (performLike) {
            DatabaseAction.performLike(prismPost);
        } else {
            DatabaseAction.performUnlike(prismPost);
        }
    }

    /**
     * Using InterfaceAction, start the animation for liking an image
     * Update the likes TextView locally
     * @param performLike - boolean if PrismPost has been liked by CurrentUser
     */
    private void performUIActivitiesForLike(boolean performLike) {
        InterfaceAction.startLikeActionButtonAnimation(this, likeActionButton, performLike);

        int newLikeCount = prismPost.getLikes() + (performLike ?  1 : -1);
        prismPost.setLikes(newLikeCount);
        likesCountTextView.setText(String.valueOf(newLikeCount));
    }

    /**
     * Setup the repost action button for the PrismPost
     */
    private void setupRepostActionButton() {
        InterfaceAction.setupRepostActionButton(this, repostActionButton, CurrentUser.hasReposted(prismPost));
        repostActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isPrismUserCurrentUser(prismPost.getUid())) {
                    Helper.toast(PrismPostDetailActivity.this, Message.CANNOT_REPOST_OWN_POST);
                    return;
                }

                boolean performRepost = !CurrentUser.hasReposted(prismPost);
                if (performRepost) {
                    AlertDialog repostConfirmationAlertDialog = InterfaceAction.createRepostConfirmationAlertDialog(PrismPostDetailActivity.this, prismPost, repostActionButton, repostCountTextView);
                    repostConfirmationAlertDialog.show();
                } else {
                    handleRepostButtonClick(false);
                }
            }
        });

        repostCountTextView.setText(String.valueOf(prismPost.getReposts()));
        repostCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismPostDetailActivity.this, prismPost.getPostId(), Default.DISPLAY_USERS_REPOST_CODE);
            }
        });
    }

    /**
     * Check reposted_posts_map HashMap if it contains the postId or not. If it contains
     * the postId, then firebaseUser has already reposted the post and perform UNREPOST operation
     * If it doesn't exist, firebaseUser has not reposted it yet, and perform REPOST operation
     * Operation REPOST (performRepost = true): does 3 things. First it adds the the firebaseUser's
     * uid to the REPOSTED_USERS table under the post. Then it adds the postId to the
     * USER_REPOSTS table under the firebaseUser. Then it adds the postId and timestamp to the
     * local reposted_posts_map HashMap so that recycler view can update
     * Operation UNREPOST (performRepost = false): undoes above 3 things
     * TODO: update comments
     * @param performRepost - boolean if PrismPost has been reposted by CurrentUser
     */
    private void handleRepostButtonClick(boolean performRepost) {
        performUIActivitiesForRepost(performRepost);

        if (performRepost) {
            DatabaseAction.performRepost(prismPost);
        } else {
            DatabaseAction.performUnrepost(prismPost);
        }
    }

    /**
     * Using InterfaceAction, start the animation for reposting an image
     * Update the reposts TextView locally
     * @param performRepost - boolean if PrismPost has been reposted by CurrentUser
     */
    private void performUIActivitiesForRepost(boolean performRepost) {
        InterfaceAction.startRepostActionButtonAnimation(this, repostActionButton, performRepost);

        int newRepostCount = prismPost.getReposts() + (performRepost ? 1 : -1);
        prismPost.setReposts(newRepostCount);
        repostCountTextView.setText(String.valueOf(newRepostCount));
    }

    /**
     * Method to setup the collapse up button onClickListener
     */
    private void setupCollapseUpButton() {
        collapsingToolbarCollapseUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCollapseUpButton(0);
                appBarLayout.setExpanded(false);
            }
        });
    }

    /**
     * Method to animate the drag arrow animation
     */
    private void animateCollapseUpButton() {
        collapsingToolbarCollapseUpButton.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        collapsingToolbarCollapseUpButton.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(800)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateCollapseUpButton();
                                    }
                                }).start();
                    }
                });
    }

    /**
     * Method to show the collapse up animation
     * @param millis - time in millis to perform collapse up button animation
     */
    private void showCollapseUpButton(int millis) {
        collapseUpButtonHandler.removeCallbacks(showCollapseUpButtonRunnable);
        showCollapseUpButtonRunnable = new Runnable() {
            public void run() {
                collapsingToolbarCollapseUpButton.setVisibility(View.VISIBLE);
                collapsingToolbarCollapseUpButton.animate()
                        .alpha(0.7f)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animateCollapseUpButton();
                            }
                }).start();
            }
        };
        collapseUpButtonHandler.postDelayed(showCollapseUpButtonRunnable, millis);
    }

    /**
     * Method to hide the collapse up animation
     * @param millis - time in millis to perform collapse up button animation
     */
    private void hideCollapseUpButton(int millis) {
        collapseUpButtonHandler.removeCallbacks(hideCollapseUpButtonRunnable);
        hideCollapseUpButtonRunnable = new Runnable() {
            public void run() {
                collapsingToolbarCollapseUpButton.animate()
                        .alpha(0f)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                collapsingToolbarCollapseUpButton.setVisibility(View.GONE);
                            }
                        }).start();
            }
        };
        collapseUpButtonHandler.postDelayed(hideCollapseUpButtonRunnable, millis);
    }

    /**
     * Method to setup the drag arrow UI
     * Adds a margin to the top of the arrow
     */
    private void setupDragArrow() {
        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) collapsingToolbarDragArrow.getLayoutParams();;
        params.setMargins(0, getStatusBarHeight() + params.topMargin, 0, 0);
        collapsingToolbarDragArrow.setLayoutParams(params);
    }

    /**
     * Method to animate the drag arrow animation
     */
    private void animateDragArrow() {
        collapsingToolbarDragArrow.animate()
                .translationYBy(200)
                .scaleX(0.3f)
                .scaleY(0.3f)
                .alpha(0f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        collapsingToolbarDragArrow.animate()
                                .translationYBy(-200)
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(0.7f)
                                .setDuration(0)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateDragArrow();
                                    }
                                });
                    }
                }).start();
    }

    /**
     * Method to show the drag arrow animation
     * @param millis - time in millis to perform drag arrow animation
     */
    private void showDragArrow(int millis) {
        dragArrowHandler.removeCallbacks(showDragArrowRunnable);
        showDragArrowRunnable = new Runnable() {
            public void run() {
                collapsingToolbarDragArrow.setVisibility(View.VISIBLE);
                collapsingToolbarDragArrow.animate()
                        .alpha(0.7f)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animateDragArrow();
                            }
                        }).start();
            }
        };
        dragArrowHandler.postDelayed(showDragArrowRunnable, millis);
    }

    /**
     * Method to hide the drag arrow animation
     * @param millis - time in millis to perform drag arrow animation
     */
    private void hideDragArrow(int millis) {
        dragArrowHandler.removeCallbacks(hideDragArrowRunnable);
        hideDragArrowRunnable = new Runnable() {
            public void run() {
                collapsingToolbarDragArrow.animate()
                        .alpha(0f)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                collapsingToolbarDragArrow.setVisibility(View.GONE);
                            }
                        }).start();
            }
        };
        dragArrowHandler.postDelayed(hideDragArrowRunnable, millis);
    }

    /**
     * Three action buttons are shown for each PrismPost
     * Like button likes the PrismPost
     * Repost button reposts the PrismPost to the users profile
     * More button offers a few options
     */
    private void setupActionButtons() {
        setupLikeActionButton();
        setupRepostActionButton();
        setupMoreActionButton();
        setupCollapseUpButton();
        setupDragArrow();
    }

    /**
     * Setup more action button onClick so that AlertDialog shows
     * for Sharing, Reporting, and Deleting a PrismPost
     */
    private void setupMoreActionButton() {
        moreActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterfaceAction.startMoreActionButtonAnimation(moreActionButton);

                boolean isCurrentUserThePostCreator = Helper.isPrismUserCurrentUser(prismPost.getPrismUser());
                AlertDialog morePrismPostAlertDialog = InterfaceAction.createMorePrismPostAlertDialog(PrismPostDetailActivity.this, prismPost, isCurrentUserThePostCreator);
                morePrismPostAlertDialog.show();
            }
        });
    }

    /**
     * Setup elements of current Activity
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupInterfaceElements() {
        likesCountTextView.setTypeface(Default.sourceSansProLight);
        repostCountTextView.setTypeface(Default.sourceSansProLight);
        detailUsernameTextView.setTypeface(Default.sourceSansProBold);
        detailPrismPostDateTextView.setTypeface(Default.sourceSansProLight);
        detailPrismPostDescriptionTextView.setTypeface(Default.sourceSansProLight);

        showCollapseUpButton(0);
        showDragArrow(0);
        hideCollapseUpButton(4000);
        hideDragArrow(3000);

        setupAppBarLayout();
        setupToolbar();
        setupStatusBar();
        setupToolbarPullDownLayout();
        setupPrismPostUserInfo();
        setupPrismPostImageView();
        setupActionButtons();
    }

}
