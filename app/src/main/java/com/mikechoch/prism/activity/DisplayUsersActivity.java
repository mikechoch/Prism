package com.mikechoch.prism.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.DisplayUsersRecyclerViewAdapter;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.fetch.OnFetchPrismUsersCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.DatabaseRead;

import java.util.ArrayList;


public class DisplayUsersActivity extends AppCompatActivity {

    private final static int LIKE_USERS = 0;
    private final static int REPOST_USERS = 1;
    private final static int FOLLOWER_USERS = 2;
    private final static int FOLLOWING_USERS = 3;

    private Toolbar toolbar;
    private TextView toolbarTextView;
    private ProgressBar likeRepostProgressBar;
    private RecyclerView usersRecyclerView;

    private DisplayUsersRecyclerViewAdapter displayUsersRecyclerViewAdapter;

    private Intent intent;
    private int activityCode;
    private String toolbarTitle;
    private ArrayList<PrismUser> prismUserArrayList;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity_layout);

        // Initialize all UI elements
        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);
        likeRepostProgressBar = findViewById(R.id.like_repost_progress_bar);
        usersRecyclerView = findViewById(R.id.like_repost_users_recycler_view);

        // Setup data structure to be populated with users who liked/reposted the post or following/followers the user
        prismUserArrayList = new ArrayList<>();

        // getIntent and grab the String to populate the Toolbar title
        // This will be "Likes" or "Reposts"
        // Default being "Error"
        intent = getIntent();
        activityCode = intent.getIntExtra(Default.USERS_INT_EXTRA, -1);

        setupUIElements();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Use Intent to getData passed from PrismPost
     * Decide if it was a likes or reposts click and set number of
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupPage() {
        String id = intent.getStringExtra(Default.USERS_DATA_ID_EXTRA);
        switch (activityCode) {
            case LIKE_USERS: {
                toolbarTitle = "Like";
                getLikedUsers(id);
                break;
            }
            case REPOST_USERS: {
                toolbarTitle = "Repost";
                getRepostedUsers(id);
                break;
            }
            case FOLLOWER_USERS: {
                toolbarTitle = "Follower";
                getFollowers(id);
                break;
            }
            case FOLLOWING_USERS: {
                toolbarTitle = "Following";
                getFollowings(id);
                break;
            }
            default: {
                break;
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Setup usersRecyclerView with a LinearLayoutManager, DefaultItemAnimator, and Adapter
     */
    private void setupLikeRepostRecyclerView() {
        // Setup the LinearLayoutManager and set it to the usersRecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);

        // Setup the DefaultItemAnimator and set it to the usersRecyclerView
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        usersRecyclerView.setItemAnimator(defaultItemAnimator);

        // Setup the DividerItemDecoration and set it to the usersRecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_view_divider));
        usersRecyclerView.addItemDecoration(dividerItemDecoration);

        // Setup the displayUsersRecyclerViewAdapter and set it to the usersRecyclerView
        displayUsersRecyclerViewAdapter = new DisplayUsersRecyclerViewAdapter(this, prismUserArrayList);
        usersRecyclerView.setAdapter(displayUsersRecyclerViewAdapter);
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupUIElements() {
        setupPage();

        // Setup Typefaces for all text based UI elements
        toolbarTextView.setTypeface(Default.sourceSansProLight);

        setupLikeRepostRecyclerView();
    }


    /**
     * Gets liked users for given postId
     * and then fetches user details for each userId
     */
    private void getLikedUsers(String postId) {
        DatabaseRead.fetchLikedUsers(postId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(ArrayList<PrismUser> prismUsers) {
                prismUserArrayList.addAll(prismUsers);
                performUIActivities();
            }

            @Override
            public void onPrismUsersNotFound() {
                Log.e(Default.TAG_DB, "No liked users found for post");
                performUIActivities();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
            }
        });

    }

    /**
     * Gets reposted users for given postId
     * and then fetches user details for each userId
     */
    private void getRepostedUsers(String postId) {
        DatabaseRead.fetchRepostedUsers(postId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(ArrayList<PrismUser> prismUsers) {
                prismUserArrayList.addAll(prismUsers);
                performUIActivities();
            }

            @Override
            public void onPrismUsersNotFound() {
                Log.e(Default.TAG_DB, "No reposted users found for post");
                performUIActivities();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
            }
        });
    }

    /**
     * Gets given user's followings
     * and then fetches user details for each userId
     */
    private void getFollowings(String userId) {
        DatabaseRead.fetchPrismUserFollowings(userId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(ArrayList<PrismUser> prismUsers) {
                prismUserArrayList.addAll(prismUsers);
                performUIActivities();
            }

            @Override
            public void onPrismUsersNotFound() {
                Log.e(Default.TAG_DB, "No followings found for this user");
                performUIActivities();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
            }
        });
    }

    /**
     * Gets give user's followers and then fetches user details for each userId
     */
    private void getFollowers(String userId) {
        DatabaseRead.fetchPrismUserFollowers(userId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(ArrayList<PrismUser> prismUsers) {
                prismUserArrayList.addAll(prismUsers);
                performUIActivities();
            }

            @Override
            public void onPrismUsersNotFound() {
                Log.e(Default.TAG_DB, "No followers found for this user");
                performUIActivities();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
            }
        });
    }

    /**
     * Hide the likeRepostProgressBar
     * Show the usersRecyclerView and notifyDataSetChanged on the adapter
     * Update the toolbarTitle and toolbarTextView (handle singular/ plural title)
     */
    private void performUIActivities() {
        usersRecyclerView.setVisibility(View.VISIBLE);
        likeRepostProgressBar.setVisibility(View.GONE);

        displayUsersRecyclerViewAdapter.notifyDataSetChanged();

        // Once data is populated set title to the number likes or reposts
        toolbarTitle = prismUserArrayList.size() + " " + toolbarTitle;
        // Make it plural
        if (!toolbarTitle.equals("Following") && prismUserArrayList.size() != 1) {
            toolbarTitle += "s";
        }
        toolbarTextView.setText(toolbarTitle);
    }

}
