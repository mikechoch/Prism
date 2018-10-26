package com.mikechoch.prism.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.NotificationType;

import java.util.ArrayList;


public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    private Context context;
    private ArrayList<com.mikechoch.prism.attribute.Notification> notificationArrayList;


    public NotificationRecyclerViewAdapter(Context context, ArrayList<com.mikechoch.prism.attribute.Notification> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setData(notificationArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout notificationRelativeLayout;
        private ImageView userProfilePicImageView;
        private ImageView notificationTypeImageView;
        private LinearLayout notificationDescriptionLinearLayout;
        private TextView notificationTypeTextView;
        private ImageView prismPostThumbnailImageView;

        private com.mikechoch.prism.attribute.Notification notification;


        NotificationViewHolder(View itemView) {
            super(itemView);

            notificationRelativeLayout = itemView.findViewById(R.id.notification_relative_layout);
            userProfilePicImageView = itemView.findViewById(R.id.notification_item_prism_profile_image_view);
            notificationTypeImageView = itemView.findViewById(R.id.notification_item_type_image_view);
            notificationDescriptionLinearLayout = itemView.findViewById(R.id.notification_item_description_linear_layout);
            notificationTypeTextView = itemView.findViewById(R.id.notification_item_type_text_view);
            prismPostThumbnailImageView = itemView.findViewById(R.id.notification_item_post_thumbnail_image_view);
        }

        /**
         * Set data the data for the current NotificationViewHolder elements
         */
        public void setData(com.mikechoch.prism.attribute.Notification notificationObject) {
            this.notification = notificationObject;
            populateInterfaceElements();
        }

        /**
         * Populate elements in current NotificationViewHolder
         */
        private void populateInterfaceElements() {
            // Setup Typefaces for all text based UI elements
            notificationTypeTextView.setTypeface(Default.sourceSansProLight);
            notificationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (notification.getType()) {
                        case LIKE:
                            // Fall through
                        case REPOST:
                            IntentHelper.intentToPrismPostDetailActivity(context, notification.getPrismPost());
                            break;
                        case FOLLOW:
                            IntentHelper.intentToUserProfileActivity(context, notification.getMostRecentUser());
                            break;
                        default:
                            break;
                    }
                }
            });

            constructNotificationTextViews();
            populateNotificationInfoFields();
            populateProfilePic();

            prismPostThumbnailImageView.setImageDrawable(null);
            if (!notification.getType().equals(NotificationType.FOLLOW)) {
                populatePrismPostThumbnail();
            }
        }

        /**
         * Populates the notificationDescriptionLinearLayout, notificationTypeTextView, and notificationTypeImageView
         * notificationDescriptionLinearLayout gets the user and # of others who performed the action
         * notificationTypeTextView gets the notification type and time since the action occurred
         * notificationTypeImageView gets the icon of the NotificationType
         */
        private void populateNotificationInfoFields() {
            String notificationTypeAndTime = notification.getType().toString() + " • " + Helper.getFancyDateDifferenceString(notification.getActionTimestamp());
            notificationTypeTextView.setText(notificationTypeAndTime.toLowerCase());

            notificationTypeImageView.setImageDrawable(context.getResources().getDrawable(notification.getType().getNotifIcon()));
        }

        /**
         * Construct the appropriate notification TextViews
         * Handles the string description of the user that performed the latest action and
         * how many others have also performed this action
         * ex. mikechoch and 2 others
         * This is generic enough to handle following, likes, or reposts
         */
        private void constructNotificationTextViews() {
            notificationDescriptionLinearLayout.removeAllViews();

            String mostRecentUsername = notification.getMostRecentUser().getUsername();
            TextView usernameTextView = new TextView(context);
            usernameTextView.setText(mostRecentUsername);
            usernameTextView.setTextSize(16);
            usernameTextView.setTextColor(Color.WHITE);
            Typeface notificationTypeface = notification.isViewed() ? Default.sourceSansProLight : Default.sourceSansProBold;
            usernameTextView.setTypeface(notificationTypeface);
            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.intentToUserProfileActivity(context, notification.getMostRecentUser());
                }
            });

            int otherCount = notification.getOtherUserCount();
            TextView userCountTextView = new TextView(context);
            if (otherCount > 0) {
                userCountTextView.setTextSize(16);
                userCountTextView.setTypeface(notificationTypeface);
                userCountTextView.setTextColor(Color.WHITE);
                userCountTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToDisplayUsersActivity(context, notification.getPrismPost().getPostId(), notification.getType().getNotifUserDisplayCode());
                    }
                });

                String userCountStringHead = " and " + String.valueOf(otherCount);
                String userCountStringTail = otherCount == 1 ? " other" : " others";
                String userCountString = userCountStringHead + userCountStringTail;
                userCountTextView.setText(userCountString);
            }
            notificationDescriptionLinearLayout.addView(usernameTextView);
            notificationDescriptionLinearLayout.addView(userCountTextView);
        }

        /**
         * Using Glide populate the PrismPost ImageView
         * Validates that the notification has a PrismPost attached to it
         * When clicked will intent the user to the PrismPostDetailActivity of the PrismPost
         */
        private void populatePrismPostThumbnail() {
            PrismPost notificationPost = notification.getPrismPost();
            if (notificationPost != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(notificationPost.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(prismPostThumbnailImageView);

                prismPostThumbnailImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToPrismPostDetailActivity(context, notification.getPrismPost());
                    }
                });
            }
        }

        /**
         * Using Glide populate the profile picture of the person who performed the notification action
         * Validates that the PrismUser has a profile picture attached
         * When profile picture ImageView is clicked intent to UserProfileActivity of PrismUser
         */
        private void populateProfilePic() {
            PrismUser mostRecentPrismUser = notification.getMostRecentUser();
            if (mostRecentPrismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(mostRecentPrismUser.getProfilePicture().getLowResProfilePicUri())
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                int imageViewPadding = (int) (1 * Default.scale);
                                RoundedBitmapDrawable profilePictureDrawable =
                                        BitmapHelper.createCircularProfilePicture(
                                                context,
                                                userProfilePicImageView,
                                                mostRecentPrismUser.getProfilePicture().isDefault(),
                                                resource,
                                                imageViewPadding);
                                userProfilePicImageView.setImageDrawable(profilePictureDrawable);
                            }
                        });

                userProfilePicImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.intentToUserProfileActivity(context, notification.getMostRecentUser());
                    }
                });
            }
        }
    }


}
