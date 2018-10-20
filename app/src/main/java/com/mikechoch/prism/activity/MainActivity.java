package com.mikechoch.prism.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.util.NetworkStateReceiver;
import com.mikechoch.prism.adapter.MainViewPagerAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;
import com.mikechoch.prism.fragment.MainFeedFragment;
import com.mikechoch.prism.fragment.NotificationFragment;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.MainViewPagerTab;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends FragmentActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private Animation hideFabAnimation;
    private Animation showFabAnimation;

    private AppBarLayout.LayoutParams params;

    private Toolbar toolbar;
    private TextView toolbarTextView;

    private CoordinatorLayout mainCoordinateLayout;
    private TabLayout prismTabLayout;
    private ViewPager prismViewPager;
    private ImageView imageUploadPreview;
    private TextView uploadingImageTextView;
    private RelativeLayout prismDecorationRelativeLayout;
    private RelativeLayout uploadingImageRelativeLayout;
    private FloatingActionButton uploadImageFab;
    private ProgressBar imageUploadProgressBar;
    private Snackbar networkSnackBar;
    private NetworkStateReceiver networkStateReceiver;

    private Uri uploadedImageUri;
    private String uploadedImageDescription;

    private Handler clearNotificationsHandler;
    private Runnable clearNotificationsRunnable;
    private boolean shouldClearNotifications = false;

    public static String FCM_API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        AndroidNetworking.initialize(this);

        FCM_API_KEY = getFirebaseKey();

        // This is a safety check to make sure no user is logged in without an account
        if (CurrentUser.getFirebaseUser() == null) {
            IntentHelper.intentToLoginActivity(MainActivity.this);
            return;
        }

        // Create uploadImageFab showing and hiding animations
        showFabAnimation = createFabShowAnimation(false);
        hideFabAnimation = createFabShowAnimation(true);

        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.prism_toolbar_title);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        mainCoordinateLayout = findViewById(R.id.main_coordinate_layout);
        prismTabLayout = findViewById(R.id.prism_tab_layout);
        prismViewPager = findViewById(R.id.prism_view_pager);
        imageUploadPreview = findViewById(R.id.image_upload_preview);
        uploadingImageTextView = findViewById(R.id.uploading_image_text_view);
        prismDecorationRelativeLayout = findViewById(R.id.prism_toolbar_decoration);
        uploadingImageRelativeLayout = findViewById(R.id.uploading_image_relative_layout);
        uploadImageFab = findViewById(R.id.upload_image_fab);
        imageUploadProgressBar = findViewById(R.id.image_upload_progress_bar);

        initializeNetworkListener();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * onDestroy is a method that gets invoked when OS tries to kill the activity
     * This is the last chance for app to finalize closing activities before app
     * gets shut down
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    /**
     * When a permission is allowed, this function will run and you can
     * Check for this allow and do something
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Default.MY_PERMISSIONS_WRITE_MEDIA_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Code here for allowing write permission
                }
                break;
            case Default.MY_PERMISSIONS_CAMERA_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Code here for allowing write permission
                }
                break;
            default:
                break;
        }
    }

    /**
     * Check the incoming Intent for an upload image intent key
     * If the Boolean Extra is true then an upload will be initialized
     */
    private void checkForImageUploadIntent() {
        Intent uploadIntent = getIntent();
        if (uploadIntent.getBooleanExtra(Default.UPLOAD_IMAGE_INTENT_KEY, false)) {
            setupUploadPrismPostToFirebase(uploadIntent);
        }
    }

    /**
     * Method to begin uploading a PrismPost into Firebase
     * Setup upload image ProgressBar and preview ImageView
     * Than begin process of uploading to Firebase
     * @param uploadIntent - Intent to obtain the PrismPost info to upload with
     */
    private void setupUploadPrismPostToFirebase(Intent uploadIntent) {
        String uploadingImageString = getResources().getString(R.string.uploading_image);
        uploadingImageTextView.setText(uploadingImageString);
        imageUploadProgressBar.setProgress(0);
        imageUploadProgressBar.setIndeterminate(false);
        uploadingImageRelativeLayout.setVisibility(View.VISIBLE);
        prismDecorationRelativeLayout.setVisibility(View.GONE);

        params.setScrollFlags(0);
        toolbar.setLayoutParams(params);

        uploadedImageUri = Uri.parse(uploadIntent.getStringExtra(Default.IMAGE_URI_EXTRA));
        uploadedImageDescription = uploadIntent.getStringExtra(Default.IMAGE_DESCRIPTION_EXTRA);

        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(uploadedImageUri)
                .apply(new RequestOptions().centerCrop())
                .into(new BitmapImageViewTarget(imageUploadPreview) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        drawable.setCircular(true);
                        imageUploadPreview.setImageDrawable(drawable);
                    }
                });
        beginUploadPrismPostToFirebase();
    }

    /**
     * Give PageChangeListener control to TabLayout
     * Create MainViewPagerAdapter and set it for the ViewPager
     */
    private void setupPrismViewPager() {
        prismViewPager.setOffscreenPageLimit(Default.MAIN_VIEW_PAGER_SIZE);
        prismViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(prismTabLayout));
        MainViewPagerAdapter prismViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        prismViewPager.setAdapter(prismViewPagerAdapter);
        prismTabLayout.setupWithViewPager(prismViewPager);
    }

    /**
     * Setup for the TabLayout
     * Give each tab an icon and set the listener for selecting, reselecting, and unselecting
     * Selected tabs will be a ColorAccent and unselected tabs White
     */
    private void setupPrismTabLayout() {
        // Create the selected and unselected tab icon colors
        int tabUnselectedColor = Color.WHITE;
        int tabSelectedColor = getResources().getColor(R.color.colorAccent);

        // Setup all TabLayout tab icons
        for (MainViewPagerTab tab : MainViewPagerTab.values()) {
            int tabId = tab.getId();
            TabLayout.Tab tabLayoutTab = prismTabLayout.getTabAt(tabId);

            // Make first tab selected color and all others unselected
            if (tabLayoutTab != null) {
                Drawable tabIcon = getResources().getDrawable(tab.getIcon());
                switch (tabId) {
                    case Default.MAIN_VIEW_PAGER_MAIN_FEED:
                        tabIcon.setColorFilter(tabSelectedColor, PorterDuff.Mode.SRC_IN);
                        break;
                    default:
                        tabIcon.setColorFilter(tabUnselectedColor, PorterDuff.Mode.SRC_IN);
                        break;
                }
                tabLayoutTab.setIcon(tabIcon.mutate());
            }
        }

        // Setup the tab selected, unselected, and reselected listener
        prismTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                params.setScrollFlags(isUploadingImage ?
//                        0 : AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
//                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
//                        AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                // Sets the selected tab to the selected color and
                // If at the HOME tab the uploadImageFab will be shown
                // Otherwise, the uploadImageFab will be hidden
                tab.getIcon().setColorFilter(tabSelectedColor, PorterDuff.Mode.SRC_IN);
                prismViewPager.setCurrentItem(tab.getPosition(), true);
                if (tab.getPosition() <= Default.MAIN_VIEW_PAGER_MAIN_FEED && !uploadImageFab.isShown()) {
//                    toolbar.setLayoutParams(params);
                    uploadImageFab.startAnimation(showFabAnimation);
                } else if (tab.getPosition() > Default.MAIN_VIEW_PAGER_MAIN_FEED && uploadImageFab.isShown()) {
//                    params.setScrollFlags(0);
//                    toolbar.setLayoutParams(params);
                    uploadImageFab.startAnimation(hideFabAnimation);
                }

                // Switch statement handing reselected tabs
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    // HOME tab will...
                    case Default.MAIN_VIEW_PAGER_MAIN_FEED:
                        break;

                    // SEARCH tab will...
                    case Default.MAIN_VIEW_PAGER_SEARCH:
                        break;

                    // NOTIFICATIONS tab will...
                    case Default.MAIN_VIEW_PAGER_NOTIFICATIONS:
                        clearNotificationsHandler = new Handler();
                        clearNotificationsRunnable = new Runnable() {
                            @Override
                            public void run() {
                                shouldClearNotifications = true;
                            }
                        };
                        clearNotificationsHandler.postDelayed(clearNotificationsRunnable, 2000);
                        break;

                    // PROFILE tab will...
                    case Default.MAIN_VIEW_PAGER_PROFILE:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Set the tab unselected to the unselected color
                tab.getIcon().setColorFilter(tabUnselectedColor, PorterDuff.Mode.SRC_IN);

                // Switch statement handing reselected tabs
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    // HOME tab will...
                    case Default.MAIN_VIEW_PAGER_MAIN_FEED:
                        break;

                    // SEARCH tab will...
                    case Default.MAIN_VIEW_PAGER_SEARCH:
                        break;

                    // NOTIFICATIONS tab will set all notifications isViewed to true
                    case Default.MAIN_VIEW_PAGER_NOTIFICATIONS:
                        NotificationFragment.clearAllNotifications();
                        clearNotificationsHandler.removeCallbacks(clearNotificationsRunnable);
                        if (shouldClearNotifications) {
                            NotificationFragment.clearAllNotifications();
                            DatabaseAction.updateViewedTimestampForAllNotifications();
                        }
                        shouldClearNotifications = false;
                        break;

                    // PROFILE tab will...
                    case Default.MAIN_VIEW_PAGER_PROFILE:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Switch statement handing reselected tabs
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    // HOME tab will bring the user back to the top of the mainContentRecyclerView
                    case Default.MAIN_VIEW_PAGER_MAIN_FEED:
                        RecyclerView mainContentRecyclerView = MainActivity.this
                                .findViewById(R.id.main_content_recycler_view);
                        if (mainContentRecyclerView != null) {
                            LinearLayoutManager layoutManager  = (LinearLayoutManager)
                                    mainContentRecyclerView.getLayoutManager();
                            if (layoutManager.findFirstVisibleItemPosition() < 10) {
                                mainContentRecyclerView.smoothScrollToPosition(0);
                            } else {
                                mainContentRecyclerView.scrollToPosition(0);
                            }
                        }
                        break;

                    // SEARCH tab will...
                    case Default.MAIN_VIEW_PAGER_SEARCH:
                        break;

                    // NOTIFICATIONS tab will...
                    case Default.MAIN_VIEW_PAGER_NOTIFICATIONS:
                        break;

                    // PROFILE tab will...
                    case Default.MAIN_VIEW_PAGER_PROFILE:
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * Setup the UploadImageFab, so when it is clicked it will Intent to UploadImageActivity
     */
    private void setupUploadImageFab() {
        uploadImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentToUploadImageSelectionActivity(MainActivity.this);
            }
        });
    }

    /**
     * Setup elements for current activity
     */
    private void setupInterfaceElements() {
        toolbarTextView.setTypeface(Default.sourceSansProBold);
        uploadingImageTextView.setTypeface(Default.sourceSansProLight);

        setupPrismViewPager();
        setupPrismTabLayout();
        setupUploadImageFab();

        TabLayout.Tab currentTab = prismTabLayout.getTabAt(prismViewPager.getCurrentItem());
        if (currentTab != null) {
            currentTab.select();
        }

        new InterfaceAction(this);

        checkForImageUploadIntent();
    }

    /**
     * Takes in a boolean shouldHide and will create a hiding and showing animation
     */
    private Animation createFabShowAnimation(boolean shouldHide) {
        float scaleFromXY = shouldHide ? 1f : 0f;
        float scaleToXY = shouldHide ? 0f : 1f;
        float pivotXY = 0.5f;
        Animation scaleAnimation  = new ScaleAnimation(scaleFromXY, scaleToXY, scaleFromXY, scaleToXY,
                Animation.RELATIVE_TO_SELF, pivotXY,
                Animation.RELATIVE_TO_SELF, pivotXY);
        scaleAnimation.setDuration(200);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                uploadImageFab.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        return scaleAnimation;
    }

    /**
     *  Takes the uploadedImageUri (which is the image that firebaseUser chooses from local storage)
     *  and uploads the file to cloud. Once that is successful, the a new post is created in
     *  ALL_POSTS section and the post details are pushed. Then the postId is added to the
     *  USER_UPLOADS section for the current firebaseUser
     *  TODO: Handle case when post upload fails -- this is very important
     *  TODO Refactor all of this
     */
    @SuppressLint("SimpleDateFormat")
    private void beginUploadPrismPostToFirebase() {
        StorageReference postImageRef = Default.STORAGE_REFERENCE.child(Key.STORAGE_POST_IMAGES_REF).child(uploadedImageUri.getLastPathSegment());
        postImageRef.putFile(uploadedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult().getDownloadUrl();
                    DatabaseReference postReference = Default.ALL_POSTS_REFERENCE.push();
                    String postId = postReference.getKey();
                    PrismPost prismPost = createPrismPostObjectForUpload(downloadUrl);

                    /* Create the post in cloud and onSuccess, add the image to local recycler view adapter */
                    postReference.setValue(prismPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                /* [1] Add postId to USER_UPLOADS table */
                                DatabaseReference currentUserReference = Default.USERS_REFERENCE.child(CurrentUser.getFirebaseUser().getUid());
                                DatabaseReference userPostRef = currentUserReference.child(Key.DB_REF_USER_UPLOADS).child(postReference.getKey());
                                userPostRef.setValue(prismPost.getTimestamp());

                                /* [2] Parse the hashTags from post caption and add the postId in TAGS table */
                                ArrayList<String> hashTags = Helper.parseDescriptionForTags(prismPost.getCaption());
                                for (String hashTag : hashTags) {
                                    DatabaseReference tagsReference = Default.TAGS_REFERENCE.child(hashTag).child(postId);
                                    tagsReference.setValue(prismPost.getTimestamp());
                                }

                                /* [3] Update each follower's news feed */
                                DatabaseReference usersReference = Default.USERS_REFERENCE;
                                ArrayList<String> followers = CurrentUser.getFollowers();
                                for (String userId : followers) {
                                    usersReference
                                            .child(userId)
                                            .child(Key.DB_REF_USER_NEWS_FEED)
                                            .child(postId)
                                            .setValue(prismPost.getTimestamp());
                                }

                                /* [4] Update local adapter */
                                prismPost.setPrismUser(CurrentUser.prismUser);
                                prismPost.setPostId(postId);
                                updateLocalRecyclerViewWithNewPost(prismPost);
                            } else {
                                uploadingImageTextView.setText("Failed to upload image");
                                Log.wtf(Default.TAG_DB, Message.POST_UPLOAD_FAIL, task.getException());
                            }
                            boolean animate = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
                            imageUploadProgressBar.setProgress(100, animate);
                        }
                    });


                } else {
                    Log.e(Default.TAG_DB, Message.FILE_UPLOAD_FAIL, task.getException());
                    Helper.toast(MainActivity.this, "Failed to upload image");
                }


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                        boolean animate = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
                        imageUploadProgressBar.setProgress(progress, animate);
                    }
                });
            }
        });
    }

    /**
     * Takes new prismPost object that got uploaded to cloud and adds it to the recyclerViewAdapter
     * and wraps up other UI elements such as textviews and progress spinners
     * @param prismPost
     */
    private void updateLocalRecyclerViewWithNewPost(PrismPost prismPost) {
        String finishingUploadingImageString = getResources().getString(R.string.finishing_up_uploading_image);
        uploadingImageTextView.setText(finishingUploadingImageString);
        prismPost.setPrismUser(CurrentUser.prismUser);
        RecyclerView mainContentRecyclerView = MainActivity.this.findViewById(R.id.main_content_recycler_view);
        LinearLayoutManager layoutManager  = (LinearLayoutManager) mainContentRecyclerView.getLayoutManager();
        RelativeLayout noMainPostsRelativeLayout = MainActivity.this.findViewById(R.id.no_main_posts_relative_layout);
        MainFeedFragment.mainFeedPrismPostArrayList.add(0, prismPost);
        mainContentRecyclerView.getAdapter().notifyItemInserted(0);
        noMainPostsRelativeLayout.setVisibility(View.GONE);

        if (layoutManager.findFirstVisibleItemPosition() < 10) {
            mainContentRecyclerView.smoothScrollToPosition(0);
        } else {
            mainContentRecyclerView.scrollToPosition(0);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadingImageTextView.setText("Done");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadingImageRelativeLayout.setVisibility(View.GONE);
                prismDecorationRelativeLayout.setVisibility(View.VISIBLE);

                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                toolbar.setLayoutParams(params);
            }
        }, 2000);
    }

    /**
     * Takes in the downloadUri that was create in cloud and reference to the post that
     * got created in cloud and prepares the PrismPost object that will be pushed
     */
    private PrismPost createPrismPostObjectForUpload(Uri downloadUrl) {
        String imageUri = downloadUrl.toString();
        String description = uploadedImageDescription;
        String userId = CurrentUser.getFirebaseUser().getUid();
        Long timestamp = -1 * Calendar.getInstance().getTimeInMillis();
        return new PrismPost(imageUri, description, userId, timestamp);
    }

    /**
     * Obtain the firebase cloud messaging server key from strings.xml
     * @return - String firebase cloud messaging server key for accessing Firebase
     */
    private String getFirebaseKey() {
        return getString(R.string.firebase_cloud_messaging_server_key);
    }

    /**
     * Initializes listeners for network (wifi or data) and location
     * Gets called first thing when app opens up
     */
    private void initializeNetworkListener() {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onNetworkConnected() {
//        CurrentUser.refreshUserProfile(this);
        if (networkSnackBar != null && networkSnackBar.isShownOrQueued()) {
            networkSnackBar.dismiss();
            networkSnackBar = null;
        } else {
            setupInterfaceElements();
        }
    }

    @Override
    public void onNetworkDisconnected() {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.main_coordinate_layout);
        networkSnackBar = Snackbar.make(coordinatorLayout, Message.NO_INTERNET,
                Snackbar.LENGTH_INDEFINITE);
        ((TextView) (networkSnackBar.getView())
                .findViewById(android.support.design.R.id.snackbar_text))
                .setTypeface(Default.sourceSansProBold);
        networkSnackBar.show();
        Helper.disableSnackbarSwipeDismiss(networkSnackBar.getView());
    }
}
