package com.mikechoch.prism.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.MainActivity;
import com.mikechoch.prism.activity.PrismPostDetailActivity;
import com.mikechoch.prism.adapter.OptionRecyclerViewAdapter;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.Setting;


public class ProfileFragment extends Fragment {

    private CardView viewProfileCardView;
    private RecyclerView settingsRecyclerView;
    private TextView userFullNameTextView;
    private TextView viewProfileTextView;
    private TextView viewProfileAnalyticsTextView;
    private TextView viewProfileAnalyticsTrendsTextView;
    private ImageView userProfileImageView;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        viewProfileCardView = view.findViewById(R.id.profile_fragment_view_profile_card_view);
        settingsRecyclerView = view.findViewById(R.id.profile_fragment_settings_recycler_view);
        userFullNameTextView = view.findViewById(R.id.profile_fragment_user_full_name_text_view);
        viewProfileTextView = view.findViewById(R.id.profile_fragment_view_profile_text_view);
        viewProfileAnalyticsTextView = view.findViewById(R.id.profile_fragment_analytics_text_view);
        viewProfileAnalyticsTrendsTextView = view.findViewById(R.id.profile_fragment_analytics_description_text_view);
        userProfileImageView = view.findViewById(R.id.profile_fragment_user_profile_image_view);

        setupInterfaceElements();

        return view;
    }

    /**
     * Populate the current user card view, which will display user's full name and profile picture
     * When clicked it will take the user to the PrismUser profile activity
     */
    private void populateCurrentUserCardView() {
        userFullNameTextView.setText(CurrentUser.prismUser.getFullName());
        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(CurrentUser.prismUser.getProfilePicture().getLowResProfilePicUri())
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(userProfileImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        if (getActivity() != null && CurrentUser.prismUser != null) {
                            int imageViewPadding = (int) (1 * Default.scale);
                            RoundedBitmapDrawable profilePictureDrawable =
                                    BitmapHelper.createCircularProfilePicture(
                                            getActivity(),
                                            userProfileImageView,
                                            CurrentUser.prismUser.getProfilePicture().isDefault(),
                                            resource,
                                            imageViewPadding);
                            userProfileImageView.setImageDrawable(profilePictureDrawable);
                        }
                    }
                });

        viewProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToUserProfileActivity(getActivity(), CurrentUser.prismUser);
            }
        });
    }

    /**
     * Setup the profile fragment settings recycler view
     * Uses the Setting enum values as a data set
     */
    private void setupProfileSettingsRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        settingsRecyclerView.setLayoutManager(linearLayoutManager);
        OptionRecyclerViewAdapter settingsRecyclerViewAdapter = new OptionRecyclerViewAdapter(getActivity(), Setting.values());
        settingsRecyclerView.setAdapter(settingsRecyclerViewAdapter);
    }

    /**
     * Setup elements of current fragment
     */
    private void setupInterfaceElements() {
        userFullNameTextView.setTypeface(Default.sourceSansProLight);
        viewProfileTextView.setTypeface(Default.sourceSansProLight);
        viewProfileAnalyticsTextView.setTypeface(Default.sourceSansProLight);
        viewProfileAnalyticsTrendsTextView.setTypeface(Default.sourceSansProLight);

        populateCurrentUserCardView();
        setupProfileSettingsRecyclerView();
    }

}
