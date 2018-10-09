package com.mikechoch.prism.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.user_interface.PrismPostStaggeredGridRecyclerView;

import java.util.ArrayList;


public class LikedPostsFragment extends Fragment {

    private SwipeRefreshLayout likedPostsSwipeRefreshLayout;
    private NestedScrollView likedPostsNestedScrollView;
    private LinearLayout userLikedPostsLinearLayout;

    private int[] swipeRefreshLayoutColors = {R.color.colorAccent};


    public static LikedPostsFragment newInstance() {
        return new LikedPostsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liked_posts_fragment_layout, container, false);
        
        likedPostsSwipeRefreshLayout = view.findViewById(R.id.liked_posts_swipe_refresh_layout);
        likedPostsNestedScrollView = view.findViewById(R.id.liked_posts_nested_scroll_view);
        userLikedPostsLinearLayout = view.findViewById(R.id.current_user_liked_posts_linear_layout);

        setupInterfaceElements();

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
                //TODO: @Parth we need to refresh here
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
     * Setup all interface elements
     */
    private void setupInterfaceElements() {
        setupUploadedRepostedSwipeRefreshLayout();
        setupLikedRecyclerViewColumns();
    }

}
