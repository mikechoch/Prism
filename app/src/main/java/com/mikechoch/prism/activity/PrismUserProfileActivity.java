package com.mikechoch.prism.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.OptionRecyclerViewAdapter;
import com.mikechoch.prism.adapter.ProfileViewPagerAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostsCallback;
import com.mikechoch.prism.callback.fetch.OnFetchPrismUserCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.constant.NotificationKey;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fire.DatabaseRead;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.ProfilePictureOption;
import com.mikechoch.prism.user_interface.InterfaceAction;
import com.mikechoch.prism.user_interface.PrismPostStaggeredGridRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class PrismUserProfileActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private ImageView toolbarUserProfilePicImageView;
    private TextView toolbarUserUsernameTextView;
    private Button toolbarFollowButton;
    private ImageView accountEditInfoButton;
    private SwipeRefreshLayout profileSwipeRefreshLayout;
    private NestedScrollView profileNestedScrollView;
    private ImageView userProfilePicImageView;
    private Button followUserButton;
    private RelativeLayout followersRelativeLayout;
    private TextView followersCountTextView;
    private TextView followersLabelTextView;
    private TextView postsCountTextView;
    private TextView postsLabelTextView;
    private RelativeLayout followingRelativeLayout;
    private TextView followingCountTextView;
    private TextView followingLabelTextView;
    private TextView userUsernameTextView;
    private TextView userFullNameTextView;
    private TabLayout userPostsTabLayout;
    private ViewPager userPostsViewPager;

    private PrismUser prismUser;
    private Uri profilePictureUri;
    private boolean isCurrentUser;
    private ArrayList<PrismPost> prismUserUploadedAndRepostedPostsArrayList;

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

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        // Initialize all UI elements
        toolbarUserProfilePicImageView = findViewById(R.id.toolbar_user_profile_profile_picture_image_view);
        toolbarUserUsernameTextView = findViewById(R.id.toolbar_user_profile_username_text_view);
        toolbarFollowButton = findViewById(R.id.toolbar_follow_user_button);
        accountEditInfoButton = findViewById(R.id.toolbar_edit_account_information_image_view);
        profileSwipeRefreshLayout = findViewById(R.id.profile_swipe_refresh_layout);
        profileNestedScrollView = findViewById(R.id.profile_scroll_view);
        userProfilePicImageView = findViewById(R.id.user_profile_profile_picture_image_view);
        followUserButton = findViewById(R.id.follow_user_button);
        followersRelativeLayout = findViewById(R.id.followers_relative_layout);
        followersCountTextView = findViewById(R.id.followers_count_text_view);
        followersLabelTextView = findViewById(R.id.followers_label_text_view);
        postsCountTextView = findViewById(R.id.posts_count_text_view);
        postsLabelTextView = findViewById(R.id.posts_label_text_view);
        followingRelativeLayout = findViewById(R.id.following_relative_layout);
        followingCountTextView = findViewById(R.id.following_count_text_view);
        followingLabelTextView = findViewById(R.id.following_label_text_view);
        userUsernameTextView = findViewById(R.id.user_profile_username_text_view);
        userFullNameTextView = findViewById(R.id.user_profile_full_name_text_view);
        userPostsTabLayout = findViewById(R.id.current_user_profile_tab_layout);
        userPostsViewPager = findViewById(R.id.current_user_profile_view_pager);

        prismUserUploadedAndRepostedPostsArrayList = new ArrayList<>();

        // Get prismUser associated with this profile page from Intent
        Intent intent = getIntent();
        prismUser = intent.getParcelableExtra(Default.PRISM_USER_EXTRA);
        if (prismUser != null) {
            setupUIElements();
            fetchUserContent();
        } else {
            String prismUserId = intent.getExtras().getString(NotificationKey.PRISM_USER_ID);
            pullPrismUserDetails(prismUserId);
        }

    }

    private void pullPrismUserDetails(String prismUserId) {
        DatabaseRead.fetchPrismUser(prismUserId, new OnFetchPrismUserCallback() {
            @Override
            public void onSuccess(PrismUser user) {
                prismUser = user;
                setupUIElements();
                fetchUserContent();
            }

            @Override
            public void onUserNotFound() {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * TODO: @Parth Comment this
     */
    private void fetchUserContent() {
        if (isCurrentUser) {
            prismUserUploadedAndRepostedPostsArrayList.addAll(CurrentUser.getUserUploadsAndReposts());
            setupUserPostsUIElements();
            return;
        }


        DatabaseRead.fetchPrismUserUploadedPosts(prismUser, new OnFetchPrismPostsCallback() {
            @Override
            public void onSuccess(ArrayList<PrismPost> prismPosts) {
                prismUserUploadedAndRepostedPostsArrayList.addAll(prismPosts);
                areUploadedAndRepostedPostsFetched[0] = true;

                if (areUserRepostedPostsFetched()) {
                    sortPostsOnUserPage();
                    setupUserPostsUIElements();
                }
            }

            @Override
            public void onPrismPostsNotFound() {
                // User has not uploaded any posts
            }

            @Override
            public void onFailure(Exception e) {
                // TODO Log this
                e.printStackTrace();
            }
        });

        DatabaseRead.fetchPrismUserRepostedPosts(prismUser, new OnFetchPrismPostsCallback() {
            @Override
            public void onSuccess(ArrayList<PrismPost> prismPosts) {
                for (PrismPost post : prismPosts) {
                    post.setIsReposted(true);
                    prismUserUploadedAndRepostedPostsArrayList.add(post);
                }

                areUploadedAndRepostedPostsFetched[1] = true;
                if (areUserUploadedPostsFetched()) {
                    sortPostsOnUserPage();
                    setupUserPostsUIElements();
                }
            }

            @Override
            public void onPrismPostsNotFound() {
                // User has not reposted any posts
            }

            @Override
            public void onFailure(Exception e) {
                // TODO Log this
                e.printStackTrace();
            }
        });
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Setup the userProfilePicImageView so it is populated with a Default or custom picture
     * When clicked it will show an AlertDialog of options for changing the picture
     */
    private void setupUserProfileUIElements() {
        userFullNameTextView.setText(prismUser.getFullName());
        userUsernameTextView.setText(prismUser.getUsername());

        setupUserProfilePicture();

        postsCountTextView.setText(String.valueOf(prismUser.getUploadCount()));

        followersCountTextView.setText(String.valueOf(prismUser.getFollowerCount()));
        followersRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismUserProfileActivity.this, prismUser.getUid(), Default.DISPLAY_USERS_FOLLOWER_CODE);
            }
        });

        followingCountTextView.setText(String.valueOf(prismUser.getFollowingCount()));
        followingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToDisplayUsersActivity(PrismUserProfileActivity.this, prismUser.getUid(),Default.DISPLAY_USERS_FOLLOWING_CODE);
            }
        });
    }

    /**
     * Setup all user profile picture based UI elements
     * Add onClickListener handling for current user/ other user
     * Current User: Show AlertDialog of options
     * Other User: Show hi-res version of profile picture
     */
    private void setupUserProfilePicture() {
        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(prismUser.getProfilePicture().hiResUri)
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(userProfilePicImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (2 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        PrismUserProfileActivity.this,
                                        userProfilePicImageView,
                                        prismUser.getProfilePicture().isDefault,
                                        resource,
                                        imageViewPadding);
                        userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                    }
                });

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

        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(prismUser.getProfilePicture().lowResUri)
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(toolbarUserProfilePicImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int imageViewPadding = (int) (1 * Default.scale);
                        RoundedBitmapDrawable profilePictureDrawable =
                                BitmapHelper.createCircularProfilePicture(
                                        PrismUserProfileActivity.this,
                                        toolbarUserProfilePicImageView,
                                        prismUser.getProfilePicture().isDefault,
                                        resource,
                                        imageViewPadding);
                        toolbarUserProfilePicImageView.setImageDrawable(profilePictureDrawable);
                    }
                });

        toolbarUserUsernameTextView.setText(prismUser.getUsername());
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

    /**
     * Toggle on the visibility of current user based UI elements
     * Call methods for updating data
     */
    private void setupCurrentUserProfilePage() {
        accountEditInfoButton.setVisibility(View.VISIBLE);
        setupEditAccountInformationButton();

        userPostsTabLayout.setVisibility(View.VISIBLE);
        userPostsViewPager.setVisibility(View.VISIBLE);
        setupCurrentUserPostsViewPager();
    }

    /**
     * Toggle on the visibility of other user based UI elements
     * Call methods for updating data
     */
    private void setupOtherUserProfilePage() {
        followUserButton.setVisibility(View.VISIBLE);
        setupFollowUserButton();

        profileNestedScrollView.setVisibility(View.VISIBLE);

        toolbarFollowButton.setVisibility(View.VISIBLE);
        setupUserUploadedPostsRecyclerView();
    }

    /**
     * Setup the current user ViewPager with two tabs
     * Tab 1: Posts (user posts and reposts)
     * Tab 2: Likes (user likes)
     */
    private void setupCurrentUserPostsViewPager() {
        userPostsViewPager.setOffscreenPageLimit(2);
        userPostsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(userPostsTabLayout));
        ProfileViewPagerAdapter userPostsViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
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
                // Switch statement handing reselected tabs
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        break;

                    case 1:
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Setup the RecyclerView for all user uploaded and reposted posts
     */
    private void setupUserUploadedPostsRecyclerView() {
        profileSwipeRefreshLayout.setVisibility(View.VISIBLE);
        profileSwipeRefreshLayout.setColorSchemeResources(InterfaceAction.swipeRefreshLayoutColors);
        profileSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileSwipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayout userUploadedPostsLinearLayout = this.findViewById(R.id.user_uploaded_posts_linear_layout);
        new PrismPostStaggeredGridRecyclerView(this, userUploadedPostsLinearLayout, prismUserUploadedAndRepostedPostsArrayList);
        userUploadedPostsLinearLayout.setVisibility(View.VISIBLE);
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
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                float toolbarElementsAlpha = Math.abs(verticalOffset/ ((float) scrollRange));
                toolbarUserProfilePicImageView.setAlpha(toolbarElementsAlpha);
                toolbarUserUsernameTextView.setAlpha(toolbarElementsAlpha);
                toolbarFollowButton.setAlpha(toolbarElementsAlpha);

                // Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
//                    toolbarUserProfilePicImageView.setVisibility(View.VISIBLE);
//                    toolbarUserUsernameTextView.setVisibility(View.VISIBLE);
                } else {
//                    toolbarUserProfilePicImageView.setVisibility(View.GONE);
//                    toolbarUserUsernameTextView.setVisibility(View.GONE);
                }
            }
        });
    }

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
    }

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

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        isCurrentUser = Helper.isPrismUserCurrentUser(prismUser);

        setupToolbar();
        // Setup Typefaces for all text based UI elements
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

        setupAppBarLayout();
        setupUserProfileUIElements();
    }

    private void sortPostsOnUserPage() {
        Collections.sort(prismUserUploadedAndRepostedPostsArrayList, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return Long.compare(p1.getTimestamp(), p2.getTimestamp());
            }
        });
    }

    private boolean areUserUploadedPostsFetched() {
        return areUploadedAndRepostedPostsFetched[0];
    }

    private boolean areUserRepostedPostsFetched() {
        return areUploadedAndRepostedPostsFetched[0];
    }

    /**
     * Current user setup is different then other user UI setup
     */
    private void setupUserPostsUIElements() {
        if (isCurrentUser) {
            setupCurrentUserProfilePage();
        } else {
            setupOtherUserProfilePage();
        }
    }
}
