package com.mikechoch.prism.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


public class UploadedRepostedPostsFragment extends Fragment {

    private LinearLayout userUploadedPostsLinearLayout;

    private ArrayList<PrismPost> userUploadedAndRepostedPosts;
    private PrismPostStaggeredGridRecyclerView prismPostStaggeredGridRecyclerView;


    public static UploadedRepostedPostsFragment newInstance() {
        return new UploadedRepostedPostsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.uploaded_reposted_posts_fragment_layout, container, false);

        userUploadedPostsLinearLayout = view.findViewById(R.id.current_user_uploaded_posts_linear_layout);

        setupInterfaceElements();

        return view;
    }

    /**
     *
     * @param userUploadedAndRepostedPosts
     */
    public void setUserUploadedAndRepostedPosts(ArrayList<PrismPost> userUploadedAndRepostedPosts) {
        this.userUploadedAndRepostedPosts = userUploadedAndRepostedPosts;
    }

    /**
     *
     * @return
     */
    public PrismPostStaggeredGridRecyclerView getPrismPostStaggeredGridRecyclerView() {
        return prismPostStaggeredGridRecyclerView;
    }

    /**
     * Setup the users uploaded and reposted posts recycler view
     * If no posts have been uploaded/reposted, show the no uploaded/reposted posts text and icon
     */
    private void setupUploadedRepostedRecyclerViewColumns() {
        userUploadedPostsLinearLayout.removeAllViews();
        userUploadedPostsLinearLayout.setWeightSum((float) Default.POSTS_COLUMNS);
        ArrayList<PrismPost> userUploadedPosts = CurrentUser.getUserUploadsAndReposts();

        if (userUploadedPosts.size() > 0) {
            prismPostStaggeredGridRecyclerView = new PrismPostStaggeredGridRecyclerView(getActivity(), userUploadedPostsLinearLayout, userUploadedPosts);
        } else {
            View noPostsView = LayoutInflater.from(getActivity()).inflate(R.layout.no_posts_user_profile_layout, null, false);

            Drawable noPostsDrawable = getResources().getDrawable(R.drawable.no_uploaded_reposted_posts_icon);
            ImageView noPostsImageView = noPostsView.findViewById(R.id.no_posts_image_view);
            noPostsImageView.setImageDrawable(noPostsDrawable);

            TextView noPostsTextView = noPostsView.findViewById(R.id.no_posts_text_view);
            noPostsTextView.setTypeface(Default.sourceSansProLight);
            noPostsTextView.setText(Message.NO_UPLOADED_OR_REPOSTED_POSTS);

            userUploadedPostsLinearLayout.addView(noPostsView);
        }
    }

    /**
     * Setup elements for current fragment
     */
    private void setupInterfaceElements() {
        setupUploadedRepostedRecyclerViewColumns();
    }

}
