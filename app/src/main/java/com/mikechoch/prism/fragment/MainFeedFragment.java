package com.mikechoch.prism.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.PrismPostRecyclerViewAdapter;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;


public class MainFeedFragment extends Fragment {

    private DatabaseReference databaseReferenceAllPosts;
    private DatabaseReference usersReference;

    private ProgressBar mainFeedProgressBar;
    private RelativeLayout noMainFeedRelativeLayout;
    private TextView noMainFeedTextView;
    private SwipeRefreshLayout mainFeedSwipeRefreshLayout;
    private PrismPostRecyclerViewAdapter mainFeedRecyclerViewAdapter;
    private RecyclerView mainFeedRecyclerView;

    public static ArrayList<PrismPost> mainFeedPrismPostArrayList;
    private boolean isLoading = false;


    public static MainFeedFragment newInstance() {
        return new MainFeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_feed_fragment_layout, container, false);

        mainFeedProgressBar = view.findViewById(R.id.main_feed_fragment_progress_bar);
        mainFeedSwipeRefreshLayout = view.findViewById(R.id.main_content_swipe_refresh_layout);
        mainFeedRecyclerView = view.findViewById(R.id.main_content_recycler_view);
        noMainFeedRelativeLayout = view.findViewById(R.id.no_main_posts_relative_layout);
        noMainFeedTextView = view.findViewById(R.id.no_main_posts_text_view);
        noMainFeedTextView.setTypeface(Default.sourceSansProLight);

        databaseReferenceAllPosts = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;

        mainFeedPrismPostArrayList = new ArrayList<>();

        setupInterfaceElements();

        return view;
    }

    /**
     * SwipeRefreshLayout OnRefreshListener handles fetching new data from the cloud database
     * Checks that isLoading is false and the totalItemCount is > then the image threshold
     * Then will call refreshData
     * Otherwise stop refreshing
     */
    private void setupMainFeedRefreshSwipeLayout() {
        mainFeedSwipeRefreshLayout.setColorSchemeResources(InterfaceAction.swipeRefreshLayoutColors);
        mainFeedSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading || !(mainFeedRecyclerViewAdapter.getItemCount() < Default.IMAGE_LOAD_THRESHOLD)) {
                    CurrentUser.refreshUserProfile(getContext());
                    refreshData();
                } else {
                    mainFeedSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * The main purpose of this MainFeedFragment is to be a Home page of the application
     * The RecyclerView being created below will show all of the most recent posts
     * The posts shown will be of people the firebaseUser follows
     */
    private void setupMainFeedRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.recycler_view_divider));
        mainFeedRecyclerView.setLayoutManager(linearLayoutManager);
        mainFeedRecyclerView.setItemAnimator(defaultItemAnimator);
        mainFeedRecyclerView.addItemDecoration(dividerItemDecoration);
        mainFeedRecyclerView.setItemViewCacheSize(20);

        /*
         * The OnScrollListener is handling the toggling of the isLoading boolean
         * Bottom of the RecyclerView will set isLoading to true and fetchMorePosts() will be called
         * Otherwise a threshold is set to call fetchMorePosts() again and isLoading will become false
         * As new data is pulled this threshold is met
         * This avoids conflicts with swipe refreshing while pulling old data
         */
        mainFeedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && (totalItemCount - Default.IMAGE_LOAD_THRESHOLD == lastVisibleItem)) {
                    isLoading = true;
                    fetchMorePosts();
                } else if (totalItemCount > lastVisibleItem + Default.IMAGE_LOAD_THRESHOLD) {
                    isLoading = false;
                }
            }
        });

        mainFeedRecyclerViewAdapter = new PrismPostRecyclerViewAdapter(getContext(), mainFeedPrismPostArrayList);
        mainFeedRecyclerView.setAdapter(mainFeedRecyclerViewAdapter);

        refreshData();
    }

    /**
     * Setup elements of current fragment
     */
    private void setupInterfaceElements() {
        setupMainFeedRecyclerView();
        setupMainFeedRefreshSwipeLayout();
    }

    /**
     *  Clears the data structure and pulls ALL_POSTS info again from cloud
     *  Queries the ALL_POSTS data sorted by the post timestamp and pulls n
     *  number of posts and loads them into an ArrayList of postIds and
     *  a HashMap of PrismObjects
     */
    private void refreshData() {
        // TODO uncomment this and put this in News Feed section
        // mainFeedPrismPostArrayList.clear();
        // mainFeedPrismPostArrayList.addAll(CurrentUser.news_feed);
        Query query = databaseReferenceAllPosts.orderByChild(Key.POST_TIMESTAMP).limitToFirst(Default.IMAGE_LOAD_COUNT);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Notify that all RecyclerView data will be cleared and then clear all data structures
                 * Iterate through the DataSnapshot and add all new data to the data structures
                 * Notify RecyclerView after items are added to data structures
                 */
                mainFeedPrismPostArrayList.clear();
                mainFeedRecyclerViewAdapter.notifyDataSetChanged();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                        mainFeedPrismPostArrayList.add(prismPost);
                    }
                    noMainFeedRelativeLayout.setVisibility(View.GONE);
                    populateUserDetailsForAllPosts(true);
                } else {
                    Log.i(Default.TAG_DB, Message.NO_DATA);
                    noMainFeedRelativeLayout.setVisibility(View.VISIBLE);
                    mainFeedSwipeRefreshLayout.setRefreshing(false);
                    mainFeedProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    /**
     *  Pulls more data (for ALL_POSTS) from cloud, typically when firebaseUser is about to
     *  reach the end of the list. It first gets the timestamp of the last post in
     *  the list and then queries more images starting from that last timestamp and
     *  appends them back to the end of the arrayList and the HashMap
     */
    private void fetchMorePosts() {
        long lastPostTimestamp = mainFeedPrismPostArrayList.get(mainFeedPrismPostArrayList.size() - 1).getTimestamp();
        //toast("Fetching more pics");
        databaseReferenceAllPosts
                .orderByChild(Key.POST_TIMESTAMP)
                .startAt(lastPostTimestamp + 1)
                .limitToFirst(Default.IMAGE_LOAD_COUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                                mainFeedPrismPostArrayList.add(prismPost);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mainFeedPrismPostArrayList.size() > 0) {
                                            mainFeedRecyclerViewAdapter
                                                    .notifyItemInserted(mainFeedPrismPostArrayList.size());
                                        }
                                    }
                                });

                            }
                            populateUserDetailsForAllPosts(false);
                        } else {
                            Log.i(Default.TAG_DB, Message.NO_DATA);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(Default.TAG_DB, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }

    /**
     * Once all posts are loaded into the prismPostHashMap,
     * this method iterates over each post, grabs firebaseUser's details
     * for the post like "profilePicUri" and "username" and
     * updates the prismPost objects in that hashMap and then
     * updates the RecyclerViewAdapter so the UI gets updated
     */
    private void populateUserDetailsForAllPosts(boolean updateRecyclerViewAdapter) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (PrismPost post : mainFeedPrismPostArrayList) {
                        DataSnapshot userSnapshot = dataSnapshot.child(post.getUid());
                        PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                        post.setPrismUser(prismUser);
                    }
                    mainFeedSwipeRefreshLayout.setRefreshing(false);

                    // gets called inside refreshData()
                    if (updateRecyclerViewAdapter) {
                        mainFeedProgressBar.setVisibility(View.GONE);
                        mainFeedRecyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.i(Default.TAG_DB, Message.NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Default.TAG_DB, Message.FETCH_USER_DETAILS_FAIL, databaseError.toException());
            }
        });
    }

}