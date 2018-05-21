package com.mikechoch.prism.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mikechoch.prism.activity.PrismUserProfileActivity;
import com.mikechoch.prism.adapter.SettingsOptionRecyclerViewAdapter;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;

/**
 * Created by mikechoch on 1/22/18.
 */

public class ProfileFragment extends Fragment {

    /*
     * Globals
     */
    private FirebaseAuth auth;
    private DatabaseReference userReference;
    
    private CardView viewProfileCardView;
    private RecyclerView settingsRecyclerView;
    private TextView userFullNameTextView;
    private TextView viewProfileTextView;
    private CardView viewProfileAnalyticsCardView;
    private TextView viewProfileAnalyticsTextView;
    private TextView viewProfileAnalyticsTrendsTextView;
    private ImageView userProfileImageView;

    public static final ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        userReference = Default.USERS_REFERENCE.child(auth.getCurrentUser().getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        // Initialize all UI elements
        viewProfileCardView = view.findViewById(R.id.profile_fragment_view_profile_card_view);
        settingsRecyclerView = view.findViewById(R.id.profile_fragment_settings_recycler_view);
        userFullNameTextView = view.findViewById(R.id.profile_fragment_user_full_name_text_view);
        viewProfileTextView = view.findViewById(R.id.profile_fragment_view_profile_text_view);
        viewProfileAnalyticsCardView = view.findViewById(R.id.profile_fragment_analytics_card_view);
        viewProfileAnalyticsTextView = view.findViewById(R.id.profile_fragment_analytics_text_view);
        viewProfileAnalyticsTrendsTextView = view.findViewById(R.id.profile_fragment_analytics_description_text_view);
        userProfileImageView = view.findViewById(R.id.profile_fragment_user_profile_image_view);

        viewProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prismUserProfileIntent = new Intent(getActivity(), PrismUserProfileActivity.class);
                prismUserProfileIntent.putExtra("PrismUser", CurrentUser.prismUser);
                getActivity().startActivity(prismUserProfileIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        viewProfileAnalyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        settingsRecyclerView.setLayoutManager(linearLayoutManager);
        SettingsOptionRecyclerViewAdapter settingsRecyclerViewAdapter = new SettingsOptionRecyclerViewAdapter(getActivity());
        settingsRecyclerView.setAdapter(settingsRecyclerViewAdapter);

        setupUIElements();
        populateUserDetails();

        return view;
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        // Setup Typefaces for all text based UI elements
        userFullNameTextView.setTypeface(Default.sourceSansProLight);
        viewProfileTextView.setTypeface(Default.sourceSansProLight);
        viewProfileAnalyticsTextView.setTypeface(Default.sourceSansProLight);
        viewProfileAnalyticsTrendsTextView.setTypeface(Default.sourceSansProLight);

    }

    private void populateUserDetails() {
        userFullNameTextView.setText(CurrentUser.prismUser.getFullName());
        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(CurrentUser.prismUser.getProfilePicture().lowResUri)
                .apply(new RequestOptions().fitCenter())
                .into(new BitmapImageViewTarget(userProfileImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        if (!CurrentUser.prismUser.getProfilePicture().isDefault) {
                            int whiteOutlinePadding = (int) (1 * Default.scale);
                            userProfileImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                            userProfileImageView.setBackground(getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                        } else {
                            userProfileImageView.setPadding(0, 0, 0, 0);
                            userProfileImageView.setBackground(null);
                        }

                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        drawable.setCircular(true);
                        userProfileImageView.setImageDrawable(drawable);
                    }
                });
    }

}
