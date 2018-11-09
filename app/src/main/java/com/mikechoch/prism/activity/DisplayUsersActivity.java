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
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.DisplayUserType;

import java.util.ArrayList;
import java.util.HashMap;


public class DisplayUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTextView;
    private ProgressBar likeRepostProgressBar;
    private RecyclerView usersRecyclerView;

    private DisplayUsersRecyclerViewAdapter displayUsersRecyclerViewAdapter;

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
                onBackPressed();
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

        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);
        likeRepostProgressBar = findViewById(R.id.like_repost_progress_bar);
        usersRecyclerView = findViewById(R.id.like_repost_users_recycler_view);

        // Setup data structure to be populated with users who liked/reposted the post or following/followers the user
        prismUserArrayList = new ArrayList<>();

        // getIntent and grab the String to populate the Toolbar title
        // This will be "Likes" or "Reposts"
        // Default being "Error"

        setupInterfaceElements();
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
    private void setupDisplayUsersPageType() {
        String id = getIntent().getStringExtra(Default.DISPLAY_USERS_ITEM_ID);
        DisplayUserType displayUserType = (DisplayUserType) getIntent().getSerializableExtra(Default.DISPLAY_USERS_TYPE);
        toolbarTitle = displayUserType.getToolbarTitle();

        switch (displayUserType) {
            case LIKED_USERS: {
                getLikedUsers(id);
                break;
            }
            case REPOSTED_USERS: {
                getRepostedUsers(id);
                break;
            }
            case FOLLOWER_USERS: {
                getFollowers(id);
                break;
            }
            case FOLLOWING_USERS: {
                getFollowings(id);
                break;
            }
            default: {
                break;
            }
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Setup the display users RecyclerView for like, repost, following, or followers
     */
    private void setupDisplayUsersRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.recycler_view_divider));
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setItemAnimator(defaultItemAnimator);
        usersRecyclerView.addItemDecoration(dividerItemDecoration);

        displayUsersRecyclerViewAdapter = new DisplayUsersRecyclerViewAdapter(this, prismUserArrayList);
        usersRecyclerView.setAdapter(displayUsersRecyclerViewAdapter);
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupInterfaceElements() {
        toolbarTextView.setTypeface(Default.sourceSansProLight);

        setupDisplayUsersPageType();
        setupDisplayUsersRecyclerView();
    }

    /**
     * Gets liked users for given postId and then fetches user details for each userId
     */
    private void getLikedUsers(String postId) {
        DatabaseRead.fetchLikedUsers(postId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(HashMap<String, PrismUser> prismUsersMap) {
                prismUserArrayList.addAll(prismUsersMap.values());
                updateInterfaceElements();
            }

            @Override
            public void onPrismUsersNotFound() {
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
                updateInterfaceElements();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
            }
        });

    }

    /**
     * Gets reposted users for given postId and then fetches user details for each userId
     */
    private void getRepostedUsers(String postId) {
        DatabaseRead.fetchRepostedUsers(postId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(HashMap<String, PrismUser> prismUsersMap) {
                prismUserArrayList.addAll(prismUsersMap.values());
                updateInterfaceElements();
            }

            @Override
            public void onPrismUsersNotFound() {
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
                updateInterfaceElements();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
            }
        });
    }

    /**
     * Gets given user's followings and then fetches user details for each userId
     */
    private void getFollowings(String userId) {
        DatabaseRead.fetchPrismUserFollowings(userId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(HashMap<String, PrismUser> prismUsersMap) {
                prismUserArrayList.addAll(prismUsersMap.values());
                updateInterfaceElements();
            }

            @Override
            public void onPrismUsersNotFound() {
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
                updateInterfaceElements();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
            }
        });
    }

    /**
     * Gets give user's followers and then fetches user details for each userId
     */
    private void getFollowers(String userId) {
        DatabaseRead.fetchPrismUserFollowers(userId, new OnFetchPrismUsersCallback() {
            @Override
            public void onSuccess(HashMap<String, PrismUser> prismUsersMap) {
                prismUserArrayList.addAll(prismUsersMap.values());
                updateInterfaceElements();
            }

            @Override
            public void onPrismUsersNotFound() {
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
                updateInterfaceElements();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(Default.TAG_DB, Message.FETCH_USERS_FAIL, e);
                Helper.toast(DisplayUsersActivity.this, Message.FETCH_USERS_FAIL);
            }
        });
    }

    /**
     * Hide the likeRepostProgressBar
     * Show the usersRecyclerView and notifyDataSetChanged on the adapter
     * Update the toolbarTitle and toolbarTextView (handle singular/ plural title)
     */
    private void updateInterfaceElements() {
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
