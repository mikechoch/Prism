package com.mikechoch.prism.fragment;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.user_interface.PrismPostStaggeredGridRecyclerView;

import java.util.ArrayList;

/**
 * Created by mikechoch on 1/22/18.
 */

public class LikedPostsFragment extends Fragment {

    /*
     * Globals
     */
    private SwipeRefreshLayout likedPostsSwipeRefreshLayout;
    private NestedScrollView likedPostsNestedScrollView;
    private LinearLayout userLikedPostsLinearLayout;

    private int[] swipeRefreshLayoutColors = {R.color.colorAccent};

    public static final LikedPostsFragment newInstance() {
        LikedPostsFragment likedPostsFragment = new LikedPostsFragment();
        return likedPostsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liked_posts_fragment_layout, container, false);
        
        likedPostsSwipeRefreshLayout = view.findViewById(R.id.liked_posts_swipe_refresh_layout);
        likedPostsNestedScrollView = view.findViewById(R.id.liked_posts_nested_scroll_view);
        userLikedPostsLinearLayout = view.findViewById(R.id.current_user_liked_posts_linear_layout);

        setupUIElements();

        return view;
    }

    /**
     *
     */
    private void setupUploadedRepostedSwipeRefreshLayout() {
        likedPostsSwipeRefreshLayout.setColorSchemeResources(swipeRefreshLayoutColors);
        likedPostsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                likedPostsSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     *
     */
    private void setupLikedRecyclerViewColumns() {
        ArrayList<PrismPost> userLikedPosts = CurrentUser.getUserLikes();
        if (userLikedPosts != null && userLikedPosts.size() > 0) {
            new PrismPostStaggeredGridRecyclerView(getActivity(), userLikedPostsLinearLayout, userLikedPosts);
        } else {
            View noPostsView = LayoutInflater.from(getActivity()).inflate(R.layout.no_posts_user_profile_layout, null, false);

            Drawable noPostsDrawable = getResources().getDrawable(R.drawable.no_liked_posts_icon);
            ImageView noPostsImageView = noPostsView.findViewById(R.id.no_posts_image_view);
            noPostsImageView.setImageDrawable(noPostsDrawable);

            TextView noPostsTextView = noPostsView.findViewById(R.id.no_posts_text_view);
            noPostsTextView.setTypeface(Default.sourceSansProLight);
            noPostsTextView.setText("No liked posts");

            userLikedPostsLinearLayout.addView(noPostsView);
        }
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupUploadedRepostedSwipeRefreshLayout();
        setupLikedRecyclerViewColumns();
    }

}
