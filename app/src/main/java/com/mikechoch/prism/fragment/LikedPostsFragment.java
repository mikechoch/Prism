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
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.user_interface.InterfaceAction;
import com.mikechoch.prism.user_interface.PrismPostStaggeredGridRecyclerView;

import java.util.ArrayList;


public class LikedPostsFragment extends Fragment {

    private NestedScrollView likedPostsNestedScrollView;
    private LinearLayout userLikedPostsLinearLayout;

    private PrismPostStaggeredGridRecyclerView prismPostStaggeredGridRecyclerView;


    public static LikedPostsFragment newInstance() {
        return new LikedPostsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.liked_posts_fragment_layout, container, false);

        likedPostsNestedScrollView = view.findViewById(R.id.liked_posts_nested_scroll_view);
        userLikedPostsLinearLayout = view.findViewById(R.id.current_user_liked_posts_linear_layout);

        setupInterfaceElements();

        return view;
    }

    /**
     *
     * @return
     */
    public PrismPostStaggeredGridRecyclerView getPrismPostStaggeredGridRecyclerView() {
        return prismPostStaggeredGridRecyclerView;
    }

    /**
     * Setup the users liked posts recycler view
     * If no posts have been liked, show the no liked posts text and icon
     */
    private void setupLikedRecyclerViewColumns() {
        ArrayList<PrismPost> userLikedPosts = CurrentUser.getUserLikes();
        if (userLikedPosts != null && userLikedPosts.size() > 0) {
            prismPostStaggeredGridRecyclerView = new PrismPostStaggeredGridRecyclerView(getActivity(), userLikedPostsLinearLayout, userLikedPosts);
        } else {
            View noPostsView = LayoutInflater.from(getActivity()).inflate(R.layout.no_posts_user_profile_layout, null);

            Drawable noPostsDrawable = getResources().getDrawable(R.drawable.no_liked_posts_icon);
            ImageView noPostsImageView = noPostsView.findViewById(R.id.no_posts_image_view);
            noPostsImageView.setImageDrawable(noPostsDrawable);

            TextView noPostsTextView = noPostsView.findViewById(R.id.no_posts_text_view);
            noPostsTextView.setTypeface(Default.sourceSansProLight);
            noPostsTextView.setText(Message.NO_LIKED_POSTS);

            userLikedPostsLinearLayout.addView(noPostsView);
        }
    }

    /**
     * Setup elements in current fragment
     */
    private void setupInterfaceElements() {
        setupLikedRecyclerViewColumns();
    }

}
