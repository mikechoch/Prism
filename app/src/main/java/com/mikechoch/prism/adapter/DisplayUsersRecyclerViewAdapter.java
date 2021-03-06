package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;


public class DisplayUsersRecyclerViewAdapter extends RecyclerView.Adapter<DisplayUsersRecyclerViewAdapter.DisplayUsersViewHolder> {

    private Context context;
    private ArrayList<PrismUser> prismUserArrayList;


    public DisplayUsersRecyclerViewAdapter(Context context, ArrayList<PrismUser> prismUserArrayList) {
        this.context = context;
        this.prismUserArrayList = prismUserArrayList;
    }

    @NonNull
    @Override
    public DisplayUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DisplayUsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.users_recycler_view_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayUsersViewHolder holder, int position) {
        holder.setData(prismUserArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return prismUserArrayList.size();
    }


    public class DisplayUsersViewHolder extends RecyclerView.ViewHolder {

        private PrismUser prismUser;
        private RelativeLayout userRelativeLayout;
        private ImageView userProfilePicture;
        private TextView usernameTextView;
        private TextView userFullNameText;
        private Button userFollowButton;


        DisplayUsersViewHolder(View itemView) {
            super(itemView);

            userRelativeLayout = itemView.findViewById(R.id.display_user_relative_layout);
            userProfilePicture = itemView.findViewById(R.id.user_profile_picture_image_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            userFullNameText = itemView.findViewById(R.id.full_name_text_view);
            userFollowButton = itemView.findViewById(R.id.small_follow_user_button);
        }

        /**
         * Set data for the DisplayUsersViewHolder interface elements
         * @param prismUser - PrismUser to populate the current DisplayUsersViewHolder elements
         */
        public void setData(PrismUser prismUser) {
            this.prismUser = prismUser;
            populateInterfaceElements();
        }

        /**
         * If the user RelativeLayout is clicked, intent to the profile of the user
         */
        private void setupUserRelativeLayout() {
            userRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, prismUser);
                }
            });
        }

        /**
         * Setup follow button initial state and onClickListener
         * Handle toggling the follow button
         */
        private void setupUserFollowButton() {
            if (!Helper.isPrismUserCurrentUser(prismUser)) {
                userFollowButton.setVisibility(View.VISIBLE);

                InterfaceAction.toggleSmallFollowButton(context, CurrentUser.isFollowingPrismUser(prismUser), userFollowButton);

                userFollowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean performFollow = !CurrentUser.isFollowingPrismUser(prismUser);
                        InterfaceAction.handleFollowButtonClick(context, performFollow, userFollowButton, prismUser);
                    }
                });
            }
        }

        /**
         * Setup the userProfilePicImageView so it is populated with a Default or custom picture
         */
        private void setupUserProfilePicImageView() {
            if (prismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(prismUser.getProfilePicture().getLowResProfilePicUri())
                        .into(new BitmapImageViewTarget(userProfilePicture) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int imageViewPadding = (int) (1 * Default.scale);
                                RoundedBitmapDrawable profilePictureDrawable =
                                        BitmapHelper.createCircularProfilePicture(
                                                context,
                                                userProfilePicture,
                                                prismUser.getProfilePicture().isDefault(),
                                                resource,
                                                imageViewPadding);
                                userProfilePicture.setImageDrawable(profilePictureDrawable);
                            }
                        });
            }
        }

        /**
         * Setup the TextViews for LikeRepost item
         */
        private void setupUsernameAndFullNameTextView() {
            usernameTextView.setText(prismUser.getUsername());
            userFullNameText.setText(prismUser.getFullName());
        }

        /**
         * Setup elements for current adapter DisplayUsersViewHolder
         */
        private void populateInterfaceElements() {
            usernameTextView.setTypeface(Default.sourceSansProBold);
            userFullNameText.setTypeface(Default.sourceSansProLight);
            userFollowButton.setTypeface(Default.sourceSansProLight);

            setupUserProfilePicImageView();
            setupUsernameAndFullNameTextView();
            setupUserFollowButton();
            setupUserRelativeLayout();
        }
    }
}
