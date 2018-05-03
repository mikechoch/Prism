package com.mikechoch.prism.fragment;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.PostsColumnRecyclerViewAdapter;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;

import java.util.ArrayList;

/**
 * Created by mikechoch on 1/22/18.
 */

public class UploadedRepostedPostsFragment extends Fragment {

    /*
     * Globals
     */
    private Typeface sourceSansProLight;
    private Typeface sourceSansProBold;

    private SwipeRefreshLayout uploadedRepostedPostsSwipeRefreshLayout;
    private LinearLayout userUploadedPostsLinearLayout;

    private int[] swipeRefreshLayoutColors = {R.color.colorAccent};


    public static final UploadedRepostedPostsFragment newInstance() {
        UploadedRepostedPostsFragment uploadedRepostedPostsFragment = new UploadedRepostedPostsFragment();
        return uploadedRepostedPostsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.uploaded_reposted_posts_fragment_layout, container, false);

        uploadedRepostedPostsSwipeRefreshLayout = view.findViewById(R.id.uploaded_reposted_posts_swipe_refresh_layout);
        userUploadedPostsLinearLayout = view.findViewById(R.id.current_user_uploaded_posts_linear_layout);

        setupUIElements();

        return view;
    }

    /**
     *
     */
    private void setupUploadedRepostedSwipeRefreshLayout() {
        uploadedRepostedPostsSwipeRefreshLayout.setColorSchemeResources(swipeRefreshLayoutColors);
        uploadedRepostedPostsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                uploadedRepostedPostsSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     *
     */
    private void setupUploadedRepostedRecyclerViewColumns() {
        userUploadedPostsLinearLayout.removeAllViews();
        userUploadedPostsLinearLayout.setWeightSum((float) Default.POSTS_COLUMNS);
//        ArrayList<PrismPost> userUploadedPosts = CurrentUser.getUserUploads();
        ArrayList<PrismPost> userUploadedPosts = CurrentUser.getUserUploadsAndReposts();

        if (userUploadedPosts.size() > 0) {
//        ArrayList<ArrayList<PrismPost>> userUploadedPostsArrayLists = new ArrayList<>(Collections.nCopies(userUploadedColumns, new ArrayList<>()));
            // TODO: figure out how to initialize an ArrayList of ArrayLists without using while loop inside of populating for-loop
            // TODO: sexify this
            ArrayList<ArrayList<PrismPost>> userUploadedPostsArrayLists = new ArrayList<>();
            for (int i = 0; i < userUploadedPosts.size(); i++) {
                while (userUploadedPostsArrayLists.size() != Default.POSTS_COLUMNS) {
                    userUploadedPostsArrayLists.add(new ArrayList<>());
                }
                userUploadedPostsArrayLists.get((i % Default.POSTS_COLUMNS)).add(userUploadedPosts.get(i));
            }

            for (int i = 0; i < Default.POSTS_COLUMNS; i++) {
                LinearLayout recyclerViewLinearLayout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams one_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                recyclerViewLinearLayout.setLayoutParams(one_params);

                RecyclerView currentUserUploadedPostsRecyclerView = (RecyclerView) LayoutInflater.from(getActivity()).inflate(R.layout.posts_recycler_view, null);
                LinearLayoutManager recyclerViewLinearLayoutManager = new LinearLayoutManager(getActivity());
                currentUserUploadedPostsRecyclerView.setLayoutManager(recyclerViewLinearLayoutManager);
                PostsColumnRecyclerViewAdapter recyclerViewAdapter = new PostsColumnRecyclerViewAdapter(getActivity(), userUploadedPostsArrayLists.get(i));
                currentUserUploadedPostsRecyclerView.setAdapter(recyclerViewAdapter);

                recyclerViewLinearLayout.addView(currentUserUploadedPostsRecyclerView);
                userUploadedPostsLinearLayout.addView(recyclerViewLinearLayout);
            }
        } else {
            View noPostsView = LayoutInflater.from(getActivity()).inflate(R.layout.no_posts_user_profile_layout, null, false);

            Drawable noPostsDrawable = getResources().getDrawable(R.drawable.no_uploaded_reposted_posts_icon);
            ImageView noPostsImageView = noPostsView.findViewById(R.id.no_posts_image_view);
            noPostsImageView.setImageDrawable(noPostsDrawable);

            TextView noPostsTextView = noPostsView.findViewById(R.id.no_posts_text_view);
            noPostsTextView.setTypeface(sourceSansProLight);
            noPostsTextView.setText("No uploaded or reposted posts");

            userUploadedPostsLinearLayout.addView(noPostsView);
        }
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupUploadedRepostedSwipeRefreshLayout();
        setupUploadedRepostedRecyclerViewColumns();
    }

}
