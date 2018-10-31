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
import com.mikechoch.prism.callback.fetch.OnFetchPrismPostsCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseRead;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;


public class MainFeedFragment extends Fragment {

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
                    CurrentUser.refreshUserProfile(getActivity());
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

        mainFeedPrismPostArrayList = new ArrayList<>();
        mainFeedRecyclerViewAdapter = new PrismPostRecyclerViewAdapter(getContext(), mainFeedPrismPostArrayList);
        mainFeedRecyclerView.setAdapter(mainFeedRecyclerViewAdapter);

        refreshData();
    }

    /**
     * Setup elements of current fragment
     */
    private void setupInterfaceElements() {
        noMainFeedTextView.setTypeface(Default.sourceSansProLight);

        setupMainFeedRefreshSwipeLayout();
        setupMainFeedRecyclerView();
    }

    /**
     *  Clears the data structure and pulls ALL_POSTS info again from cloud
     *  Queries the ALL_POSTS data sorted by the post timestamp and pulls n
     *  number of posts and loads them into an ArrayList of postIds and
     *  a HashMap of PrismObjects
     */
    private void refreshData() {
        DatabaseRead.fetchLatestPrismPosts(new OnFetchPrismPostsCallback() {
            @Override
            public void onSuccess(ArrayList<PrismPost> prismPosts) {
                noMainFeedRelativeLayout.setVisibility(View.GONE);
                mainFeedProgressBar.setVisibility(View.GONE);
                mainFeedSwipeRefreshLayout.setRefreshing(false);

                mainFeedPrismPostArrayList.clear();
                mainFeedPrismPostArrayList.addAll(prismPosts);
                mainFeedRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPrismPostsNotFound() {
                Log.i(Default.TAG_DB, Message.NO_DATA);
                noMainFeedRelativeLayout.setVisibility(View.VISIBLE);
                mainFeedSwipeRefreshLayout.setRefreshing(false);
                mainFeedProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                // TODO Log this
                e.printStackTrace();
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

        DatabaseRead.fetchMorePrismPosts(lastPostTimestamp, new OnFetchPrismPostsCallback() {
            @Override
            public void onSuccess(ArrayList<PrismPost> prismPosts) {
                mainFeedProgressBar.setVisibility(View.GONE);
                mainFeedSwipeRefreshLayout.setRefreshing(false);

                mainFeedPrismPostArrayList.addAll(prismPosts);
                mainFeedRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPrismPostsNotFound() {
                Log.i(Default.TAG_DB, Message.NO_DATA);
            }

            @Override
            public void onFailure(Exception e) {
                // TODO Log this
                e.printStackTrace();
            }
        });

    }

}