package com.mikechoch.prism.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.OptionRecyclerViewAdapter;
import com.mikechoch.prism.adapter.ProfileViewPagerAdapter;
import com.mikechoch.prism.attribute.LinkedPrismPosts;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostsCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismUserCallback;
import com.mikechoch.prism.callback.fetch.OnFetchUserProfileCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fire.DatabaseRead;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.DisplayUserType;
import com.mikechoch.prism.type.ProfilePictureOption;
import com.mikechoch.prism.user_interface.InterfaceAction;
import com.mikechoch.prism.user_interface.PrismPostStaggeredGridRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PrismUserProfileActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    // Shared Current User and Non Current User
    private ImageView toolbarUserProfilePicImageView;
    private TextView toolbarUserUsernameTextView;
    private TextView userUsernameTextView;
    private TextView userFullNameTextView;
    private SwipeRefreshLayout profileSwipeRefreshLayout;
    private NestedScrollView profileNestedScrollView;
    private ImageView userProfilePicImageView;
    private LinearLayout followersRelativeLayout;
    private TextView followersCountTextView;
    private TextView followersLabelTextView;
    private TextView postsCountTextView;
    private TextView postsLabelTextView;
    private LinearLayout followingRelativeLayout;
    private TextView followingCountTextView;
    private TextView followingLabelTextView;
    private LinearLayout noUploadsOrRepostsLinearLayout;
    private TextView noUploadsOrRepostsTextView;

    // Current User
    private ImageView accountEditInfoButton;
    private TabLayout userPostsTabLayout;
    private ViewPager userPostsViewPager;
    private ProfileViewPagerAdapter userPostsViewPagerAdapter;

    // Non Current User
    private Button toolbarFollowButton;
    private Button followUserButton;
    private PrismPostStaggeredGridRecyclerView prismPostStaggeredGridRecyclerView;

    private ArrayList<PrismPost> prismUserUploadedAndRepostedPostsArrayList;
    private ArrayList<PrismPost> prismUserLikedPostsArrayList;

    private PrismUser prismUser;
    private boolean isCurrentUser;
    private boolean[] areUploadedAndRepostedPostsFetched = {false, false};


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
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prism_user_profile_activity_layout);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        toolbarUserProfilePicImageView = findViewById(R.id.toolbar_user_profile_profile_picture_image_view);
        toolbarUserUsernameTextView = findViewById(R.id.toolbar_user_profile_username_text_view);
        profileSwipeRefreshLayout = findViewById(R.id.profile_swipe_refresh_layout);
        profileNestedScrollView = findViewById(R.id.profile_scroll_view);
        userUsernameTextView = findViewById(R.id.user_profile_username_text_view);
        userFullNameTextView = findViewById(R.id.user_profile_full_name_text_view);
        userProfilePicImageView = findViewById(R.id.user_profile_profile_picture_image_view);
        followersRelativeLayout = findViewById(R.id.followers_linear_layout);
        followersCountTextView = findViewById(R.id.followers_count_text_view);
        followersLabelTextView = findViewById(R.id.followers_label_text_view);
        postsCountTextView = findViewById(R.id.posts_count_text_view);
        postsLabelTextView = findViewById(R.id.posts_label_text_view);
        followingRelativeLayout = findViewById(R.id.following_linear_layout);
        followingCountTextView = findViewById(R.id.following_count_text_view);
        followingLabelTextView = findViewById(R.id.following_label_text_view);
        noUploadsOrRepostsLinearLayout = findViewById(R.id.prism_user_profile_no_uploads_or_reposts_linear_layout);
        noUploadsOrRepostsTextView = findViewById(R.id.prism_user_profile_no_uploads_or_reposts_text_view);

        accountEditInfoButton = findViewById(R.id.toolbar_edit_account_information_image_view);
        userPostsTabLayout = findViewById(R.id.current_user_profile_tab_layout);
        userPostsViewPager = findViewById(R.id.current_user_profile_view_pager);

        toolbarFollowButton = findViewById(R.id.toolbar_follow_user_button);
        followUserButton = findViewById(R.id.follow_user_button);

        prismUserUploadedAndRepostedPostsArrayList = new ArrayList<>();
        prismUserLikedPostsArrayList = new ArrayList<>();

        prismUser = getPrismUserIntentData();
        if (prismUser != null) {
            setupInterfaceElements();
        } else {
            handleIncomingNotificationClick();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     *
     */
    private void setupSwipeRefreshLayout() {
        profileSwipeRefreshLayout.setColorSchemeResources(InterfaceAction.swipeRefreshLayoutColors);
        profileSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPrismUserInterface();
            }
        });
    }

    /**
     * Attempt to get the Serialized PrismUser from incoming Intent
     * @return - PrismUser to populate profile page interface elements with
     *           PrismUser could be null if this comes from a notification PendingIntent click
     */
    private PrismUser getPrismUserIntentData() {
        return (PrismUser) getIntent().getSerializableExtra(Default.PRISM_USER_EXTRA);
    }

    /**
     * PrismUser will be fetched based off the PrismUser String id got from incoming Intent
     * If PrismUser String id is null, toast no user found and go back to MainActivity
     */
    private void handleIncomingNotificationClick() {
        String prismUserId = getNotificationIntentData();
        if (prismUserId != null) {
            pullPrismUserDetails(prismUserId);
        } else {
            Helper.toast(PrismUserProfileActivity.this, Message.USER_NOT_FOUND);
            onBackPressed();
        }
    }

    /**
     * Attempt to get PrismUser String id from notification PendingIntent click
     * @return - String PrismUser id to fetch PrismUser for
     */
    private String getNotificationIntentData() {
        Bundle bundle = getIntent().getExtras();
        String notificationPrismUserId = null;
        if (bundle != null) {
            notificationPrismUserId = bundle.getString(NotificationKey.PRISM_USER_ID);
        }
        return notificationPrismUserId;
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Setup the AppBarLayout knowing when it is fully collapsed/ expanded
     * The percentage of the collapsed will be used to
     * set the alpha of the toolbar and collapsingToolbar elements
     */
    private void setupAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                // Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                // Based on scrollRange adjust alpha of profile picture ImageViews
                // As the AppBarLayout is hidden, the large ImageView will hide
                // and the toolbar ImageView will show
                // As the AppBarLayout is shown, the large ImageView will show
                // and the toolbar ImageView will hide
                float toolbarElementsAlpha = Math.abs(verticalOffset / ((float) scrollRange));
                toolbarUserProfilePicImageView.setAlpha(toolbarElementsAlpha);
                toolbarUserUsernameTextView.setAlpha(toolbarElementsAlpha);
                toolbarFollowButton.setAlpha(toolbarElementsAlpha);
            }
        });
    }

    /**
     * Setup interface elements of current activity
     */
    private void setupInterfaceElements() {
        toolbarUserUsernameTextView.setTypeface(Default.sourceSansProBold);
        toolbarFollowButton.setTypeface(Default.sourceSansProLight);
        followUserButton.setTypeface(Default.sourceSansProLight);
        followersCountTextView.setTypeface(Default.sourceSansProBold);
        followersLabelTextView.setTypeface(Default.sourceSansProLight);
        postsCountTextView.setTypeface(Default.sourceSansProBold);
        postsLabelTextView.setTypeface(Default.sourceSansProLight);
        followingCountTextView.setTypeface(Default.sourceSansProBold);
        followingLabelTextView.setTypeface(Default.sourceSansProLight);
        userUsernameTextView.setTypeface(Default.sourceSansProBold);
        userFullNameTextView.setTypeface(Default.sourceSansProLight);
        noUploadsOrRepostsTextView.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        setupAppBarLayout();

        isCurrentUser = Helper.isPrismUserCurrentUser(prismUser);

        if (isCurrentUser) {
            CurrentUser.refreshUser(new OnFetchUserProfileCallback() {
                @Override
                public void onSuccess() {
                    prismUser = CurrentUser.getPrismUser();
                    fetchUserContent();
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        } else {
            fetchUserContent();
        }

    }

    /**
     * Using PrismUser String id pull PrismUser details for populating the profile page
     * @param prismUserId - String PrismUser id used to fetch PrismUser from Firebase database
     */
    private void pullPrismUserDetails(String prismUserId) {
        DatabaseRead.fetchPrismUser(prismUserId, new OnFetchPrismUserCallback() {
            @Override
            public void onSuccess(PrismUser user) {
                prismUser = user;
                setupInterfaceElements();
            }

            @Override
            public void onUserNotFound() {
                Helper.toast(PrismUserProfileActivity.this, Message.USER_NOT_FOUND);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    /**
     * After global PrismUser is defined attempt to fetch user content
     * If the PrismUser is CurrentUser:
     * Get the uploaded and reposted PrismPosts from CurrentUser class
     * If the PrismUser is not CurrentUser:
     * Use DatabaseRead to fetch uploaded and reposted PrismPosts from Firebase for PrismUser
     */
    private void fetchUserContent() {

        if (isCurrentUser) {
            prismUserUploadedAndRepostedPostsArrayList.clear();
            prismUserLikedPostsArrayList.clear();
            prismUserUploadedAndRepostedPostsArrayList.addAll(CurrentUser.getUserUploadsAndReposts());
            prismUserLikedPostsArrayList.addAll(CurrentUser.getUserLikes());

            sortPostsOnUserPage();
            setupPrismUserInterface();
        } else {
            DatabaseRead.fetchPrismUserUploadedPosts(prismUser, new OnFetchPrismPostsCallback() {
                @Override
                public void onSuccess(LinkedPrismPosts linkedPrismPosts) {
                    prismUserUploadedAndRepostedPostsArrayList.addAll(linkedPrismPosts.getPrismPosts());

                    areUploadedAndRepostedPostsFetched[0] = true;
                    if (areUserPostsFetched()) {
                        sortPostsOnUserPage();
                        setupPrismUserInterface();
                    }
                }

                @Override
                public void onPrismPostsNotFound() {
                    // User has not uploaded any posts
                    // This interface is shown by default and if the ArrayList is not empty will be handled appropriately
                    areUploadedAndRepostedPostsFetched[0] = true;
                    setupPrismUserInterface();
                }

                @Override
                public void onFailure(Exception e) {
                    areUploadedAndRepostedPostsFetched[0] = true;
                    Helper.toast(PrismUserProfileActivity.this, Message.FETCH_USER_UPLOADS_FAIL);
                    setupPrismUserInterface();
                }
            });

            DatabaseRead.fetchPrismUserRepostedPosts(prismUser, new OnFetchPrismPostsCallback() {
                @Override
                public void onSuccess(LinkedPrismPosts linkedPrismPosts) {
                    for (PrismPost post : linkedPrismPosts.getPrismPosts()) {
                        post.setIsReposted(true);
                        prismUserUploadedAndRepostedPostsArrayList.add(post);
                    }

                    areUploadedAndRepostedPostsFetched[1] = true;
                    if (areUserPostsFetched()) {
                        sortPostsOnUserPage();
                        setupPrismUserInterface();
                    }
                }

                @Override
                public void onPrismPostsNotFound() {
                    // User has not reposted any posts
                    // This interface is shown by default and if the ArrayList is not empty will be handled appropriately
                    areUploadedAndRepostedPostsFetched[1] = true;
                    setupPrismUserInterface();
                }

                @Override
                public void onFailure(Exception e) {
                    areUploadedAndRepostedPostsFetched[1] = true;
                    Helper.toast(PrismUserProfileActivity.this, Message.FETCH_USER_REPOSTS_FAIL);
                    setupPrismUserInterface();
                }
            });
        }
    }

    /**
     * Check the boolean Array that uploaded (index 0) and reposted (index 1) are both true
     * This validates that both sets of PrismPosts have been fetched from Firebase
     * Now the interface setup can continue
     * @return - boolean representing both indices being true
     */
    private boolean areUserPostsFetched() {
        return areUploadedAndRepostedPostsFetched[0] && areUploadedAndRepostedPostsFetched[1];
    }

    /**
     * Using the Collection class and a Comparator<PrismPost>, sort the Array by long timestamp
     */
    private void sortPostsOnUserPage() {
        Collections.sort(prismUserUploadedAndRepostedPostsArrayList, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return Long.compare(p1.getTimestamp(), p2.getTimestamp());
            }
        });

        Collections.sort(prismUserLikedPostsArrayList, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return Long.compare(p1.getTimestamp(), p2.getTimestamp());
            }
        });
    }

    /**
     * Setup all user info based elements
     * User full name TextView, username TextView populated, and toolbar username populated
     * Add click listener for clicking profile picture:
     * CurrentUser: Show AlertDialog of options
     * Non-CurrentUser: Show hi-res version of profile picture in ShowUserProfilePictureActivity
     */
    private void setupUserInfo() {
        userFullNameTextView.setText(prismUser.getFullName());
        userUsernameTextView.setText(prismUser.getUsername());
        toolbarUserUsernameTextView.setText(prismUser.getUsername());

        // Using Glide to populate the toolbar ImageView profile picture
        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(prismUser.getProfilePicture().getLowResProfilePicUri())
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(toolbarUserProfilePicImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (1 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        PrismUserProfileActivity.this,
                                        toolbarUserProfilePicImageView,
                                        prismUser.getProfilePicture().isDefault(),
                                        resource,
                                        imageViewPadding);
                        toolbarUserProfilePicImageView.setImageDrawable(profilePictureDrawable);
                    }
                });

        // Using Glide to populate the large ImageView profile picture
        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(prismUser.getProfilePicture().getHiResProfilePicUri())
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(userProfilePicImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (2 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        PrismUserProfileActivity.this,
                                        userProfilePicImageView,
                                        prismUser.getProfilePicture().isDefault(),
                                        resource,
                                        imageViewPadding);
                        userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                    }
                });

        // Handle large ImageView profile picture click
        // Intents to ShowUserProfilePictureActivity and shows large scale profile picture
        userProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrentUser) {
                    AlertDialog setProfilePictureAlertDialog = createSetProfilePictureAlertDialog();
                    setProfilePictureAlertDialog.show();
                } else {
                    IntentHelper.intentToShowUserProfilePictureActivity(PrismUserProfileActivity.this, prismUser, userProfilePicImageView);
                }
            }
        });
    }

    /**
     * Setup the userProfilePicImageView so it is populated with a Default or custom picture
     * When clicked it will show an AlertDialog of options for changing the picture
     */
    private void setupUserStats() {
        postsCountTextView.setText(String.valueOf(prismUser.getUploadCount()));
        followersCountTextView.setText(String.valueOf(prismUser.getFollowerCount()));
        followingCountTextView.setText(String.valueOf(prismUser.getFollowingCount()));

        followersLabelTextView.setText("followers");
        postsLabelTextView.setText("posts");
        followingLabelTextView.setText("following");

        followersRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismUserProfileActivity.this, prismUser.getUid(), DisplayUserType.FOLLOWER_USERS);
            }
        });

        followingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismUserProfileActivity.this, prismUser.getUid(),DisplayUserType.FOLLOWING_USERS);
            }
        });
    }

    /**
     *
     */
    private void setupPrismUserInterface() {
        setupUserInfo();
        setupUserStats();

        if (isCurrentUser) {
            setupEditAccountInformationButton();
            setupCurrentUserPostsViewPager();
        } else {
            setupFollowUserButton();
            setupUserUploadedPostsRecyclerView();
        }

        setupSwipeRefreshLayout();
    }

    /**
     *
     */
    private void refreshPrismUserInterface() {
        if (isCurrentUser) {
            CurrentUser.refreshUser(new OnFetchUserProfileCallback() {
                @Override
                public void onSuccess() {
                    prismUser = CurrentUser.getPrismUser();
                    setupUserInfo();
                    setupUserStats();

                    //TODO: Do not think the refresh like this will work properly
                    //TODO: May have to recreate the views on this activity instead
                    userPostsViewPagerAdapter.refreshViewPagerTabs();

                    profileSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Exception e) {
                    profileSwipeRefreshLayout.setRefreshing(false);
                    Helper.toast(PrismUserProfileActivity.this, Message.FETCH_USER_INFO_FAIL);
                }

            });
        } else {
            DatabaseRead.fetchPrismUser(prismUser.getUid(), new OnFetchPrismUserCallback() {
                @Override
                public void onSuccess(PrismUser user) {
                    prismUser = user;
                    setupUserInfo();
                    setupUserStats();

                    //TODO: Do not think the refresh like this will work properly
                    //TODO: May have to recreate the views on this activity instead
                    if (prismPostStaggeredGridRecyclerView != null) {
                        prismPostStaggeredGridRecyclerView.refreshStaggeredRecyclerViews();
                    }

                    profileSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onUserNotFound() {
                    profileSwipeRefreshLayout.setRefreshing(false);
                    Helper.toast(PrismUserProfileActivity.this, Message.USER_NOT_FOUND);
                }

                @Override
                public void onFailure(Exception e) {
                    profileSwipeRefreshLayout.setRefreshing(false);
                    Helper.toast(PrismUserProfileActivity.this, Message.FETCH_USER_INFO_FAIL);
                }
            });
        }
    }



    /* =====================================
     * CURRENT USER SETUP: Start
     * ===================================== */

    /**
     * If the editAccountInformation button is pressed, Intent to EditUserProfileActivity
     */
    private void setupEditAccountInformationButton() {
        accountEditInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToEditUserProfileActivity(PrismUserProfileActivity.this);
            }
        });

        accountEditInfoButton.setVisibility(View.VISIBLE);
    }

    /**
     * Setup the current user ViewPager with two tabs
     * Tab 1: Posts (user posts and reposts)
     * Tab 2: Likes (user likes)
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupCurrentUserPostsViewPager() {
        userPostsViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                profileSwipeRefreshLayout.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        profileSwipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        userPostsViewPager.setOffscreenPageLimit(2);
        userPostsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(userPostsTabLayout));
        userPostsViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), prismUserUploadedAndRepostedPostsArrayList, prismUserLikedPostsArrayList);
        userPostsViewPager.setAdapter(userPostsViewPagerAdapter);
        userPostsTabLayout.setupWithViewPager(userPostsViewPager);

        userPostsTabLayout.getTabAt(Default.USER_POSTS_VIEW_PAGER_POSTS).setCustomView(Helper.createTabTextView(this, "POSTS"));
        userPostsTabLayout.getTabAt(Default.USER_POSTS_VIEW_PAGER_LIKES).setCustomView(Helper.createTabTextView(this, "LIKES"));

        int selectedTabColor = getResources().getColor(R.color.colorAccent);
        int unselectedTabColor = Color.WHITE;
        ((TextView) userPostsTabLayout.getTabAt(userPostsTabLayout.getSelectedTabPosition()).getCustomView())
                .setTextColor(selectedTabColor);

        // Setup the tab selected, unselected, and reselected listener
        userPostsTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(selectedTabColor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(unselectedTabColor);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        userPostsTabLayout.setVisibility(View.VISIBLE);
        userPostsViewPager.setVisibility(View.VISIBLE);
    }

    /**
     * Create an AlertDialog for when the userProfilePicImageView is clicked
     * Gives the option to take a picture or select one from gallery
     */
    private AlertDialog createSetProfilePictureAlertDialog() {
        RecyclerView recyclerView = new RecyclerView(this);

        AlertDialog.Builder moreOptionAlertDialogBuilder = new AlertDialog.Builder(this, R.style.DarkThemAlertDialog);
        moreOptionAlertDialogBuilder.setView(recyclerView);
        AlertDialog moreOptionAlertDialog = moreOptionAlertDialogBuilder.create();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        OptionRecyclerViewAdapter moreOptionsRecyclerViewAdapter = new OptionRecyclerViewAdapter(this, ProfilePictureOption.values(), prismUser, userProfilePicImageView, moreOptionAlertDialog);
        recyclerView.setAdapter(moreOptionsRecyclerViewAdapter);

        return moreOptionAlertDialog;
    }

    /* =====================================
     * CURRENT USER SETUP: End
     * ===================================== */



    /* =====================================
     * NON CURRENT USER SETUP: Start
     * ===================================== */

    /**
     *  Setup follow buttons initial state and onClickListener
     *  Handle toggling the follow buttons
     */
    private void setupFollowUserButton() {
        boolean isFollowing = CurrentUser.isFollowingPrismUser(prismUser);
        InterfaceAction.toggleSmallFollowButton(this, isFollowing, toolbarFollowButton);
        InterfaceAction.toggleLargeFollowButton(this, isFollowing, followUserButton);

        followUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean performFollow = !CurrentUser.isFollowingPrismUser(prismUser);
                handleFollowButtonClick(performFollow);
            }
        });

        toolbarFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean performFollow = !CurrentUser.isFollowingPrismUser(prismUser);
                handleFollowButtonClick(performFollow);
            }
        });

        followUserButton.setVisibility(View.VISIBLE);
        toolbarFollowButton.setVisibility(View.VISIBLE);
    }

    /**
     * Setup the RecyclerView for all user uploaded and reposted posts
     */
    private void setupUserUploadedPostsRecyclerView() {
        LinearLayout userUploadedPostsLinearLayout = this.findViewById(R.id.user_uploaded_posts_linear_layout);
        if (prismUserUploadedAndRepostedPostsArrayList.size() > 0) {
            noUploadsOrRepostsLinearLayout.setVisibility(View.GONE);
            prismPostStaggeredGridRecyclerView = new PrismPostStaggeredGridRecyclerView(this,
                    userUploadedPostsLinearLayout, prismUserUploadedAndRepostedPostsArrayList);
        }

        userUploadedPostsLinearLayout.setVisibility(View.VISIBLE);
        profileNestedScrollView.setVisibility(View.VISIBLE);
    }

    /**
     * Handle the follow button when clicked to update firebase
     * @param performFollow
     */
    private void handleFollowButtonClick(boolean performFollow) {
        if (performFollow) {
            InterfaceAction.toggleSmallFollowButton(this, true, toolbarFollowButton);
            InterfaceAction.toggleLargeFollowButton(this, true, followUserButton);
            DatabaseAction.followUser(prismUser);
        } else {
            AlertDialog unfollowAlertDialog = InterfaceAction.createUnfollowConfirmationAlertDialog(this, prismUser, toolbarFollowButton, followUserButton);
            unfollowAlertDialog.show();
        }
    }

    /* =====================================
     * NON CURRENT USER SETUP: End
     * ===================================== */

}
