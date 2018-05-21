package com.mikechoch.prism.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.DisplayUsersActivity;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.NotificationType;

import java.util.ArrayList;

/**
 * Created by mikechoch on 1/21/18.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    /*
     * Global variables
     */
    private Context context;
    public static ArrayList<Notification> notificationArrayList;


    public NotificationRecyclerViewAdapter(Context context, ArrayList<Notification> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(notificationArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseAuth auth;
        private DatabaseReference postAuthorUserReference;
        private StorageReference storageReference;
        private DatabaseReference allPostsReference;

        private RelativeLayout notificationRelativeLayout;
        private ImageView userProfilePicImageView;
        private ImageView notificationTypeImageView;
        private LinearLayout notificationDescriptionLinearLayout;
        private TextView notificationTypeTextView;
        private ImageView prismPostThumbnailImageView;

        private Notification notification;


        public ViewHolder(View itemView) {
            super(itemView);

            // Cloud database initializations
            auth = FirebaseAuth.getInstance();
            postAuthorUserReference = Default.USERS_REFERENCE.child(auth.getCurrentUser().getUid());
            storageReference = Default.STORAGE_REFERENCE.child(Key.STORAGE_POST_IMAGES_REF);
            allPostsReference = Default.ALL_POSTS_REFERENCE;

            notificationRelativeLayout = itemView.findViewById(R.id.notification_relative_layout);
            userProfilePicImageView = itemView.findViewById(R.id.notification_item_prism_profile_image_view);
            notificationTypeImageView = itemView.findViewById(R.id.notification_item_type_image_view);
            notificationDescriptionLinearLayout = itemView.findViewById(R.id.notification_item_description_linear_layout);
            notificationTypeTextView = itemView.findViewById(R.id.notification_item_type_text_view);
            prismPostThumbnailImageView = itemView.findViewById(R.id.notification_item_post_thumbnail_image_view);
        }

        /**
         * Set data for the ViewHolder UI elements
         */
        public void setData(Notification notificationObject) {
            this.notification = notificationObject;
            populateUIElements();
        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
            // Setup Typefaces for all text based UI elements
            notificationTypeTextView.setTypeface(Default.sourceSansProLight);

            notificationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (notification.getType()) {
                        case LIKE:
                        case REPOST:
                            Helper.intentToPrismPostDetailActivity(context, notification.getPrismPost(), null);
                            break;
                        case FOLLOW:
                            Helper.intentToUserProfileActivity(context, notification.getMostRecentUser());
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
         *
         */
        private void constructNotificationTextViews() {
            notificationDescriptionLinearLayout.removeAllViews();

            String mostRecentUsername = notification.getMostRecentUser().getUsername();
            TextView usernameTextView = new TextView(context);
            usernameTextView.setText(mostRecentUsername);
            usernameTextView.setTextSize(16);
            usernameTextView.setTextColor(Color.WHITE);
            usernameTextView.setTypeface(Default.sourceSansProLight);
            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.intentToUserProfileActivity(context, notification.getMostRecentUser());
                }
            });

            int otherCount = notification.getOtherUserCount();
            TextView userCountTextView = new TextView(context);
            if (otherCount > 0) {
                userCountTextView.setTextSize(16);
                userCountTextView.setTypeface(Default.sourceSansProLight);
                userCountTextView.setTextColor(Color.WHITE);
                userCountTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentToDisplayUsersActivity(notification.getType().getNotifUserDisplayCode());
                    }
                });
                if (otherCount == 1) {
                    String userCountString = " and " + String.valueOf(otherCount) + " other";
                    userCountTextView.setText(userCountString);
                } else {
                    String userCountString = " and " + String.valueOf(otherCount) + " others";
                    userCountTextView.setText(userCountString);
                }
            }
            notificationDescriptionLinearLayout.addView(usernameTextView);
            notificationDescriptionLinearLayout.addView(userCountTextView);
        }

        /**
         * Intent to DisplayUserActivity with the correct intentType code
         * @param displayUsersCode
         */
        private void intentToDisplayUsersActivity(int displayUsersCode) {
            Intent displayUsersIntent = new Intent(context, DisplayUsersActivity.class);
            displayUsersIntent.putExtra("UsersInt", displayUsersCode);
            displayUsersIntent.putExtra("UsersDataId", notification.getPrismPost().getPostId());
            context.startActivity(displayUsersIntent);
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        /**
         *
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
            }

            prismPostThumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.intentToPrismPostDetailActivity(context, notification.getPrismPost(), null);
                }
            });
        }

        /**
         *
         */
        private void populateProfilePic() {
            PrismUser mostRecentPrismUser = notification.getMostRecentUser();
            if (mostRecentPrismUser.getProfilePicture() != null) {
                Glide.with(context)
                        .asBitmap()
                        .thumbnail(0.05f)
                        .load(mostRecentPrismUser.getProfilePicture().lowResUri)
                        .into(new BitmapImageViewTarget(userProfilePicImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                if (!mostRecentPrismUser.getProfilePicture().isDefault) {
                                    int whiteOutlinePadding = (int) (1 * Default.scale);
                                    userProfilePicImageView.setPadding(whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding, whiteOutlinePadding);
                                    userProfilePicImageView.setBackground(context.getResources().getDrawable(R.drawable.circle_profile_picture_frame));
                                } else {
                                    userProfilePicImageView.setPadding(0, 0, 0, 0);
                                    userProfilePicImageView.setBackground(null);
                                }

                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                drawable.setCircular(true);
                                userProfilePicImageView.setImageDrawable(drawable);
                            }
                        });
            }

            userProfilePicImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.intentToUserProfileActivity(context, notification.getMostRecentUser());
                }
            });
        }
    }


}
